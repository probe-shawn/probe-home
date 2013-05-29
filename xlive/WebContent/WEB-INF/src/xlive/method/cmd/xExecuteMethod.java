package xlive.method.cmd;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.w3c.dom.Element;

import xlive.xUtility;
import xlive.method.xDefaultMethod;
import xlive.method.xMethodException;

public class xExecuteMethod  extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why="";
		String command=getArguments("command");
		boolean wait_for="true".equalsIgnoreCase(getArguments("wait-for"));
		if(command.trim().length()==0){
			why = "command empty";
		}else{
			try{
				Process process=Runtime.getRuntime().exec(command);
				Element output=setReturnArguments("output","");
				Element error=setReturnArguments("error","");
				streamThread output_thread=new streamThread(output, process.getInputStream());
				streamThread error_thread=new streamThread(error, process.getErrorStream());
				output_thread.start();
				error_thread.start();
				if(wait_for)process.waitFor();
				setReturnArguments("exit-value",String.valueOf(process.exitValue()));
			}catch(Exception e){
				valid=false;
				why=e.getMessage();
			}
		}
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
	class streamThread extends Thread{
		private Element data;
		private InputStream inputStream;
		streamThread(Element data, InputStream input){
			this.data=data;
			inputStream=input;
		}
		public void run()
		{
			try {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				InputStreamReader isr = new InputStreamReader(inputStream);
				BufferedReader br = new BufferedReader(isr);
				String line=null;
				while((line = br.readLine()) != null){
					output.write(line.getBytes());
				}
				if(output.size()>0)data.setTextContent(output.toString());
			}catch (IOException ioe){
				ioe.printStackTrace(); 
				data.setTextContent(ioe.getMessage());
			}
		}

	}
	
}
