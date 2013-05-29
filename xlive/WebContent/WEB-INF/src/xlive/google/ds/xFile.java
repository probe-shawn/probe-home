package xlive.google.ds;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;
   
public class xFile {
	private Entity entity;
	private boolean existed=false;
    public xFile(Entity entity){
    	this.entity=entity;
    	existed=true;
    }
	public xFile(String fname){
    	String[] pathname=xFile.splitPathName(fname);
    	String path=pathname[0],name=pathname[1];
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	try {
			entity= ds.get(xFile.generateKey(path, name));
			existed=true;
		} catch (EntityNotFoundException e) {
			entity = new Entity(xFile.generateKey(path, name));
		}
    }
    public static String[] splitPathName(String fname){
    	if("/".equals(fname)) return new String[]{"","/"};
    	String[] names = fname.split("/");
    	String path="";
    	for(int i=0;i<names.length-1;++i) {
    		if(names[i].trim().length()==0) continue;
    		path = path+"/"+names[i];
    	}
    	if(path.length()==0) path="/";
    	return new String[]{path, names[names.length-1]};
    }
    public static Key generateKey(String path, String name){
    	String fname=path+(path.endsWith("/")?"":"/")+name;
    	return new KeyFactory.Builder("xFileObject", "/").addChild("xFileObject", fname).getKey();
    }
	public static xFile findFileObject(String path, String name){
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	try {
			 Entity found_entity= ds.get(xFile.generateKey(path, name));
			 return  new xFile(found_entity);
		} catch (EntityNotFoundException e) {
		}
		return null;
    }
	public boolean isRoot(){
		String name= this.getName(),path=this.getPath();
		return ("/".equals(name)&&path.length()==0);
	}
    public String getFileName(){
    	String name= this.getName(),path=this.getPath();
    	return (this.isRoot()) ? "/" : (path+(path.endsWith("/")?"":"/")+name);
    }
    public String getName(){
    	return (String) this.entity.getProperty("name");
    }
    public void setName(String name){
    	this.entity.setProperty("name",name);
    }
    public String getPath(){
    	return (String) this.entity.getProperty("path");
    }
    public void setPath(String path){
    	this.entity.setProperty("path",path);
    }
   public boolean exists(){
    	return this.existed;
    }
    public boolean isDirectory(){
    	Boolean dir = (Boolean)this.entity.getProperty("directory");
    	return dir != null ? dir.booleanValue() :false;
    }
    public void setDirectory(Boolean dir){
    	this.entity.setProperty("directory",dir);
    }
    public boolean isFile(){
    	return !this.isDirectory();
    }
    public long getLength(){
    	Long len = (Long) this.entity.getProperty("length");
    	return (len != null)? len.longValue() : 0;
    }
    public void setLength(long len){
    	this.entity.setProperty("length", Long.valueOf(len));
    }
    public Date getLastModified() {
    	return (Date) this.entity.getProperty("lastModified");
    } 
    public void setLastModified(Date last) {
    	this.entity.setProperty("lastModified",last);
    } 
    public Date getCreateDate() {
    	return (Date) this.entity.getProperty("createDate");
    }
    public void setCreateDate(Date create) {
    	this.entity.setProperty("createDate", create);
    }
    public void setBlobKey(Key key){
    	this.entity.setProperty("blobKey",key);
    }
    public Key getBlobKey(){
    	return (Key)this.entity.getProperty("blobKey");
    }
    
    public boolean setBytes(byte[] bytedata){
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Key blobkey = (Key)this.entity.getProperty("blobKey");
    	Entity blob_entity = null;
    	if(blobkey != null){
    		try {
				blob_entity = ds.get(blobkey);
			} catch (EntityNotFoundException e) {
				blob_entity = null;
			}
    	}
    	if(blob_entity == null){
    		blob_entity = new Entity("xBlobObject");
    	}
    	blob_entity.setProperty("blob", new Blob(bytedata));
    	ds.put(blob_entity);
    	this.setBlobKey(blob_entity.getKey());
		return true;
    }
    /*
    public boolean setBlobKey(BlobKey blob_key, long length){
		javax.jdo.PersistenceManager pm=xlive.google.xPMF.get().getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		boolean ok=true;
		BlobKey old_blob_key=null;
		try{
			tx.begin();
			if(!existed) ok &= this.makeParent(path,pm);
			if(ok) {
				xFileObject fo =null;
				try{
					fo = pm.getObjectById(xFileObject.class,xFile.generateKey(path, name));
					old_blob_key=fo.getBlobKey();
				}catch(JDOObjectNotFoundException e){
					fo = new xFileObject(path, name);
				}
				fo.setBlobKey(blob_key, length);
				try{
					pm.makePersistent(fo);
					this.existed=true;
					this.length=fo.getLength();
				}catch(Exception e){
					e.printStackTrace();
					ok =false;
				}
			}
			if(ok) {
				tx.commit();
			}
		}finally{
			if(tx.isActive())tx.rollback();
			pm.close();
		}
		if(ok && old_blob_key != null){ // blok_key, xfileobjectis different entity group  
	    	xBSF.blobStoreService().delete(old_blob_key);
		}
		return ok;
    }
    */
    public byte[] getBytes(){
    	Key blobkey=this.getBlobKey();
    	byte[] result=null;
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	try {
			Entity blob_entity = ds.get(blobkey);
			result = ((Blob) blob_entity.getProperty("blob")).getBytes();
		} catch (EntityNotFoundException e) {
		}
    	return result;
    }
    public boolean renameTo(String fname, boolean overwrite){
    	if(!existed) return false;
    	String[] pn=xFile.splitPathName(fname);
    	xFile old;
    	if((old=findFileObject(pn[0],pn[1])) != null && !overwrite) return false;
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	boolean ok = true;
    	try{
			ok &= this.makeParent(pn[0]);
			try{
				xFile xfile = new xFile(fname);
				xfile.setPath(pn[0]);
				xfile.setName(pn[1]);
				xfile.setDirectory(old.isDirectory());
				xfile.setBlobKey(old.getBlobKey());
				xfile.setCreateDate(old.getCreateDate());
				xfile.setLength(old.getLength());
				xfile.setLastModified(new Date());
				ds.put(xfile.entity);
				ds.delete(old.entity.getKey());
			}catch(Exception e){
				ok=false;
			}
    	}finally{
    	}
    	return ok;
    }
    public boolean delete(){
    	if(!existed || this.isDirectory()) return false;
     	boolean ok=true;
   	return ok;
    }
	public List<xFile> list(){
    	List<xFile> result = new ArrayList<xFile>();
        return result;
    }
    public boolean make(){
		boolean ok=true;
		return ok;
    }
    public boolean makeDirs(){
		boolean ok=true;
		return ok;
    }
    public boolean rd(){
    	List<xFile> list=this.list();
    	if(!list.isEmpty()) return false;
    	boolean ok=true;
    	return ok;
    }
	private boolean makeParent(String parent_path){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		boolean ok=true;
		String[] pn=xFile.splitPathName(parent_path);
		while(ok){
			xFile fo=xFile.findFileObject(pn[0], pn[1]);
			if(fo != null) return (fo.isDirectory() ? true : false);
			fo = new xFile(pn[0]+'/'+pn[1]);
			fo.setDirectory(true);
			ds.put(fo.entity);
			fo.existed=true;
			if("".equals(pn[0]) && "/".equals(pn[1])) break;
			pn=xFile.splitPathName(pn[0]);
		}
		return ok;
	}
}
