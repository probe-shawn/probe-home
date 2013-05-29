package xlive.method.sms.provider.rxtx;


import xlive.xServerConfig;
import xlive.xUtility;
import xlive.xWebInformation;
import xlive.method.*;
import xlive.xml.xXmlDocument;

import gnu.io.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream; 
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Element;

public class xSendTextMessageMethod extends xDefaultMethod{
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public Object process()throws xMethodException {
		File sms_directory=directoryResolve(getProperties("sms-store-directory"));
		if(!sms_directory.exists()) sms_directory.mkdirs();
	   	Element arguments_sms_node=(Element)getArguments("sms", XPathConstants.NODE);
	    boolean synchronize = true;
	    try{
	    	synchronize = !"true".equals(getArguments("asyn"));
	    }catch(Exception e){}
	    //
		String comp_code=getArguments("sms.comp-code");
		if(comp_code == null ||comp_code.trim().length() != 4){
			String server_code=xServerConfig.getServerCode();
			String[]splites=server_code.split("@");
			comp_code=splites[0];
		}
     	Date order_date=null;
    	try{
    		order_date=simpleDateFormat.parse(getArguments("sms.order-time"));
    		if(order_date.after(new Date()))synchronize=false;
    	}catch(Exception e){}
    	if(order_date==null) order_date= new Date();
    	//
    	String file_name=((synchronize)? "syn_" : "")+comp_code+"_"+simpleDateFormat.format(order_date)+".xml";
    	File file = new File(sms_directory, file_name);
    	int count = 1;
    	while(file.exists()){
    		file_name=((synchronize)? "syn_" : "")+comp_code+"_"+simpleDateFormat.format(order_date)+"_"+count+".xml";
    		file = new File(sms_directory, file_name);
    		++count;
    	}
    	///
    	Element comp_code_element=(Element)getArguments("sms.comp-code", XPathConstants.NODE);
    	if(comp_code_element != null && (comp_code_element.getTextContent()==null||comp_code_element.getTextContent().trim().length()!=4))
    		comp_code_element.setTextContent(comp_code);
    	Element message_id=(Element)getArguments("sms.message-id", XPathConstants.NODE);
    	if(message_id != null && (message_id.getTextContent()==null||message_id.getTextContent().trim().length()==0))
    		message_id.setTextContent(file_name);
    	Element create_time=(Element)getArguments("sms.create-time", XPathConstants.NODE);
    	if(create_time != null && (create_time.getTextContent()==null||create_time.getTextContent().trim().length()==0))
    		create_time.setTextContent(xUtility.formatDate());
    	Element status=(Element)getArguments("sms.status", XPathConstants.NODE);
    	if(status != null) status.setTextContent("1");
    	// cost
    	Element cost=(Element)getArguments("sms.cost", XPathConstants.NODE);
    	if(cost != null) cost.setTextContent("1");
    	///
    	FileOutputStream sms_output=null;
		try {
			sms_output= new FileOutputStream(file);
			new xXmlDocument().Transform(arguments_sms_node, sms_output);
			sms_output.close();
			sms_output=null;
		}catch(IOException ioe){
			ioe.printStackTrace();
			throw createMethodException("IOException", ioe.getLocalizedMessage());
		}catch(TransformerConfigurationException tcx){
	    	tcx.printStackTrace();
			throw createMethodException("TransformerConfigurationException", tcx.getLocalizedMessage());
	    }catch(TransformerException te){
	    	te.printStackTrace();
			throw createMethodException("TransformerException", te.getLocalizedMessage());
	    }finally{
	    	try{if(sms_output!=null)sms_output.close();}catch(Exception e){}
	    }
		try{
			processMethod("create-connection");
			String port_name=getQNameArguments("create-connection.port-name");
			port_name=(port_name==null||port_name.trim().length()==0)? "COM1":port_name;
			SerialPort serial_port=(SerialPort)getPropertiesObject("connection-instance");
			if(serial_port != null){
				String phone_number=getArguments("sms.phone-number");
				String message=getArguments("sms.message");
				int result=sendTextMessage(phone_number, message, serial_port);
				setReturnArguments("success-count", String.valueOf(result));
			}
		}finally{
			processMethod("close-connection");
		}
		return getServiceContext().doNextProcess();
	}
	public static int sendTextMessage(String phone_num,String msg,SerialPort serial_port){
		char crtl_z=26;//這個用來當ctrl_z的標記
		char c_0d=13;
		char c_0a=10;
		String write_cmd="AT+CMGS=";
		String read_cmd="AT+CMGL";
		String delete_cmd="AT+CMGD=";

		//just for test
		System.out.println("phone_number :"+phone_num+"\nmessge :"+msg);
		if(true) return 1;
		//
		//因為簡訊字數會影響發出的通數,本函式可自行拆字發出
        //若為正整數則代表成功發出幾通簡訊,若為0或負數,代表中途有誤,且在錯誤前發了幾通
	    int success_count=0;
		if(phone_num.length()!=10 || msg.length()==0 || msg.length()>750) return 0;
		//在此對phone_num做格式調整,例如0912345678要改成9021436587(兩兩交換位置)
		char[] t1=phone_num.toCharArray();
		char[] t2={t1[1],t1[0],t1[3],t1[2],t1[5],t1[4],t1[7],t1[6],t1[9],t1[8] };
		phone_num=String.valueOf(t2);
	    String send_msg[]=new String[10];
	    try {  
			InputStream in=serial_port.getInputStream();
			OutputStream out=serial_port.getOutputStream();
	    	
             //因為一則簡訊只及能發出75個字,所以拆字串,最多拆10個
			for(int i=0;i<=(msg.length()-1)/75;i++){
				if(msg.length()>75*(i+1))				
					 send_msg[i]=STR2UTF(msg.substring(i*75,(i+1)*75));
				else send_msg[i]=STR2UTF(msg.substring(i*75,msg.length()));
				//System.out.println(UTF2STR(send_msg[i]));
			//以下為發出unicode簡訊所指定的格式		
			 String final_str="0011000A81"+phone_num+"001AA7";
			 int str_bytes=send_msg[i].length()/2;
			 if(str_bytes>15)//16進位的轉換處理
				  final_str+=Integer.toHexString(str_bytes).toUpperCase()+send_msg[i];
			 else final_str+="0"+Integer.toHexString(str_bytes).toUpperCase()+send_msg[i];	 
			 String at_cmd=write_cmd+String.valueOf(final_str.length()/2-1)+String.valueOf(c_0d)+String.valueOf(c_0a);
			 final_str+=String.valueOf(crtl_z)+String.valueOf(c_0d)+String.valueOf(c_0a);
			 //
             out.write(at_cmd.getBytes()) ;//先送出at-cmd
         
                 byte[] buffer = new byte[1024];
                 int len =0;
                 String read_msg="";
                 long run_time=System.currentTimeMillis();
                     while ( len > -1 )//讀取回應
                     { 
                    	 Thread.sleep(200);
                    	
                     	len = in.read(buffer);
                     	
                       if(len>0)
                       {	  
                       String tmp=  new String(buffer,0,len);
                       read_msg=read_msg+tmp;
                       
                             for(int j=0;j<tmp.length();j++)
                     	        if(tmp.charAt(j)=='>')//若回應>符號,則送出簡訊字串
                     	        {   read_msg="";
                     	        	out.write(final_str.getBytes()) ;
                     	        	run_time=System.currentTimeMillis();
                     	        }	  
                       }
                       
                       if(read_msg.lastIndexOf("OK")>=0) 
              	          {
                    	   if(read_msg.lastIndexOf("+CMGS:")>=0)
                    	   {
                    	   len=-1;
              	           success_count++;
                    	   }
                    	   else
                    	   {len=-1;
                           i=10;//跳出for迴圈
                           success_count=success_count*(-1);
                          }
              	          }
                       else
                       if(read_msg.lastIndexOf("ERROR")>=0 ||System.currentTimeMillis()-run_time>30000)//若超過30秒則放棄
                          {len=-1;
                           i=10;//跳出for迴圈
                           success_count=success_count*(-1);
                          }
                     }//while迴圈
                     
			}//FOR迴圈
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            } 		 
		return success_count;
	}
    private static String STR2UTF(String s)
    {   
    	char hexDigit[] = {
		        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
		        'A', 'B', 'C', 'D', 'E', 'F'
		    };
        StringBuffer stringbuffer = new StringBuffer();
        int i = s.length();
        for(int j = 0; j < i; j++){
            char c = s.charAt(j);
            stringbuffer.append(hexDigit[c >> 12 & 0xf]);
            stringbuffer.append(hexDigit[c >> 8 & 0xf]);
            stringbuffer.append(hexDigit[c >> 4 & 0xf]);
            stringbuffer.append(hexDigit[c >> 0 & 0xf]);
        }
        return stringbuffer.toString();
    }
	
	
}
