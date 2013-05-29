package xlive.method.sys;

import java.io.FileNotFoundException;
import java.io.IOException;

import xlive.method.*;
import xlive.*;
import org.w3c.dom.*;
import java.io.*;

public class xRequestHtmlMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		String name=getArguments("name");
		String encoding=getArguments("encoding");
		if(encoding==null || encoding.trim().length() ==0) encoding=null;
		else encoding=encoding.trim();
		String web_dir=xWebInformation.getWebDirectory();
		Element return_node=setReturnArguments("data", "");
		FileInputStream fis=null;
		ByteArrayOutputStream baos=null;
        try {
        	baos=new ByteArrayOutputStream();
        	fis=new FileInputStream(web_dir+name);
            byte[] buf = new byte[10240];
            int bytes_read;
            while((bytes_read = fis.read(buf)) != -1) baos.write(buf, 0, bytes_read);
            return_node.appendChild(return_node.getOwnerDocument().createCDATASection((encoding !=null) ? baos.toString(encoding) :baos.toString()));
            fis.close();
            fis=null;
            baos.close();
            baos=null;
            return getServiceContext().doNextProcess();
        }catch(FileNotFoundException fnf){
        	fnf.printStackTrace();
        	throw this.createMethodException("FileNotFoundException", fnf.getLocalizedMessage());
        }catch(IOException ioe){
        	 ioe.printStackTrace();
         	throw this.createMethodException("IOException", ioe.getLocalizedMessage());
        }finally{
        	try{
        		if(fis != null) fis.close();
        		if(baos!=null) baos.close();
        	}catch(Exception e){}
        }
	}
}
