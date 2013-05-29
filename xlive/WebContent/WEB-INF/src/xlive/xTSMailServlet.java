package xlive;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import xlive.method.logger.xLogger;
import xlive.method.rst.finance.xValidMailData;
import javax.mail.Message;
import javax.mail.Transport;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
@SuppressWarnings("serial")
public class xTSMailServlet extends HttpServlet {
  public static boolean isGAE(){
	  String env = System.getProperty("com.google.appengine.runtime.environment"); 
	  return (env != null);
  }
  public void init(ServletConfig config) throws ServletException {
      super.init(config);
  }
  public void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	  super.doPut(req,res);
	  return;
  }
  public void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	  super.doDelete(req, res);
	  return;
  }
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      doPost(req, res); 
	  return;
  }
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  String test="mail process start"+"\n";
	  Properties props = new Properties(); 
      Session session = Session.getDefaultInstance(props, null); 
      try {
		MimeMessage message = new MimeMessage(session, request.getInputStream());
		
		Address[] adds =message.getFrom();
		for(int i =0; adds != null && i <adds.length;++i){
			test+="from addr :"+adds[i].toString()+"\n";
		}
		Address[] ccs=message.getRecipients(MimeMessage.RecipientType.CC);
		for(int i =0;ccs != null && i < ccs.length;++i){
			test+="cc addr :"+ccs[i].toString()+"\n";
		}
		Address[] tos=message.getRecipients(MimeMessage.RecipientType.TO);
		for(int i =0; tos != null &&  i < tos.length;++i){
			test+="to addr :"+tos[i].toString()+"\n";
		}
		test+="message.getSubject() :"+message.getSubject()+"\n";
		test+="message.getContentType() :"+message.getContentType()+"\n";
		
	   if(message.getContentType().indexOf("multipart")==-1){		  
		   test+="result:textmail"+"\n";
		   xLogger.log(Level.WARNING,test);
		   mailToAdmin(message,"textmail");
		   return ;
	   }
		   
		Multipart content = (Multipart)message.getContent();
		if(content != null)	test+="content.getCount() :"+content.getCount()+"\n";		
		int filecount=0;
		
		for(int i = 0; i < content.getCount();++i){
			BodyPart part = content.getBodyPart(i);			
			test+="part"+(i+1)+"\n";
			test+="part.getContentType() :"+part.getContentType()+"\n";
			test+="filename:"+part.getFileName()+"\n";	
			
			if(part.getFileName()!=null){						
				if(part.getFileName().indexOf("_JLIV")>=0 && part.getFileName().length()==17)
					processTSMail( message, part);	
				else 
					if(part.getFileName().indexOf("86719712-20680100076825")>=0 && part.getFileName().length()==40)
						processTSMail( message, part);	
					else
						filecount++;
				filecount++;				
			}
		}
		//如果附檔不是1個,代表非正常,記下並通知系統管理者		
		if(filecount!=1){					 
			 test+="result:file error"+"\n";	
			 xLogger.log(Level.WARNING, test);
			 mailToAdmin(message,"file error");
		}else{
			 test+="result:ok"+"\n";
			 xLogger.log(Level.WARNING, test);
		}		
		
	} catch (Exception e) {
		xLogger.log(Level.WARNING,"Servlet Exception:"+"\n"+ test+"\n"+"err msg:"+e.getMessage());
	}

  }
  public String getServletInfo() {
        return "xlive.xTSMailservlet Information";
  }
  
  public boolean processTSMail(MimeMessage message,BodyPart part){  //有關台新的銷帳檔處理	  
	  //建一筆記錄，內含一個銷帳檔（區分超商，虛擬帳戶）
	  //掛一個TASK處理這一筆銷帳檔
	  //預設是超商繳款
	  String filename="";
	  String str="";
	  String test="";
	  BufferedReader br=null;
	  try{
		  int type=1;//預設是超商繳款
		  filename=part.getFileName();
		  if(filename.length()==40)type=2;
		  test+="filename="+filename+"\n";
		  test+="size="+part.getSize()+"\n";
		  List<String> records =new ArrayList<String>();
		  InputStream in=part.getInputStream();
		  ByteArrayOutputStream bufferStream=new ByteArrayOutputStream(1024);
		  int data=0,c=0;
		  test+="before while"+"\n";
		  try{
			  
			  while((data = in.read()) > -1){
				  c++;
				  test+="("+data+")";  
				  if(data==13)continue;
				  if(data==10){	
					  test+="\nget a word"+"\n";
					  str=bufferStream.toString();
					  test+="str="+str+"\n";
					  test+="len="+str.length()+"\n";
					  if((type==1 && str.length()==67) || (type==2 && str.length()==125))
						  records.add(str);  
					  bufferStream.reset();
					  continue;
				  }
				  if(data==0)data=32;
				  bufferStream.write((byte) data);
			  }
		  }catch(IOException e){
			  xLogger.log(Level.WARNING,"read/total="+c+"/"+part.getSize()+"\n"+e.getMessage());
		  }
		  test+="after while"+"\n";
		  str=bufferStream.toString();
		  test+="last word="+str+"\n";
		  test+="last len="+str.length()+"\n";
		  if((type==1 && str.length()==67) || (type==2 && str.length()==125))
			  records.add(str);  
		  
		  if(records.isEmpty()){
			  xLogger.log(Level.WARNING,"no valid record!"+"\n"+test);
			  return true;	
		  }
		  xLogger.log(Level.WARNING,"get valid record!"+"\n"+test);
		  DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		  Entity entity =null;		  
		  xValidMailData md=null;
		  entity = new Entity(xValidMailData.class.getSimpleName());
		  md = new xValidMailData(entity);
		  md.setDate(new Date());
		  md.setLast(new Date());
		  md.setFileName(filename);
		  md.setType(String.valueOf(type));
		  md.setFrom(message.getFrom()[0].toString());
		  md.setRecords(records);
		  md.setQTY(records.size());
		  md.setDoneFlag("0");
		  md.setRemark(message.getSubject());			
		  ds.put(md.entity);
		  Queue queue = QueueFactory.getDefaultQueue();
		  TaskOptions to = TaskOptions.Builder.withUrl("/web/rst/finance");
			to.param("method", "process-income");
			to.param("key", KeyFactory.keyToString(md.entity.getKey()) );
			queue.add(to);
	  }catch (Exception e) {
		  xLogger.log(Level.WARNING,"Process Exception"+"\n"+test+"error msg="+e.getMessage());
		  mailToAdmin(message,"Process Exception");
		}
	  
	  return true;
  }
 
   
  public void mailToAdmin(MimeMessage message,String remark){	  
	  //有狀況時通知系統管理者
	  
	  Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      try {       	
          Message msg = new MimeMessage(session);               
          msg.setSubject("imall mail error:"+remark);
          msg.setFrom(new InternetAddress("probe.imall@gmail.com", "iMall"));
          msg.setRecipient(Message.RecipientType.TO, new InternetAddress("probe.steven@gmail.com"));
          String txt="";
          Address[] adds =message.getFrom();
	  		for(int i =0; adds != null && i <adds.length;++i){
	  			txt+="from addr :"+adds[i].toString()+"\n";
	  		}
         
          txt+="mail subject:"+message.getSubject()+"\n";
          Multipart mp = new MimeMultipart();
          MimeBodyPart htmlPart = new MimeBodyPart();
          htmlPart.setContent(txt, "text/html");
          mp.addBodyPart(htmlPart);
          msg.setContent(mp);
          Transport.send(msg);
      } catch (Exception e) {          
    	  xLogger.log(Level.WARNING,"mailtoAdmin Exception:"+"\n"+ remark+"\n"+"err msg:"+e.getMessage());
      }
    
	  	  
  }
  
}

 
