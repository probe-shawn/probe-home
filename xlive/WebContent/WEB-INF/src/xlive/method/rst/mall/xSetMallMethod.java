package xlive.method.rst.mall;

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
import com.google.appengine.api.datastore.KeyFactory;

import xlive.google.xMemCache;
import xlive.method.*;
import xlive.method.rst.barter.xBarter;
import xlive.method.rst.menu.xBom;
import xlive.method.rst.statistic.xGetMethod;

public class xSetMallMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String keystr=this.getArguments("mall.key");
		String fid=this.getArguments("mall.fid");
		
		Entity entity =null;
		xMallDetail mall_detail=null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(keystr != null && keystr.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(keystr));
			}catch(EntityNotFoundException e){entity=null;}
		}
		if(entity == null && fid != null && fid.trim().length()>0){
			try{
				entity = ds.get(xMallDetail.generateKey(fid));
			}catch(EntityNotFoundException e){entity = null;}
		}
		if(entity == null){
			entity = new Entity(xMallDetail.generateKey(fid));
			mall_detail = new xMallDetail(entity);
			mall_detail.setDate(new Date());
			mall_detail.setVisit(0);
			mall_detail.setTzOffset(this.getArguments("mall.tz-offset"));
			Entity head_entity = new Entity(xBom.class.getSimpleName());
			//
			String mall_type = this.getArguments("mall.mall-type");
			mall_detail.setStatus(("page".equals(mall_type)) ? 1 : 0);
			//
    	    xBom menu_main = new xBom(head_entity);
    	    menu_main.setId(fid);
    	    menu_main.setMallFid(fid);
    	    menu_main.setName("¥D¿ï³æ");
    	    menu_main.setType(xBom.bomDirectory);
	    	ds.put(menu_main.entity);
	    	mall_detail.setBomKey(KeyFactory.keyToString(menu_main.entity.getKey()));
    	    xMemCache.iMallService().increment(xGetMethod.mallCount, Long.valueOf(1));
    	    //sample
    	    if("page".equals(mall_type)){
	     	    xMall mall = new xMall(new Entity(xMall.generateKey(fid)));
	    	    mall.setBonus(100);  ///////// test
	    	    mall.setBonusRefund(0);
	    	    ds.put(mall.entity);
    	    }
    	    //
    	    if("account".equals(this.getArguments("mall.mall-type"))){
	    	    String name=this.getArguments("mall.name");
	    	    xSetMallMethod.createIOUBarter(fid,name);
	    	    xSetMallMethod.createIOUBarter(fid,name);
	    	    xSetMallMethod.createIOUBarter(fid,name);
	    	    xSetMallMethod.createIOUBarter(fid,name);
    	    }
		}else{
			mall_detail = new xMallDetail(entity);
			//long old_status=mall_detail.getStatus();
			//if(old_status >= 0){// normal
			//	mall_detail.setPause(this.getArguments("mall.pause"));
			//	mall_detail.setPauseMessage(this.getArguments("mall.pause-message"));
			//}
		}
		mall_detail.setId(this.getArguments("mall.id")); 
		mall_detail.setName(this.getArguments("mall.name"));
		
		mall_detail.setMallType(this.getArguments("mall.mall-type"));
		mall_detail.setType(this.getArguments("mall.type"));
		mall_detail.setDesc(this.getArguments("mall.desc"));
		mall_detail.setDescHtml(this.getArguments("mall.desc-html"));
		mall_detail.setFid(this.getArguments("mall.fid"));
		mall_detail.setLast(new Date());
		mall_detail.setPhone(this.getArguments("mall.phone"));
		mall_detail.setAddr(this.getArguments("mall.addr"));
		mall_detail.setNote(this.getArguments("mall.note"));
		mall_detail.setLatitude(this.getArguments("mall.latitude"));
		mall_detail.setLongitude(this.getArguments("mall.longitude"));
    	double lon = mall_detail.getLongitude();
    	double lat = mall_detail.getLatitude();
    	if(lon != 0 && lat != 0){
    		com.beoui.geocell.model.Point point = new com.beoui.geocell.model.Point();
    		point.setLat(lat);
    		point.setLon(lon);
    		List<String> geocell = com.beoui.geocell.GeocellManager.generateGeoCell(point);
    		mall_detail.entity.setProperty("geoCell", geocell);
    	}
		mall_detail.setStoreName(this.getArguments("mall.shop.store-name"));
		mall_detail.setOpenTime(this.getArguments("mall.shop.open-time"));
		mall_detail.setMail(this.getArguments("mall.shop.mail"));
		mall_detail.setBonusPercent(this.getArguments("mall.shop.bonus-percent"));
		mall_detail.setServiceType(this.getArguments("mall.shop.service-type"));
		mall_detail.setServiceMessage(this.getArguments("mall.shop.service-message"));
		mall_detail.setMenuBgIcon(this.getArguments("mall.shop.menu-bg-icon"));
		mall_detail.setMinOutgo(this.getArguments("mall.shop.fare.outgo.min-outgo"));
		mall_detail.setFareDelivery(this.getArguments("mall.shop.fare.delivery.fare-delivery"));
		mall_detail.setMaxDelivery(this.getArguments("mall.shop.fare.delivery.max-delivery"));
		//icons
		mall_detail.setMallBgIcon(this.getArguments("mall.mall-bg-icon"));
		Element icons_element=(Element)this.getArguments("mall.mall-icons", XPathConstants.NODE);
		if(icons_element != null){
			List<String> icons = new ArrayList<String>();
			NodeList node_list = (NodeList)icons_element.getElementsByTagName("mall-icon");
			for(int i = 0; i < node_list.getLength();++i){
				Element ele = (Element)node_list.item(i);
				String file=ele.getTextContent();
				if(file != null && file.trim().length() > 0) icons.add(ele.getTextContent());
			}
			mall_detail.setMallIcons(icons);
		}else mall_detail.setMallIcons(null);
		//
		mall_detail.setBarterBgIcon(this.getArguments("mall.barter.barter-bg-icon"));
		//
		ds.put(mall_detail.entity);
		/*
		*/
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
	public static void createIOUBarter(String fid, String name){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		xBarter barter = new xBarter(new Entity(xBarter.class.getSimpleName()));
		barter.setCreatorFid(fid);
		barter.setCreatorName(name);
		barter.setOwnerFid(fid);
		barter.setOwnerName(name);
		barter.setName(name+"'s IOU");
		barter.setRemoved(0);
		barter.setType(1);
		barter.setStatus(6);
		barter.setIcon("https://graph.facebook.com/"+fid+"/picture?type=large");
		barter.entity.setProperty("squDate", new Date());
		barter.entity.setProperty("createDate", new Date());
		ds.put(barter.entity);
	}
}
