package xlive.method.rst.barter;

import java.util.ArrayList;

import org.w3c.dom.Element;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import xlive.method.*;
import xlive.xml.xXmlDocument;

public class xRejectLoveMeMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String my_love_keystr=this.getArguments("my-love-key");
		String love_me_keystr=this.getArguments("love-me-key");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ArrayList<Entity> put_array = new ArrayList<Entity>();
		xBarter my_love_barter=null;
		xBarter love_me_barter=null;
		try {
			Entity my_love_entity = ds.get(KeyFactory.stringToKey(my_love_keystr));
	    	my_love_barter = new xBarter(my_love_entity);
			Entity love_me_entity=ds.get(KeyFactory.stringToKey(love_me_keystr));
			love_me_barter = new xBarter(love_me_entity);
			if(my_love_keystr.equals(love_me_barter.getILove()))love_me_barter.setILove(null);
			my_love_barter.removeLoveMe(love_me_keystr);
	    	put_array.add(my_love_entity);
	    	put_array.add(love_me_entity);
			ds.put(put_array);
		} catch (EntityNotFoundException e) {
		}
		if(valid && my_love_barter != null){
			Element data=this.setReturnArguments("data", "");
			data.appendChild(xBarter.barterXml(my_love_barter,false));
			
			try{
				xBarter barter = new xBarter(my_love_barter.entity);
				String data_str=xXmlDocument.nodeToString(xBarter.barterXml(barter,false));
				StringBuffer msg =  new StringBuffer();
				msg.append("<message>")
					.append("<target>barter</target>")
					.append("<op>set</op>")
					.append("<data>").append(data_str).append("</data>")
				.append("</message>");
				xlive.method.channel.xSendMessageMethod.sendMessage(this.getArguments("client-id"), null, null, null,barter.getOwnerFid(), msg.toString());
			}catch(Exception e){}
			try{
				xBarter barter = new xBarter(love_me_barter.entity);
				String data_str=xXmlDocument.nodeToString(xBarter.barterXml(barter,false));
				StringBuffer msg =  new StringBuffer();
				msg.append("<message>")
					.append("<target>barter</target>")
					.append("<op>set</op>")
					.append("<data>").append(data_str).append("</data>")
				.append("</message>");
				xlive.method.channel.xSendMessageMethod.sendMessage(this.getArguments("client-id"), null, null, null,barter.getOwnerFid(), msg.toString());
			}catch(Exception e){}
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
