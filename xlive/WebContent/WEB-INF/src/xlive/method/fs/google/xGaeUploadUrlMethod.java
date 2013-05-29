package xlive.method.fs.google;


import xlive.method.*;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;


public class xGaeUploadUrlMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		extendDefaultPropertiesToArguments();
		String handle_uri = "/_gaeupload?method=upload";
		handle_uri += "&work-directory="+this.getArguments("work-directory");
		handle_uri += "&prefix-file-name="+this.getArguments("prefix-file-name");
		handle_uri += "&gae-redirect="+"/web/"+this.getObjectPath("/");
		String gae_action=BlobstoreServiceFactory.getBlobstoreService().createUploadUrl(handle_uri);
		this.setReturnArguments("valid", "true");
		this.setReturnArguments("url",gae_action);
		return getServiceContext().doNextProcess();
	}
}
