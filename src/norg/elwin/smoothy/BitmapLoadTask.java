package norg.elwin.smoothy;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class BitmapLoadTask extends AsyncTask<String, Integer, Bitmap> {
	
	private static final String TAG = "BitmapLoadTask";
	private WeakReference<ImageView> imageViewRef;

	public BitmapLoadTask(ImageView imageView) {
		this.imageViewRef = new WeakReference<ImageView>(imageView);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		String path = params[0];
		return downloadBitmap(path);
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		ImageView imageView = imageViewRef.get();
		if (imageView != null) {
			imageView.setImageBitmap(result);
		}
	}
	
	private Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
	    final HttpGet getRequest = new HttpGet(url);
	    getRequest.addHeader("Referer", "http://image.baidu.com/");

	    try {
	        HttpResponse response = client.execute(getRequest);
	        final int statusCode = response.getStatusLine().getStatusCode();
	        if (statusCode != HttpStatus.SC_OK) { 
	            Log.w(TAG, "Error " + statusCode + " while retrieving bitmap from " + url); 
	            return null;
	        }
	        
	        final HttpEntity entity = response.getEntity();
	        if (entity != null) {
	            InputStream inputStream = null;
	            try {
	                inputStream = entity.getContent(); 
	                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
	                return bitmap;
	            } finally {
	                if (inputStream != null) {
	                    inputStream.close();  
	                }
	                entity.consumeContent();
	            }
	        }
	    } catch (Exception e) {
	        // Could provide a more explicit error message for IOException or IllegalStateException
	        getRequest.abort();
	        Log.w(TAG, "Error while retrieving bitmap from " + url + " Exception: " + e.toString());
	    } finally {
	        if (client != null) {
	            client.close();
	        }
	    }
	    return null;
		
//		InputStream inputStream;
//		try {
//			inputStream = new URL(path).openStream();
//			return BitmapFactory.decodeStream(inputStream);
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return null;
	}
}