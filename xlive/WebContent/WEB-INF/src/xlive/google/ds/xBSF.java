package xlive.google.ds;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;


public final class xBSF {
    private static final BlobstoreService blobService = BlobstoreServiceFactory.getBlobstoreService();
    private xBSF() {}
    public static BlobstoreService blobStoreService() {
        return blobService;
    }
}