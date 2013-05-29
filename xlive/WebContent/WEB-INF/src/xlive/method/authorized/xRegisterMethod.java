package xlive.method.authorized;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

import javax.xml.xpath.XPathConstants;
import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.google.ds.xFile;
import xlive.method.*;
import xlive.wrap.xWrapFileOutputStream;
import xlive.xml.xXmlDocument;

public class xRegisterMethod extends xDefaultMethod{
	private int patchUserKey=1000;
	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()) return gae_process();
		//////
		String directory=getProperties("userdata-directory");
		String user_id=getArguments("data.user-id");
		//String user_pass=getMethodArguments("data.user_pass");
		//
		File userdata_directory=directoryResolve(directory);
		File user_file= new File(userdata_directory,user_id+".xml");
		if(user_file.exists()){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "user id existed");
			return getServiceContext().doNextProcess(false);
		}
		//
		Node data_node=(Node)this.getArguments("data", XPathConstants.NODE);
		Element user_node=this.createElement("user");
		user_node.appendChild(data_node=data_node.cloneNode(true));
		String user_key=createUserKey();
		data_node.appendChild(createElement("user-key", user_key));
		//
		boolean valid=true;
		String why="";
		OutputStream fo=null;
		try {
			fo = xWrapFileOutputStream.fileOutputStream(user_file);
			new xXmlDocument().Transform(user_node, fo);
		}catch(Exception e){
			e.printStackTrace();
			valid=false;
			why=e.getMessage();
		}finally{
			try{
				if(fo != null)fo.close();
			}catch(Exception e){}
		}
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		setReturnArguments("data.user-key", user_key);
		return getServiceContext().doNextProcess();
	}
	private String createUserKey(){
		return String.valueOf(System.currentTimeMillis())+String.valueOf((++this.patchUserKey)%10000);
	}
	private Object gae_process() throws xMethodException{
		if(xProbeServlet.isGAE()) return gae_process();
		//////
		String userdata_directory=getProperties("userdata-directory");
		userdata_directory = this.resourceDirectoryConvert(userdata_directory);
		String user_id=getArguments("data.user-id");
		//String user_pass=getMethodArguments("data.user_pass");
		//
		xFile user_file= new xFile(userdata_directory+user_id+".xml");
		if(user_file.exists()){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "user id existed");
			return getServiceContext().doNextProcess(false);
		}
		//
		Node data_node=(Node)this.getArguments("data", XPathConstants.NODE);
		Element user_node=this.createElement("user");
		user_node.appendChild(data_node=data_node.cloneNode(true));
		String user_key=createUserKey();
		data_node.appendChild(createElement("user-key", user_key));
		//
		boolean valid=true;
		String why="";
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			new xXmlDocument().Transform(user_node, baos);
			user_file.setBytes(baos.toByteArray());
		}catch(Exception e){
			e.printStackTrace();
			valid=false;
			why=e.getMessage();
		}finally{
		}
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		setReturnArguments("data.user-key", user_key);
		return getServiceContext().doNextProcess();
	}

}
