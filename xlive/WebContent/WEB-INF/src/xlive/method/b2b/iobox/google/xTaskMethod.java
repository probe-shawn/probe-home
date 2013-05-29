package xlive.method.b2b.iobox.google;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import xlive.xServiceContext;
import xlive.xUtility;
import xlive.xWebInformation;
import xlive.google.ds.xFile;
import xlive.method.*;

public class xTaskMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
	    String outbox_directory=getProperties("outbox-directory");
	    outbox_directory=this.resourceDirectoryConvert(outbox_directory);
	    String inbox_directory=getProperties("inbox-directory");
	    inbox_directory=this.resourceDirectoryConvert(inbox_directory);
		//
		boolean valid=true;
		String why="";
		String object_url=this.getArguments("object-url");
		String file_name=this.getArguments("file-name");
		xFile exe_file = new xFile(inbox_directory+file_name);
		if(!exe_file.exists()){
			valid = false;
			setReturnArguments("valid", valid ? "true":"false");
			setReturnArguments("why", inbox_directory+file_name+"  not found!");
			return getServiceContext().doNextProcess();
		}
		if(exe_file.getLength() == 0){
			valid = false;
			setReturnArguments("valid", valid ? "true":"false");
			setReturnArguments("why", "execute file content empty !");
			return getServiceContext().doNextProcess();
		}
		exe_file.renameTo(inbox_directory+"running_"+file_name, true);
		xServiceContext context=null;
		try{
			ByteArrayInputStream bais = new ByteArrayInputStream(exe_file.getBytes());
			Element root=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bais).getDocumentElement();
			context = new xServiceContext(root);
			xWebInformation.dispatch(context, object_url);
		}catch(Exception e){
			valid=false;
			e.printStackTrace();
			try{
				String fail_name=inbox_directory+xUtility.postfixTimeStamp("fail_"+file_name);
				xFile fail_file = new xFile(fail_name);
				fail_file.setBytes(exe_file.getBytes());
			}catch(Exception eall){}
		}finally{
			try{
				xFile result_file = null;
				if(!valid||(context != null && !context.isValid())){
					String invalid = file_name;
					int index=invalid.lastIndexOf(".");
					if(index>=0) invalid=invalid.substring(0,index)+"_invalid"+invalid.substring(index);
					result_file=new xFile(outbox_directory+invalid);
				}else{
					result_file= new xFile(outbox_directory+xUtility.postfixTimeStamp("return_"+file_name));
				}
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				context.responseToOutputStream(baos);
				result_file.setBytes(baos.toByteArray());
				exe_file.delete();
			}catch(Exception e){e.printStackTrace();}
		}
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
