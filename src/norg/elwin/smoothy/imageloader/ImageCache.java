package norg.elwin.smoothy.imageloader;

import norg.elwin.smoothy.Utils;
import android.graphics.drawable.BitmapDrawable;
import android.util.LruCache;

/**
 * This class manage the cache in memory and disk for images.
 * @author Elwin
 *
 */
public class ImageCache {
	private static final String TAG = "ImageCache";

	private static final int MEM_CACHE_SIZE = 1024*1024*5;
	private LruCache<String, BitmapDrawable> mMemCache;
	
	public ImageCache() {
		mMemCache = new LruCache<String, BitmapDrawable>(MEM_CACHE_SIZE);
	}

	/**
	 * To speed performance, recommend run this method in UIThread.
	 * @param url
	 * @return
	 */
	public BitmapDrawable getMemCache(String url) {
		BitmapDrawable cacheObj = mMemCache.get(url);
		Utils.logd(TAG, "cache: url=" + url.substring(url.length()-15) + " hit=" + (cacheObj==null));
		return cacheObj;
	}
	
	/**
	 * Retrieve cache from disk, strongly recommend run this method in non-UIThread.
	 * @return
	 */
	public BitmapDrawable getDiskCache() {
		return null;
	}
	
	/**
	 * load the image from disk asynchronous.
	 * @param url
	 * @param listener
	 */
	public void loadDiskImage(String url, OnImageLoadListener listener) {
		
	}
	
	/**
	 * Add to both memory and disk cache.
	 * @param drawable
	 */
	public void addCache(String url, BitmapDrawable bitmap) {
		addCacheToMem(url, bitmap);
		addCacheToDisk(url, bitmap);
	}
	
	private void addCacheToMem(String url, BitmapDrawable bitmap) {
		mMemCache.put(url, bitmap);
	}
	
	private void addCacheToDisk(String url, BitmapDrawable bitmap) {
		
	}

	public boolean hasDiskCache(String url) {
		// TODO 
		return false;
	}
}
