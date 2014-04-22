package norg.elwin.smoothy;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Utils {
	public static InputStream urlToInputStream(String url) {
		try {
			URL netUrl = new URL(url);
			return netUrl.openStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
