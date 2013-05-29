package xlive.method.rst.circle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;

import xlive.method.*;

public class xSetCircleMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String fid=this.getArguments("circle.fid");
		
		Entity entity =null;
		xCircle circle=null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(entity == null && fid != null && fid.trim().length()>0){
			try{
				entity = ds.get(xCircle.generateKey(fid));
			}catch(EntityNotFoundException e){entity = null;}
		}
		if(entity == null){
			entity = new Entity(xCircle.generateKey(fid));
			circle = new xCircle(entity);
			circle.setDate(new Date());
			circle.setVisit(0);
			circle.setTzOffset(this.getArguments("circle.tz-offset"));
			circle.setFid(this.getArguments("circle.fid"));
		}else{
			circle = new xCircle(entity);
		}
		circle.setName(this.getArguments("circle.name"));
		circle.setDescHtml(this.getArguments("circle.desc-html"));
		circle.setLast(new Date());
		circle.setPhone(this.getArguments("circle.phone"));
		circle.setAddr(this.getArguments("circle.addr"));
		circle.setDescHtml(this.getArguments("circle.desc-html"));
		circle.setMap(this.getArguments("circle.map"));
		circle.setLatitude(this.getArguments("circle.latitude"));
		circle.setLongitude(this.getArguments("circle.longitude"));
    	double lon = circle.getLongitude();
    	double lat = circle.getLatitude();
    	if(lon != 0 && lat != 0){
    		com.beoui.geocell.model.Point point = new com.beoui.geocell.model.Point();
    		point.setLat(lat);
    		point.setLon(lon);
    		List<String> geocell = com.beoui.geocell.GeocellManager.generateGeoCell(point);
    		circle.entity.setProperty("geoCell", geocell);
    	}
    	circle.setMail(this.getArguments("circle.mail"));
    	circle.setIcon(this.getArguments("circle.icon"));
    	circle.setBgIcon(this.getArguments("circle.bg-icon"));
		//icons
		Element icons_element=(Element)this.getArguments("circle.icons", XPathConstants.NODE);
		if(icons_element != null){
			List<String> icons = new ArrayList<String>();
			NodeList node_list = (NodeList)icons_element.getElementsByTagName("icon");
			for(int i = 0; i < node_list.getLength();++i){
				Element ele = (Element)node_list.item(i);
				String file=ele.getTextContent();
				if(file != null && file.trim().length() > 0) icons.add(ele.getTextContent());
			}
			circle.setIcons(icons);
		}else circle.setIcons(null);
		circle.setApplyMessage(this.getArguments("circle.apply-message"));
		//
		ds.put(circle.entity);
		/*
		*/
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
}
