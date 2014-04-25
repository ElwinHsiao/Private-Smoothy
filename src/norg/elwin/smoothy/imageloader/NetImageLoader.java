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
	private static final int DEFAULT_QUEUE_SIZE = 10;
	protected static final String TAG = null;
	private ExecutorService mExecutor;
	private OverflowStackSet<DownloadRequest> mQueue;
	private boolean mIsStarted;

	public NetImageLoader() {
		mExecutor = Executors.newFixedThreadPool(3);
		mQueue = new OverflowStackSet<DownloadRequest>(DEFAULT_QUEUE_SIZE);
	}

	public void enqueueRequest(String url, OnImageLoadListener listener) {
		startDispatchIfNeed();
		
		DownloadRequest overFlowedItem = mQueue.push(new DownloadRequest(url, listener));
		if (overFlowedItem != null) {
			overFlowedItem.listener.onCanceled(overFlowedItem.url);
		}
		mQueue.notifyAll();
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
							mQueue.wait();
							continue;
						} catch (InterruptedException e) {
							Log.w(TAG, "Dispatcher thread has been interupted: " + e);
						};
					}
					
					doDownload(request);
				}
			}

			private void doDownload(DownloadRequest request) {
				Bitmap bitmap = Utils.downloadBitmapBaidu(request.url);
				if (bitmap == null) {
					request.listener.onError(request.url);
				} else {
					request.listener.onLoaded(request.url, new BitmapDrawable(bitmap));
				}
			}
		});
		mIsStarted = true;
	}

	static class DownloadRequest {
		String url;
		OnImageLoadListener listener;
		
		public DownloadRequest(String url, OnImageLoadListener listener) {
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
}
