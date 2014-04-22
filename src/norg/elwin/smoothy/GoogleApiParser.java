package norg.elwin.smoothy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.JsonReader;
import android.util.Log;

public class GoogleApiParser {
	private static final String TAG = "GoogleApiParser";

	public void parse(String keyword, OnHeaderInfoListener listener) {
		String url = String.format("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=%s&rsz=8&start=10", keyword);
		InputStream is = Utils.urlToInputStream(url);
		parse(is, listener);
	}
	
	public void parse(InputStream is, OnHeaderInfoListener listener) {
		try {
			parseInner(is, listener);
		} catch (IOException e) {
			// TODO 
			e.printStackTrace();
		}
	}

	private void parseInner(InputStream is,
			OnHeaderInfoListener listener) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(is));
		reader.beginObject();
		Log.d(TAG, reader.nextName());
		reader.beginObject();
		Log.d(TAG, reader.nextName());
		reader.beginArray();
//		List<HeaderInfo> imageInfos = new ArrayList<HeaderInfo>();
		while (reader.hasNext()) {
			reader.beginObject();
			HeaderInfo imageInfo = new HeaderInfo();
			while (reader.hasNext()) {
				String name = reader.nextName();
				if (name.equals("unescapedUrl")) {
					imageInfo.url = reader.nextString();
				} else if (name.equals("titleNoFormatting")) {
					imageInfo.title = reader.nextString();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
//			imageInfos.add(imageInfo);
			Log.d(TAG, imageInfo.toString());
			if (listener != null) {
				listener.OnHeaderInfo(imageInfo);
			}
		}
		reader.close();
	}
}
