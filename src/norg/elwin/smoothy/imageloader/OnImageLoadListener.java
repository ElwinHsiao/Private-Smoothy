package norg.elwin.smoothy.imageloader;

import android.graphics.drawable.BitmapDrawable;

/**
 * This listener use for notice the caller about the image loading message. 
 * @author Elwin
 *
 */
public interface OnImageLoadListener {
	/**
	 * When the image loaded.
	 * @param url
	 * @param bitmapDrawable
	 */
	void onLoaded(String url, BitmapDrawable bitmapDrawable);
	
	/**
	 * This load request was canceled for some reason, such as the request been overflow from the request queue and etc.
	 * @param url
	 */
	void onCanceled(String url);
	
	/**
	 * The loading process occurred some errors.
	 * @param url
	 */
	void onError(String url);
}