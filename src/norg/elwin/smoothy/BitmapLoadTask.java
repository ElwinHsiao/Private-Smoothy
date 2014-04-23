package norg.elwin.smoothy;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapLoadTask extends AsyncTask<String, Integer, Bitmap> {
	
	private static final String TAG = "BitmapLoadTask";
	private WeakReference<ImageView> imageViewRef;

	public BitmapLoadTask(ImageView imageView) {
		this.imageViewRef = new WeakReference<ImageView>(imageView);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		String url = params[0];
		return Utils.downloadBitmapBaidu(url);
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		ImageView imageView = imageViewRef.get();
		if (imageView != null) {
			imageView.setImageBitmap(result);
		}
	}
	
	
}