package xlive.method.b2b.iobox.google;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Element;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import xlive.google.ds.xFile;
import xlive.method.*;

public class xExecuterMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		return synchronized_process();
	}
	public synchronized Object synchronized_process() throws xMethodException{
	    String outbox_directory=getProperties("outbox-directory");
	    outbox_directory=this.resourceDirectoryConvert(outbox_directory);
	    String inbox_directory=getProperties("inbox-directory");
	    inbox_directory=this.resourceDirectoryConvert(inbox_directory);
	    String executable_regex=getArguments("executable-regex");
		Pattern pattern = Pattern.compile(executable_regex); 
		//
		boolean valid=true;
		String why="";
	    Element data = this.setReturnArguments("date", "");
	    List<xFile> files = new xFile(inbox_directory).list();
	    Iterator<xFile> it = files.iterator();
	    while(it.hasNext()){
	    	xFile file = it.next();
	    	if(file.isDirectory()) continue;
	    	String name=file.getName();
			Matcher matcher=pattern.matcher(name);
			if(matcher.matches() && matcher.groupCount() > 0){
				String object_url=matcher.group(1);
				//file.renameTo(inbox_directory+"running"+name,true);
				data.appendChild(createElement("file", name));
				String url = "/web/"+this.getObjectPath("/");
				Queue queue = QueueFactory.getDefaultQueue();
			    queue.add(TaskOptions.Builder.withUrl(url).param("method", "task").param("object-url", object_url).param("file-name", file.getName()));
			}
	    }
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
	/*
	class executeThread extends Thread{
		private File executeFile;
		private File resultFile;
		private String objectURL;
		executeThread(File execute_file,File result_file, String object_url){
			super();
			executeFile=execute_file;
			resultFile=result_file;
			objectURL=object_url;
		}
		public void run(){
			FileOutputStream output_stream=null;
			xServiceContext context=null;
			boolean valid=true;
			try{
				Element root=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(executeFile)).getDocumentElement();
				context = xWebInformation.createServiceContext("request",root);
				xWebInformation.dispatch(context, objectURL);
			}catch(Exception e){
				valid=false;
				e.printStackTrace();
				try{
					File fail_file = new File(executeFile.getParentFile(), xUtility.postfixTimeStamp("fail_"+executeFile.getName()));
					xUtility.copyFile(executeFile, fail_file);
				}catch(Exception eall){}
			}finally{
				try{
					if(!valid||(context != null && !context.isValid())){
						String invalid = resultFile.getName();
						int index=invalid.lastIndexOf(".");
						if(index>=0) invalid=invalid.substring(0,index)+"_invalid"+invalid.substring(index);
						resultFile=new File(resultFile.getParent(), invalid);
					}
					output_stream = new FileOutputStream(resultFile); 
					context.responseToOutputStream(output_stream);
					output_stream.close();
					output_stream=null;
					executeFile.delete();
				}catch(Exception e){e.printStackTrace();}
				try{if(output_stream != null) output_stream.close();}catch(Exception e){}
				try{if(context != null) xWebInformation.detroyContextSession(context.getSessionId());}catch(Exception e){e.printStackTrace();}
			}
		}
	}
	*/
}
