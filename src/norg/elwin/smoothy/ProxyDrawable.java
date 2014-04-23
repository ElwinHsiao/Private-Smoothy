package norg.elwin.smoothy;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

public class ProxyDrawable extends BitmapDrawable {
	private static final String TAG = "ProxyDrawable";

	private static LruCache<String, Bitmap> sLruCache;

	private Context mContext;
	private WeakReference<ImageView> mImageViewRef;
	private static Bitmap PLACEHOLDER_BITMAP = Bitmap.createBitmap(1, 1, Config.RGB_565);
;


	public ProxyDrawable(Resources res, Bitmap bitmap, ImageView imageView) {
        super(res, bitmap);
        mImageViewRef = new WeakReference<ImageView>(imageView);
        if (sLruCache == null) {
        	sLruCache = new LruCache<String, Bitmap>(1024*1024*5);
        }
    }

	public void load(String url) {
		if (url == null) {
			return;
		}
		Bitmap bitmap = sLruCache.get(url);
		if (bitmap != null) {
			if (bitmap == PLACEHOLDER_BITMAP) {
				Log.i(TAG, "This url is being download: " + url);
				return;
			}
			setImageDrawable(bitmap);	// using the cached object.
			return;
		}
		
		sLruCache.put(url, PLACEHOLDER_BITMAP);		// mark the this url being download.
    	new WorkerTask().execute(url);
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
    			sLruCache.put(url, result);
    		} else {
    			sLruCache.remove(url);	// remove the placeholder bitmap.
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
