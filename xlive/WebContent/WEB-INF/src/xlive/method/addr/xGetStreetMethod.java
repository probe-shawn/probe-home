package xlive.method.addr;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import xlive.method.xDefaultMethod;
import xlive.method.xMethodException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;

public class xGetStreetMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid = true;
		StringBuffer why= new StringBuffer();
		String city=this.getArguments("city");
		//city="台北市";
		String area=this.getArguments("area");
		//area="大安區";
		//String url=this.getArguments("url");
        String url="http://www.post.gov.tw/post/internet/f_searchzone/streetNameData.jsp";
        Element data=this.setReturnArguments("data","");
        try {
            url+="?"+"city="+URLEncoder.encode(city,"utf-8")+"&"+"cityarea="+URLEncoder.encode(area,"utf-8");
            System.out.println(url);
            Document doc=CallServlet(url,why) ;          
            data.appendChild(data.getOwnerDocument().adoptNode(doc.getDocumentElement()));
            setReturnArguments("valid", String.valueOf(true));              
        }catch (Exception e)  {
        	valid = false;
        	why.append(e.getMessage());
        }
  	   	setReturnArguments("valid", String.valueOf(valid));
  	   	setReturnArguments("why", why.toString());
  	   
        return this.getServiceContext().doNextProcess();
   }
   private Document CallServlet(String aURL, StringBuffer why){
       URL url = null;
       HttpURLConnection con = null;
       boolean bok = true;
       Document doc=null;
       try {
           DataOutputStream out = null;
           url = new URL(aURL);
           con = (HttpURLConnection)url.openConnection();
           con.setRequestMethod("POST");
           con.setDoInput(true);
           con.setDoOutput(true);
           con.setUseCaches(false);
           con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
           con.setRequestProperty("encoding", "utf-8");
           out = new DataOutputStream(con.getOutputStream());
           out.flush();
           out.close();
           out = null;
           InputStream in = new DataInputStream(con.getInputStream());
           try {
        	   //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	   //xUtility.copyStream(in, baos);
           	   doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
           }catch(SAXParseException e){
        	   bok=false;
        	   why.append(e.getMessage());
           }catch(SAXException e) {
      	       bok=false;
      	       why.append(e.getMessage());
           }catch(IOException e) {
        	   bok=false;
        	   why.append(e.getMessage());
   	       }catch(ParserConfigurationException e){
   	    	   bok=false;
   	    	   why.append(e.getMessage());
   	       }catch(Exception e){
   	    	   bok=false;
   	    	   why.append(e.getMessage());
   	       }
           in.close();
           con.disconnect();
           con = null;
           return (bok)? doc : null;
       }catch(SocketException e){
    	   bok = false;
    	   why.append(e.getMessage());
       }catch(MalformedURLException e) {
       	   bok = false;
       	   why.append(e.getMessage());
       }catch(IOException e) {
       	   bok = false;
       	   why.append(e.getMessage());
       }catch(Exception e) {
       	   bok = false;
       	   why.append(e.getMessage());
       }finally{
    	   try {if(con != null) con.disconnect();}catch(Exception econ){}
       }
       return null;
   }
   
   

	    
}
