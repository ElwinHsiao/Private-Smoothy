package norg.elwin.smoothy;

import java.lang.ref.WeakReference;

import junit.framework.Assert;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

public class ProxyDrawable extends BitmapDrawable {
	private static final String TAG = "ProxyDrawable";

	private Context mContext;
	private WorkerTask mWorkerTask;
	private WeakReference<ImageView> mImageViewRef;

	private static LruCache<String, Bitmap> sLruCache = new LruCache<String, Bitmap>(1024*1024*5);;

	public ProxyDrawable(Resources res, Bitmap bitmap, ImageView imageView) {
        super(res, bitmap);
        mImageViewRef = new WeakReference<ImageView>(imageView);
    }

	public void load(String url) {
		if (url == null) {
			return;
		}
		Bitmap bitmap = getBitmapFromMemCache(url);
		if (bitmap != null) {
			setImageDrawable(bitmap);	// using the cached object.
			return;
		}
		
		
//		cancelPreviousTask();
    	mWorkerTask = new WorkerTask();
    	mWorkerTask.execute(url);
    }
	
	private void cancelPreviousTask() {
		WorkerTask previousTask = retriveWorkTask();
		if (previousTask != null) {
			boolean result = previousTask.cancel(true);
			Utils.logd(TAG, "in cancelPreviousTask: canceled success? " + result);
		}
	}

	private WorkerTask retriveWorkTask() {
		ImageView imageView = mImageViewRef.get();
		//Assert.assertNotNull(imageView);
		if (imageView != null) {
			Drawable previousDrawable = imageView.getDrawable();
			if (previousDrawable instanceof ProxyDrawable) {
				return ((ProxyDrawable) previousDrawable).getWorkTask();
			}
		}
		
		return null;
	}
	
	public WorkerTask getWorkTask() {
		return mWorkerTask;
	}
	
	private Bitmap getBitmapFromMemCache(String url) {
		Bitmap bitmap = sLruCache.get(url);
		Utils.logd(TAG, "Cache for url=**" + url.substring(url.length()-10) + " hit=" + (bitmap!=null));
		return bitmap;
	}

	private void addBitmapToCache(String url, Bitmap result) {
		sLruCache.put(url, result);
	}
	
    class WorkerTask extends AsyncTask<String, Integer, Bitmap> {
    	private String url;
    	
    	@Override
    	protected Bitmap doInBackground(String... params) {
    		url = params[0];
    		Bitmap bitmap = Utils.downloadBitmapBaidu(url);
    		
    		return bitmap;
    	}

    	@Override
    	protected void onPostExecute(Bitmap result) {
    		if (result != null) {
    			setImageDrawable(result);
    			addBitmapToCache(url, result);
    		}
    	}

    }
    
    private void setImageDrawable(Bitmap bitmap) {
    	ImageView imageView = mImageViewRef.get();
    	
    	if (bitmap != null && imageView != null && imageView.getDrawable() == ProxyDrawable.this) {
    		imageView.setImageBitmap(bitmap);
    	}
    }
}
