package norg.elwin.smoothy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        findViewById(R.id.girl_button).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				loadImage(imageView, "http://pic19.nipic.com/20120315/7044702_190131486105_2.jpg");
				loadImage(imageView, "http://imgt3.bdstatic.com/it/u=2986005735,2710018150&fm=21&gp=0.jpg");
				parseBaiduAPI("baby");
			}
		});
    }

    private void loadImage(ImageView imageView, String url) {
    	new BitmapLoadTask(imageView).execute(url);
    }
    
    private void parseBaiduAPI(String keyword) {
    	String format = "http://image.baidu.com/i?tn=baiduimagejson&width=&height=&word=%s&rn=20&pn=2";
    	final String url = String.format(format, keyword);
    	new AsyncTask<String, Integer, List<?>>() {

			@Override
			protected List<?> doInBackground(String... params) {
				try {
					InputStream is = new URL(url).openStream();
					JsonReader reader = new JsonReader(new InputStreamReader(is));
					reader.beginObject();
					while (reader.hasNext()) {
						if (reader.nextName().equals("data")) {
							reader.beginArray();
							while (reader.hasNext()) {
								reader.beginObject();
								while (reader.hasNext()) {
									String name = reader.nextName();
									if (name.equals("thumbURL")) {
										Log.d(TAG, "url=" + reader.nextString());
									} else if (name.equals("fromPageTitle")) {
										Log.d(TAG, "title=" + reader.nextString());
									} else {
										reader.skipValue();
									}
								}
								reader.endObject();
							}
							reader.endArray();
						} else {
							reader.skipValue();
						}
					}
					reader.close();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
    		
    	}.execute(url);
    }
    
    class BitmapLoadTask extends AsyncTask<String, Integer, Bitmap> {
    	
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
			
//			InputStream inputStream;
//			try {
//				inputStream = new URL(path).openStream();
//				return BitmapFactory.decodeStream(inputStream);
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			return null;
		}
    }
}
