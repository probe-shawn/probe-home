package xlive.method.schedule;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import xlive.xProbeServlet;
import xlive.xResourceManager;
import xlive.xServiceContext;
import xlive.xUtility;
import xlive.xWebInformation;
import xlive.method.*;
import xlive.method.logger.xLogger;
import xlive.xml.xXmlDocument;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class xStartMethod extends xDefaultMethod implements Runnable{
	private static File jobsDirectory;
	private static boolean stopThread=false;
	private static Element jobs=null;
	private static LinkedList<String> waitQueue;
	private static Date waitDate=new Date();
	private static long waitTime=0;
	private static long idleWaitTime=60*1000;
	
	public Object process()throws xMethodException{
		if(xProbeServlet.isGAE()) {
			return getServiceContext().doNextProcess();
			//return gaeProcess();
		}
		//
		jobsDirectory=directoryResolve(getProperties("jobs-directory"));
		if(!jobsDirectory.exists()) jobsDirectory.mkdirs();
		if(waitQueue != null) return getServiceContext().doNextProcess();
		readJobs();
		waitQueue = new LinkedList<String>();
		new Thread(this).start();
		return getServiceContext().doNextProcess();
	}
	private Object gaeProcess() throws xMethodException{
		deleteAllTaskObjects();
		String pid = String.valueOf(System.currentTimeMillis());
		String jobs_directory = getProperties("jobs-directory");
		jobs_directory=this.resourceDirectoryConvert(jobs_directory);
		Set paths = xResourceManager.getResourcePaths(jobs_directory);
        if(paths==null) return getServiceContext().doNextProcess();
        Iterator it=paths.iterator();
        while(it.hasNext()){
        	String path=(String)it.next();
        	if(path.endsWith("/")|| !path.endsWith(".xml")) continue;
        	String[] names=path.split("/");
        	String name=names[names.length-1];
        	addTaskObject(pid, name);
			Queue queue = QueueFactory.getDefaultQueue();
			String url = "/web/"+this.getObjectPath("/");
			TaskOptions options = TaskOptions.Builder.withUrl(url);
			options=options.param("method", "google-task");
			options=options.param("job-name", name);
			options=options.param("job-path", path);
			options=options.param("pid", pid);
			options=options.countdownMillis(3000);
			options=options.header("jobname", name);
			options=options.header("pid", pid);
		    queue.add(options);
        }
		return getServiceContext().doNextProcess();
	}
    private boolean deleteAllTaskObjects(){
    	/* ??????????????????????????????????????????????????????????????
  		javax.jdo.PersistenceManager pm= xlive.google.xPMF.get().getPersistenceManager();
  		Transaction tx=pm.currentTransaction();
    	try {
    		tx.begin();
    		javax.jdo.Extent<xTaskObject> all = pm.getExtent(xTaskObject.class);
    		Iterator<xTaskObject> it = all.iterator();
    		while(it.hasNext()){
    			pm.deletePersistent(it.next());
    		}
    		//if(all.iterator().hasNext())pm.deletePersistentAll(all);
    	    tx.commit();
    	    return true;
        }catch(Exception e){
        	e.printStackTrace();
        }finally {
        	if(tx.isActive())tx.rollback();
            pm.close();
        }
        */
        return false;
    }
    private void addTaskObject(String pid, String name){
    	/* ??????????????????????????????????????????????
    	xTaskObject task = new xTaskObject(pid, name);
 		javax.jdo.PersistenceManager pm= xlive.google.xPMF.get().getPersistenceManager();
  		Transaction tx=pm.currentTransaction();
    	try {
    		tx.begin();
    		pm.makePersistent(task);
    	    tx.commit();
        }catch(Exception e){
        	e.printStackTrace();
        }finally {
        	if(tx.isActive())tx.rollback();
            pm.close();
        }
        */
    }
	public void cleanUp(){
		if(stopThread) super.cleanUp();
	}
	private void readJobs()throws xMethodException{
		jobs=createElement("jobs");
		File[] files=jobsDirectory.listFiles();
		for(int i=0; i<files.length;++i){
			if(files[i].isFile()&& files[i].getName().endsWith("xml")){
				try {
					Document xml_doc= new xXmlDocument().createDocument(files[i]);
					Element root=(Element)xml_doc.getDocumentElement();
					root.setAttribute("filePath", files[i].getAbsolutePath());
					root.setAttribute("fileName", files[i].getName());
					jobs.appendChild(jobs.getOwnerDocument().adoptNode(root));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (SAXParseException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void stopService(){
		stopThread=true;
		notifyService();
	}
	public static void notifyService(){
		try{
			synchronized(waitQueue){
				waitQueue.notify();
			}
		}catch(Exception e){}
	}
	public void run(){
		try{
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){}
			logMessage("schedule start service");
			while(!stopThread){
				Date current = new Date();
				long current_time=current.getTime();
				long min_next_time=Long.MAX_VALUE;
				XPath xp = XPathFactory.newInstance().newXPath();
				Vector<Element> drop = new Vector<Element>();
				Vector<Element> executor = new Vector<Element>();
				try{
					NodeList node_list=(NodeList)xp.evaluate(xWebInformation.xPathValidate("./job"), jobs, XPathConstants.NODESET);
					for(int i=0;i<node_list.getLength();++i){
						long next_time=Long.MAX_VALUE;
						Element node=(Element)node_list.item(i);
						String nexttime_string=node.getAttribute("nextTime");
						if(nexttime_string!=null && nexttime_string.trim().length()>0){
							try{
								next_time=Long.parseLong(nexttime_string);
							}catch(Exception e){
								drop.add(node);
								continue;
							}
						}else{
							try{
								String stop=xp.evaluate(xWebInformation.xPathValidate("./stop"), node);
								if("true".equals(stop)){
									drop.add(node);
									continue;
								}
								boolean has_time_interval=false,has_date_pattern=false;
								long long_time_interval=0;
								String time_interval=xp.evaluate(xWebInformation.xPathValidate("./schedule-time-interval"), node);
								if(time_interval != null && time_interval.trim().length()> 0){
									try{long_time_interval=Long.parseLong(time_interval);}catch(Exception e){long_time_interval=0;}
									if(long_time_interval>0) has_time_interval=true;
								}
								String date_pattern=xp.evaluate(xWebInformation.xPathValidate("./schedule-date-pattern"), node);
								if(!has_time_interval && date_pattern != null && date_pattern.trim().length()> 0){
									has_date_pattern =true;
								}
								if(!has_time_interval && !has_date_pattern){
									drop.add(node);
									continue;
								}
								Date start_date=xUtility.parseDate(xp.evaluate(xWebInformation.xPathValidate("./start-date"), node));
								Date end_date=xUtility.parseDate(xp.evaluate(xWebInformation.xPathValidate("./end-date"), node));
								if(end_date != null && end_date.before(current)) {
									drop.add(node);
									continue;
								}
								if(start_date==null) start_date=current;
								String on_server_start=xp.evaluate(xWebInformation.xPathValidate("./on-server-start"), node);
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
									drop.add(node);
									continue;
								}
								node.setAttribute("nextTime", String.valueOf(next_time));
								if(has_time_interval) node.setAttribute("timeInterval", String.valueOf(long_time_interval));
							}catch(XPathExpressionException e){
								e.printStackTrace();
								drop.add(node);
								continue;
							}catch(Exception e){
								e.printStackTrace();
								drop.add(node);
								continue;
							}
						}
						if(next_time <= 0){
							drop.add(node);
							continue;
						}
						if(next_time <= current_time) executor.add(node);
						else min_next_time=Math.min(next_time, min_next_time);
					}
				}catch(XPathExpressionException e){
					e.printStackTrace();
					throw createMethodException("computeNextTime", e.getLocalizedMessage());
				}catch(Exception e){
					e.printStackTrace();
					throw createMethodException("computeNextTime", e.getLocalizedMessage());
				}
				for(int i=0; i < drop.size();++i){
					Node node=(Node)drop.get(i);
					node.getParentNode().removeChild(node);
				}
				if(min_next_time==Long.MAX_VALUE)min_next_time=current_time+idleWaitTime;
				waitTime=min_next_time-current_time;
				if(waitTime <=0 ) waitTime=100;
				waitDate= new Date(current_time+waitTime);
				for(int i=0; i < executor.size();++i){
					Element node=(Element)executor.get(i);
					if("true".equals(node.getAttribute("running"))) continue;
					new executeThread(node).start();
 				}
				synchronized(waitQueue){
					waitQueue.wait(waitTime);
				}
				if(stopThread || xWebInformation.serviceStop()) break;
			}
			waitQueue=null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	class executeThread extends Thread{
		private Element jobElement;
		executeThread(Element job){
			super();
			jobElement=job;
		}
		public void run(){
			try{
				XPath xp = XPathFactory.newInstance().newXPath();
				String running=jobElement.getAttribute("running");
				if("true".equals(running)) return;
				jobElement.setAttribute("running", "true");
				//
				String exe_time=jobElement.getAttribute("nextTime");
				String object_name=xp.evaluate(xWebInformation.xPathValidate("./object-name"), jobElement);
				String method_name=xp.evaluate(xWebInformation.xPathValidate("./method-name"), jobElement);
				Node arguments=(Node)xp.evaluate(xWebInformation.xPathValidate("./arguments"), jobElement, XPathConstants.NODE);
				String file_name=jobElement.getAttribute("fileName");
				xLogger.log(Level.FINE, null, "schedule", "run", "schedule.run :"+file_name,0);
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
				String stop=xp.evaluate(xWebInformation.xPathValidate("./stop"), jobElement);
				String run_once=xp.evaluate(xWebInformation.xPathValidate("./run-once"), jobElement);
				Date current = new Date();
				Date end_date=xUtility.parseDate(xp.evaluate(xWebInformation.xPathValidate("./end-date"), jobElement));
				long next_time=0;
				if((end_date != null && end_date.before(current)) || "true".equals(stop) || "true".equals(run_once)) {
					next_time=0;
				}else{
					long long_time_interval=0;
					try{
						String tmp=jobElement.getAttribute("timeInterval");
						if(tmp!=null && tmp.trim().length()>0)long_time_interval=Long.parseLong(tmp);
					}catch(Exception e){long_time_interval=0;}
					if(long_time_interval > 0){
						next_time = current.getTime()+long_time_interval;
					}else{
						String date_pattern=xp.evaluate(xWebInformation.xPathValidate("./schedule-date-pattern"), jobElement);
						it.sauronsoftware.cron4j.Predictor predictor = new it.sauronsoftware.cron4j.Predictor(date_pattern,current);
						next_time=predictor.nextMatchingTime();
						if(next_time==Long.parseLong(exe_time)) next_time=predictor.nextMatchingTime();
					}
					if(end_date != null && end_date.before(new Date(next_time)))next_time=0;
				}
				jobElement.setAttribute("nextTime", String.valueOf(next_time));
				jobElement.setAttribute("running", "false");
				if(next_time < waitDate.getTime())	{
					try{
						synchronized(waitQueue){
							waitQueue.notify();
						}
					}catch(java.lang.IllegalMonitorStateException e){
						e.printStackTrace();
					}
				}
			}catch(XPathExpressionException e){
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				jobElement.setAttribute("running", "false");
			}

		}
	}
}
