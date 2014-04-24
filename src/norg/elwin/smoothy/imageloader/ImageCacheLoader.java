package norg.elwin.smoothy.imageloader;

import android.graphics.drawable.BitmapDrawable;

/**
 * A image loader that can automatically cache your previous load task in memory and disk.  
 * @author Elwin.
 *
 */
public class ImageCacheLoader {
	private ImageCache mCache;
	private NetImageLoader mNetLoader;
	
	public ImageCacheLoader() {
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
	
	class WrappedOnImageLoadListener implements OnImageLoadListener {
		private OnImageLoadListener listener;
		public WrappedOnImageLoadListener(OnImageLoadListener listener) {
			this.listener = listener;
		}

		@Override
		public void onLoaded(String url, BitmapDrawable bitmapDrawable) {
			addToCache(url, bitmapDrawable);
			this.listener.onLoaded(url, bitmapDrawable);
		}

		@Override
		public void onCanceled(String url) {
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


}
