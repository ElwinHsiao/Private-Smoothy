package norg.elwin.smoothy.imageloader;

import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class ImageResolver {
	
	private ImageCacheLoader mImageCacheLoader;

	public void ImageResolver() {
		mImageCacheLoader = new ImageCacheLoader();
	}

	public void resolveImage(String url, ImageView imageView) {
//		mImageCacheLoader.loadImage(url, listener);
	}
	
	private void addMapping(String url, ImageView imageView) {
		
	}
	
	private void delMapping(ImageView imageView) {
		
	}
	
	private void notifyBitmap(String url, BitmapDrawable drawable) {
		
	}
}
