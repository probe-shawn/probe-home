package xlive.method.rst.mall;
import java.util.ArrayList;
import java.util.List;
import xlive.method.*;
import org.w3c.dom.*;
import com.beoui.geocell.model.BoundingBox;
import com.beoui.geocell.model.DefaultCostFunction;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;


public class xGetLocalMallMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Element malls_element = this.setReturnArguments("malls", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService(); 
    	int last_call=0,map_size=0;
    	double lat1=-1,lon1=-1,lat2=-1,lon2=-1;
    	try{
	    	lat1=Double.parseDouble((String)this.getArguments("lat-1"));
	    	lon1=Double.parseDouble((String)this.getArguments("lon-1")); 
	    	lat2=Double.parseDouble((String)this.getArguments("lat-2"));
	    	lon2=Double.parseDouble((String)this.getArguments("lon-2")); 
	    	last_call=Integer.parseInt((String)this.getArguments("last-call"));	    	
    	}catch(Exception e){
    		e.printStackTrace();
    		valid = false;
    		why="lat,lon parseDouble error";
    	}
    	int count = 0;		
    	if(valid){
    		BoundingBox box = new BoundingBox(lat1,lon1,lat2,lon2);
    		/*
    		List<String> qcell = com.beoui.geocell.GeocellManager.bestBboxSearchCells(box, new CostFunction() {
    		        	public double defaultCostFunction(int numCells, int resolution) {
    		        			if(numCells > 100) return Double.MAX_VALUE;
    		        			else return 0;
    		        	}});
			*/
    		List<String> qcell = com.beoui.geocell.GeocellManager.bestBboxSearchCells(box, new DefaultCostFunction());  
    		map_size=qcell.get(0).length();
    		
    		if(map_size>=0){
	    		Query q = new Query(xMallDetail.class.getSimpleName());
	    		/*
	    		q.addFilter("geoCell", FilterOperator.IN, qcell);
	    		//q.addFilter("geoCell", FilterOperator.NOT_EQUAL, null);
	    		*/
	    		q.setFilter(new Query.FilterPredicate("geoCell", FilterOperator.IN, qcell));
	    		
	    		PreparedQuery pq = ds.prepare(q);
	    		for(Entity found : (Iterable<Entity>)pq.asIterable()) {
	    			Element mall_element=(Element)malls_element.appendChild(this.createElement("mall"));
	    			xMallDetail mall_detail = new xMallDetail(found);
			        mall_element.appendChild(this.createElement("fid")).setTextContent(mall_detail.getFid());
			        mall_element.appendChild(this.createElement("name")).setTextContent(mall_detail.getName());
			        mall_element.appendChild(this.createElement("desc")).setTextContent(mall_detail.getDesc());
			        mall_element.appendChild(this.createElement("store-name")).setTextContent(mall_detail.getStoreName());
			        mall_element.appendChild(this.createElement("phone")).setTextContent(mall_detail.getPhone());
			        mall_element.appendChild(this.createElement("addr")).setTextContent(mall_detail.getAddr());
			        mall_element.appendChild(this.createElement("latitude")).setTextContent(String.valueOf(mall_detail.getLatitude()));
			        mall_element.appendChild(this.createElement("longitude")).setTextContent(String.valueOf(mall_detail.getLongitude()));
			        ++count;
			    }
    		}
    		else{   //這一段暫時用不到 			
    			List<PreparedQuery> pqlist=new ArrayList<PreparedQuery>();
    			for(int i=0;i<qcell.size();i++){    				
    				Query q = new Query(xMallDetail.class.getSimpleName());
    				/*
    				q.addFilter("geoCell", FilterOperator.EQUAL, qcell.get(i));
    				*/
    				q.setFilter(new Query.FilterPredicate("geoCell", FilterOperator.EQUAL, qcell.get(i)));
    				PreparedQuery pq = ds.prepare(q);    				
    				pqlist.add(pq);    								
    			}
    			for(int i=0;i<qcell.size();i++){
    				Element area_element=(Element)malls_element.appendChild(this.createElement("area"));
    				int area_malls=pqlist.get(i).countEntities(FetchOptions.Builder.withLimit(999999));
    				area_element.appendChild(this.createElement("totmall")).setTextContent(String.valueOf(area_malls));
    				for(Entity found : (Iterable<Entity>)pqlist.get(i).asIterable(FetchOptions.Builder.withLimit(1))) {
    					xMallDetail mall_detail = new xMallDetail(found);
    					area_element.appendChild(this.createElement("fid")).setTextContent(mall_detail.getFid());
    					area_element.appendChild(this.createElement("name")).setTextContent(mall_detail.getName());
    					area_element.appendChild(this.createElement("desc")).setTextContent(mall_detail.getDesc());
    					area_element.appendChild(this.createElement("store-name")).setTextContent(mall_detail.getStoreName());
    					area_element.appendChild(this.createElement("phone")).setTextContent(mall_detail.getPhone());
    					area_element.appendChild(this.createElement("addr")).setTextContent(mall_detail.getAddr());
    					area_element.appendChild(this.createElement("latitude")).setTextContent(String.valueOf(mall_detail.getLatitude()));
    			        area_element.appendChild(this.createElement("longitude")).setTextContent(String.valueOf(mall_detail.getLongitude()));
    				}
    				
    				//BoundingBox bb=GeocellUtils.computeBox(qcell.get(i));
    				//double area_lat=(bb.getNorth()+bb.getSouth())/2;
    				//double area_lon=(bb.getEast()+bb.getWest())/2;
    			}
    		}
    	}
    	this.setReturnArguments("count", String.valueOf(count));
    	this.setReturnArguments("mapsize", "16");//現階段一律給到最小比例尺
    	this.setReturnArguments("lastcall", String.valueOf(last_call));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}
