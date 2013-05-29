package xlive.method.schedule;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import xlive.xResourceManager;
import xlive.xServiceContext;
import xlive.xUtility;
import xlive.xWebInformation;
import xlive.method.*;
import xlive.method.logger.xLogger;
import xlive.xml.xXmlDocument;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class xGoogleTaskMethod extends xDefaultMethod {
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String job_path = this.getArguments("job-path");
		String job_name = this.getArguments("job-name");
		String pid = this.getArguments("pid");
		if(!this.taskExisted(pid, job_name)){
			this.setReturnArguments("valid", String.valueOf(valid));
			this.setReturnArguments("why", "task not found");
			return getServiceContext().doNextProcess();
		}
		Element job_element = null;
		try {
			job_element = new xXmlDocument().createDocument(xResourceManager.getResourceAsStream(job_path)).getDocumentElement();
		} catch (SAXParseException e) {
			valid = false;
			why = e.getMessage();
			e.printStackTrace();
		} catch (SAXException e) {
			valid = false;
			why = e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			valid = false;
			why = e.getMessage();
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			valid = false;
			why = e.getMessage();
			e.printStackTrace();
		}
		if(valid && job_element != null){
			XPath xp = XPathFactory.newInstance().newXPath();
			long next_time=Long.MAX_VALUE;
			Date current = new Date();
			long current_time=current.getTime();
			try{
				String stop=xp.evaluate(xWebInformation.xPathValidate("./stop"), job_element);
				if("true".equals(stop)){
					this.setReturnArguments("valid", String.valueOf(valid));
					this.setReturnArguments("why", "stop");
					return getServiceContext().doNextProcess();
				}
				boolean has_time_interval=false,has_date_pattern=false;
				long long_time_interval=0;
				String time_interval=xp.evaluate(xWebInformation.xPathValidate("./schedule-time-interval"), job_element);
				if(time_interval != null && time_interval.trim().length()> 0){
					try{long_time_interval=Long.parseLong(time_interval);}catch(Exception e){long_time_interval=0;}
					if(long_time_interval>0) has_time_interval=true;
				}
				String date_pattern=xp.evaluate(xWebInformation.xPathValidate("./schedule-date-pattern"), job_element);
				if(!has_time_interval && date_pattern != null && date_pattern.trim().length()> 0){
					has_date_pattern =true;
				}
				if(!has_time_interval && !has_date_pattern){
					this.setReturnArguments("valid", String.valueOf(valid));
					this.setReturnArguments("why", "time or date pattern misssing");
					return getServiceContext().doNextProcess();
				}
				Date start_date=xUtility.parseDate(xp.evaluate(xWebInformation.xPathValidate("./start-date"), job_element));
				Date end_date=xUtility.parseDate(xp.evaluate(xWebInformation.xPathValidate("./end-date"), job_element));
				if(end_date != null && end_date.before(current)) {
					this.setReturnArguments("valid", String.valueOf(valid));
					this.setReturnArguments("why", "expired");
					return getServiceContext().doNextProcess();
				}
				if(start_date==null) start_date=current;
				String on_server_start=xp.evaluate(xWebInformation.xPathValidate("./on-server-start"), job_element);
				if("true".equals(on_server_start)){
					next_time=current_time;
				}else{
					if(has_time_interval) next_time=current_time+long_time_interval;
					if(has_date_pattern){
						it.sauronsoftware.cron4j.Predictor predictor = new it.sauronsoftware.cron4j.Predictor(date_pattern,start_date);
						next_time=predictor.nextMatchingTime();
					}
				}
				if(end_date != null && end_date.before(new Date(next_time))){
					this.setReturnArguments("valid", String.valueOf(valid));
					this.setReturnArguments("why", "expired");
					return getServiceContext().doNextProcess();
				}
				job_element.setAttribute("nextTime", String.valueOf(next_time));
				if(has_time_interval) job_element.setAttribute("timeInterval", String.valueOf(long_time_interval));
			}catch(XPathExpressionException e){
				e.printStackTrace();
				valid = false;
				why = e.getMessage();
			}catch(Exception e){
				e.printStackTrace();
				valid = false;
				why = e.getMessage();
			}
			if(!valid || next_time <= 0){
				this.setReturnArguments("valid", String.valueOf(valid));
				this.setReturnArguments("why", why);
				return getServiceContext().doNextProcess();
			}
			if(valid && next_time <= current_time) { 
				xLogger.log(Level.FINE, null, "schedule", "run", "schedule.run :"+job_name,0);
				next_time = execute(job_element);
			}
			if(next_time > 0){
				long count_down = next_time-System.currentTimeMillis();
				count_down = (count_down <=0)? 100:count_down;
				Queue queue = QueueFactory.getDefaultQueue();
				String url = "/web/"+this.getObjectPath("/");
				TaskOptions options = TaskOptions.Builder.withUrl(url);
				options=options.param("method", "google-task");
				options=options.param("job-name", job_name);
				options=options.param("job-path", job_path);
				options=options.param("pid", pid);
				options=options.countdownMillis(count_down);
				options=options.header("jobname", job_name);
				options=options.header("pid", pid);
			    queue.add(options);
			}
		}else{
			this.setReturnArguments("valid", String.valueOf(valid));
			this.setReturnArguments("why", why);
		}
		return getServiceContext().doNextProcess();
	}
	private boolean taskExisted(String pid, String name){
		boolean found = false;
		/* ????????????????????????????????????????????????????????
  		javax.jdo.PersistenceManager pm= xlive.google.xPMF.get().getPersistenceManager();
  		Transaction tx=pm.currentTransaction();
    	try {
    		tx.begin();
    		try{
    			xTaskObject result=(xTaskObject) pm.getObjectById(xTaskObject.class, xTaskObject.generateKey(pid, name));
    			found = (result!=null);
    		}catch(JDOObjectNotFoundException e){
    		}
    	    tx.commit();
        } finally {
        	if(tx.isActive())tx.rollback();
            pm.close();
        }
        */
        return found;
	}
	private long execute(Element job_element){
		long next_time=0;
		try{
			XPath xp = XPathFactory.newInstance().newXPath();
			job_element.setAttribute("running", "true");
			String exe_time=job_element.getAttribute("nextTime");
			String object_name=xp.evaluate(xWebInformation.xPathValidate("./object-name"), job_element);
			String method_name=xp.evaluate(xWebInformation.xPathValidate("./method-name"), job_element);
			Node arguments=(Node)xp.evaluate(xWebInformation.xPathValidate("./arguments"), job_element, XPathConstants.NODE);
			//
			Element[] xlive_process_methods=xWebInformation.createElements("xlive.method");
			xlive_process_methods[xlive_process_methods.length-1].setAttribute("name", method_name);
			if(arguments != null){
				arguments=xlive_process_methods[xlive_process_methods.length-1].getOwnerDocument().adoptNode(arguments.cloneNode(true));
				try{
					Element arg_method_name_node=(Element)xp.evaluate("./"+method_name, arguments, XPathConstants.NODE);
					if(arg_method_name_node != null){
						xlive_process_methods[xlive_process_methods.length-1].appendChild(arguments);
					}else{
						arguments.getOwnerDocument().renameNode(arguments, null, method_name);
						xlive_process_methods[xlive_process_methods.length-1].appendChild(xWebInformation.createElement("arguments")).appendChild(arguments);
					}
				}catch(XPathExpressionException e){
					e.printStackTrace();
				}
			}
			xServiceContext context = new xServiceContext(xlive_process_methods[0]);
			try{
				xWebInformation.dispatch(context, object_name);
			}catch(Exception e){
				e.printStackTrace();
			}
			//
			String stop=xp.evaluate(xWebInformation.xPathValidate("./stop"), job_element);
			String run_once=xp.evaluate(xWebInformation.xPathValidate("./run-once"), job_element);
			Date current = new Date();
			Date end_date=xUtility.parseDate(xp.evaluate(xWebInformation.xPathValidate("./end-date"), job_element));
			if((end_date != null && end_date.before(current)) || "true".equals(stop) || "true".equals(run_once)) {
				next_time=0;
			}else{
				long long_time_interval=0;
				try{
					String tmp=job_element.getAttribute("timeInterval");
					if(tmp!=null && tmp.trim().length()>0)long_time_interval=Long.parseLong(tmp);
				}catch(Exception e){long_time_interval=0;}
				if(long_time_interval > 0){
					next_time = current.getTime()+long_time_interval;
				}else{
					String date_pattern=xp.evaluate(xWebInformation.xPathValidate("./schedule-date-pattern"), job_element);
					it.sauronsoftware.cron4j.Predictor predictor = new it.sauronsoftware.cron4j.Predictor(date_pattern,current);
					next_time=predictor.nextMatchingTime();
					if(next_time==Long.parseLong(exe_time)) next_time=predictor.nextMatchingTime();
				}
				if(end_date != null && end_date.before(new Date(next_time)))next_time=0;
			}
			job_element.setAttribute("nextTime", String.valueOf(next_time));
			job_element.setAttribute("running", "false");
		}catch(XPathExpressionException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			job_element.setAttribute("running", "false");
		}
		return next_time;
	}

}
