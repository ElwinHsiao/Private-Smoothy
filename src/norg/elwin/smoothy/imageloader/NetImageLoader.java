package norg.elwin.smoothy.imageloader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handle all download requests.
 * @author Elwin
 *
 */
public class NetImageLoader {
	private ExecutorService mExecutor;

	public NetImageLoader() {
		mExecutor = Executors.newFixedThreadPool(2);
	}

	public void enqueueRequest(String url, OnImageLoadListener listener) {
		
	}
	
	
}
