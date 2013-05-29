package xlive.google;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public final class xMemCache {
    private static final MemcacheService sessionIdCache = MemcacheServiceFactory.getMemcacheService("sessionid");
    private static final MemcacheService loginDataCache = MemcacheServiceFactory.getMemcacheService("logindata");
    private static final MemcacheService propertiesCache = MemcacheServiceFactory.getMemcacheService("properties");
    private static final MemcacheService iMallCache = MemcacheServiceFactory.getMemcacheService("imall");
    private static final MemcacheService pngCache = MemcacheServiceFactory.getMemcacheService("png");
    private xMemCache() {}
    public static MemcacheService sessionIdService() { 
        return sessionIdCache;
    }  
    public static MemcacheService loginDataService() {
        return loginDataCache;
    }
    public static MemcacheService propertiesService() {
        return propertiesCache;
    }
    public static MemcacheService iMallService() {
        return iMallCache;
    }
    public static MemcacheService pngService() {
        return pngCache;
    }
}