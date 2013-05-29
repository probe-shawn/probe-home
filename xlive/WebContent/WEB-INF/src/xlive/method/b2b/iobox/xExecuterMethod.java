package xlive.method.b2b.iobox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import xlive.xServiceContext;
import xlive.xUtility;
import xlive.xWebInformation;
import xlive.method.*;

public class xExecuterMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		return synchronized_process();
	}
	public synchronized Object synchronized_process() throws xMethodException{
	    String outbox_directory=getProperties("outbox-directory");
	    File outbox_file=directoryResolve(outbox_directory);
	    String inbox_directory=getProperties("inbox-directory");
	    File inbox_file=directoryResolve(inbox_directory);
	    String executable_regex=getArguments("executable-regex");
		Pattern pattern = Pattern.compile(executable_regex); 
		//
		boolean valid=true;
		String why="";
	    File[] files = inbox_file.listFiles();
	    Element data = this.setReturnArguments("date", "");
		for(int i=0;i<files.length;++i){
			if(files[i].isDirectory()) continue;
			String name=files[i].getName();
			Matcher matcher=pattern.matcher(name);
			if(matcher.matches() && matcher.groupCount() > 0){
				String object_url=matcher.group(1);
				File result_file = new File(outbox_file, xUtility.postfixTimeStamp("return"+name));
				File running_file=new File(inbox_file, "running"+name);
				files[i].renameTo(running_file);
				data.appendChild(createElement("file", name));
				new executeThread(running_file, result_file, object_url).start();
			}
		}
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
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
				context = new xServiceContext(root);
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
			}
		}

	}

}
