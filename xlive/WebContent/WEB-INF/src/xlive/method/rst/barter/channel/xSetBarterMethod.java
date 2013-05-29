package xlive.method.rst.barter.channel;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import xlive.method.*;
import xlive.method.rst.barter.xBarter;
import xlive.xml.xXmlDocument;

public class xSetBarterMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String client_id=this.getArguments("client-id");
		String mall_fid=this.getArguments("mall-fid");
		String barter_key = this.getArguments("barter-key");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(barter_key != null && barter_key.trim().length()>0){
			try{
				xBarter barter = new xBarter(ds.get(KeyFactory.stringToKey(barter_key)));
				String data=xXmlDocument.nodeToString(xBarter.barterXml(barter,false));
				StringBuffer msg =  new StringBuffer();
				msg.append("<message>")
					.append("<target>barter</target>")
					.append("<op>set</op>")
					.append("<data>").append(data).append("</data>")
				.append("</message>");
				xlive.method.channel.xSendMessageMethod.sendMessage(client_id, null, null, null, mall_fid, msg.toString());
			}catch(EntityNotFoundException e){}
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
