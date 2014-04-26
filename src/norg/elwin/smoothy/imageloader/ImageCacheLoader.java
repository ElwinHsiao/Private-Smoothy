package norg.elwin.smoothy.imageloader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * A image loader that can automatically cache your previous load task in memory and disk.  
 * @author Elwin.
 *
 */
public class ImageCacheLoader {
	private ImageCache mCache;
	private NetImageLoader mNetLoader;
	private Resources mResources;
	
	public ImageCacheLoader(Resources res) {
		mResources = res;
		mCache = new ImageCache();
		mNetLoader = new NetImageLoader();
	}
	
	/**
	 * Load the image asynchronous or synchronous.
	 * @param url
	 * @param listener
	 */
	public void loadImage(String url, OnImageLoadListener listener) {
		// check the memory cache
		if (loadFromMemory(url, listener)) {
			return;
		}
		
		// check the disk cache.
		if (hasDiskCache(url)) {
			loadFromDisk(url, listener);
		} else {	// download from network.
			loadFromNetwork(url, listener);
		}
	}

	/**
	 * Load image from memory cache.
	 * @param url
	 * @param listener
	 * @return true if success, false otherwise.
	 */
	private boolean loadFromMemory(String url, OnImageLoadListener listener) {
		BitmapDrawable bitmap = mCache.getMemCache(url);
		if (bitmap != null) {
			listener.onLoaded(url, bitmap);
			return true;
		}
		
		return false;
	}
	
	/**
	 * This method won't do a heavy work.
	 * @param url
	 * @return
	 */
	private boolean hasDiskCache(String url) {
		return mCache.hasDiskCache(url);
	}

	private void loadFromDisk(String url, OnImageLoadListener listener) {
		mCache.loadDiskImage(url, listener);
	}

	private void loadFromNetwork(String url, OnImageLoadListener listener) {
		WrappedOnImageLoadListener wrappedListener = new WrappedOnImageLoadListener(listener);
		mNetLoader.enqueueRequest(url, wrappedListener);
	}
	
	class WrappedOnImageLoadListener implements NetImageLoader.OnDownLoadListener {
		private OnImageLoadListener listener;
		public WrappedOnImageLoadListener(OnImageLoadListener listener) {
			this.listener = listener;
		}

		@Override
		public void onDownLoaded(String url, Bitmap bitmap) {
			BitmapDrawable bitmapDrawable = new BitmapDrawable(mResources, bitmap);
			addToCache(url, bitmapDrawable);
			this.listener.onLoaded(url, bitmapDrawable);
		}

		@Override
		public void onOverflowed(String url) {
			this.listener.onCanceled(url);
		}

		@Override
		public void onError(String url) {
			this.listener.onError(url);
		}

	}
	
	private void addToCache(String url, BitmapDrawable bitmapDrawable) {
		mCache.addCache(url, bitmapDrawable);				// add to cache
	}

	/**
	 * This listener use for notice the caller about the image loading message. 
	 * @author Elwin
	 *
	 */
	public interface OnImageLoadListener {
		/**
		 * When the image loaded.
		 * @param url
		 * @param bitmapDrawable
		 */
		void onLoaded(String url, BitmapDrawable bitmapDrawable);
		
		/**
		 * This load request was canceled for some reason, such as the request been overflow from the request queue and etc.
		 * @param url
		 */
		void onCanceled(String url);
		
		/**
		 * The loading process occurred some errors.
		 * @param url
		 */
		void onError(String url);
	}
}
