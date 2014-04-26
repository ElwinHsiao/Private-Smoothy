package norg.elwin.smoothy.imageloader;

import norg.elwin.smoothy.R;
import norg.elwin.smoothy.imageloader.ImageCacheLoader.OnImageLoadListener;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

public class ImageResolver {
	
	protected static final String TAG = "ImageResolver";
	private ImageCacheLoader mImageCacheLoader;
	private BidiMap<ImageView, String> mImageViewMap;
	
	private Context mContext;

	public ImageResolver(Context context) {
		mContext = context;
		mImageCacheLoader = new ImageCacheLoader(mContext.getResources());
		mImageViewMap = new DualHashBidiMap<ImageView, String>();
	}

	public void resolveImage(ImageView imageView, String url) {
		if (url == null) {
			Log.w(TAG, "in resolveImage, ignore empty url");
			return;
		}
		imageView.setImageDrawable(getStubDrawable());
		addMapping(imageView, url);
		mImageCacheLoader.loadImage(url, mOnImageLoadListener);
	}
	
	private void notifyBitmap(String url, BitmapDrawable drawable) {
		ImageView imageView = popImageView(url);
		imageView.setImageDrawable(drawable);
	}
	
	private void notifyError(String url) {
		ImageView imageView = popImageView(url);
		imageView.setImageDrawable(getErrorDrawable());
	}

	private void addMapping(ImageView imageView, String url) {
		ImageView otherImageView = mImageViewMap.getKey(url);
		if (otherImageView != null && otherImageView != imageView) {
			throw new RuntimeException("Unsupoort multi-bind for a url currently, maybe will in future");
		}
		
		mImageViewMap.put(imageView, url);	// replace or add.
	}
	
	private void delMapping(String url) {
		mImageViewMap.removeValue(url);
	}
	
	private ImageView popImageView(String url) {
		return mImageViewMap.removeValue(url);
	}
	
	private Drawable mErrorDrawable;
	private Drawable getErrorDrawable() {
		if (mErrorDrawable == null) {
			mErrorDrawable = mContext.getResources().getDrawable(R.drawable.image_error);
		}
		return mErrorDrawable;
	}
	
	private Drawable mStubDrawable;
	private Drawable getStubDrawable() {
		if (mStubDrawable == null) {
			mStubDrawable = mContext.getResources().getDrawable(R.drawable.image_stub);
		}
		return mStubDrawable;
	}
	
	private OnImageLoadListener mOnImageLoadListener = new OnImageLoadListener() {
		
		@Override
		public void onLoaded(String url, BitmapDrawable bitmapDrawable) {
			notifyBitmap(url, bitmapDrawable);
		}
		
		@Override
		public void onError(String url) {
			Log.e(TAG, "load error for url: " + url);
			notifyError(url);
		}
		
		@Override
		public void onCanceled(String url) {
			Log.i(TAG, "load canceled for url: " + url);
			delMapping(url);
		}
	};
}
