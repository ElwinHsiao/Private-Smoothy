package norg.elwin.smoothy.imageloader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import norg.elwin.smoothy.Utils;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

/**
 * Handle all download requests.
 * @author Elwin
 *
 */
public class NetImageLoader {
	private static final int THREAD_POOL_SIZE = 1 + 5;		// one for dispatch request.
	private static final int DEFAULT_QUEUE_SIZE = 2;
	protected static final String TAG = null;
	private ExecutorService mExecutor;
	private OverflowStackSet<DownloadRequest> mQueue;
	private boolean mIsStarted;

	public NetImageLoader() {
		mExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		mQueue = new OverflowStackSet<DownloadRequest>(DEFAULT_QUEUE_SIZE);
	}

	public void enqueueRequest(String url, OnDownLoadListener listener) {
		startDispatchIfNeed();
		
		synchronized (mQueue) {
			DownloadRequest overFlowedItem = mQueue.push(new DownloadRequest(url, listener));
			if (overFlowedItem != null) {
				overFlowedItem.listener.onOverflowed(overFlowedItem.url);
			}
			mQueue.notify();
		}
	}
	
	/**
	 * This method will start the ThreadPool for work.
	 */
	private void startDispatchIfNeed() {
		if (mIsStarted) {
			return;
		}
		
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				while(true) {
					DownloadRequest request = mQueue.pop();
					if (request == null) {
						try {
							synchronized (mQueue) {
								mQueue.wait();
							}
							continue;
						} catch (InterruptedException e) {
							Log.w(TAG, "Dispatcher thread has been interupted: " + e);
						};
					}
					
					dispatchRequest(request);
				}
			}


		});
		mIsStarted = true;
	}
	
	private void dispatchRequest(final DownloadRequest request) {
		mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				doDownload(request);
			}
		});
	}

	private void doDownload(DownloadRequest request) {
		Bitmap bitmap = Utils.downloadBitmapBaidu(request.url);
		if (bitmap == null) {
			request.listener.onError(request.url);
		} else {
			request.listener.onDownLoaded(request.url, bitmap);
		}
	}
	
	static class DownloadRequest {
		String url;
		OnDownLoadListener listener;
		
		public DownloadRequest(String url, OnDownLoadListener listener) {
			this.url = url;
			this.listener = listener;
		}
		
		@Override
		public boolean equals(Object o) {
			DownloadRequest dest = (DownloadRequest) o;
			return url.equals(dest.url);
		}
		
		@Override
		public int hashCode() {
			return url.hashCode();
		}
		
	}
	
	/**
	 * This listener use for notice the caller about the image loading message. 
	 * @author Elwin
	 *
	 */
	public interface OnDownLoadListener {
		/**
		 * When the image downloaded.
		 * @param url
		 * @param bitmapDrawable
		 */
		void onDownLoaded(String url, Bitmap bitmap);
		
		/**
		 * This load request was canceled for some reason, such as the request been overflow from the request queue and etc.
		 * @param url
		 */
		void onOverflowed(String url);
		
		/**
		 * The loading process occurred some errors.
		 * @param url
		 */
		void onError(String url);
	}
}
