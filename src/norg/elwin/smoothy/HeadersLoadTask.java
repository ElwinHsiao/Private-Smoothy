package norg.elwin.smoothy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

public class HeadersLoadTask extends AsyncTask<String, HeaderInfo, List<HeaderInfo>> {

	private static final String TAG = "HeadersLoadTask";
	private OnHeaderInfoListener mOnHeaderInfoListener;
	private BaiduApiParser mBaiduApiParser;
	private boolean mIsCancelled;

	public HeadersLoadTask(OnHeaderInfoListener listener) {
		mOnHeaderInfoListener = listener;
		mBaiduApiParser = new BaiduApiParser();
	}
	
	@Override
	protected void onPreExecute() {
		mOnHeaderInfoListener.onPrePerform();
	};
	
	@Override
	protected List<HeaderInfo> doInBackground(String... params) {
		mBaiduApiParser.parse(params[0]);
		return null;
	}
	
	@Override
	protected void onProgressUpdate(HeaderInfo... values) {
		if (mIsCancelled) {
			return;
		}
		mOnHeaderInfoListener.OnHeaderInfo(values[0]);
	};
	
	@Override
	protected void onPostExecute(List<HeaderInfo> result) {
		if (mIsCancelled) {
			return;
		}
		mOnHeaderInfoListener.onFinish();
	};
	
	@Override
	protected void onCancelled() {
		Utils.logd(TAG, "Task was cancel, result:" + isCancelled());
		mIsCancelled = true;
	}

	
	class BaiduApiParser {
		private static final String TAG = "BaiduApiParser";

		public void parse(String keyword) {
	    	String url = String.format("http://image.baidu.com/i?tn=baiduimagejson&width=&height=&word=%s&rn=60&pn=0", keyword);
	    	InputStream is = Utils.urlToInputStream(url);
	    	parse(is);
	    	url = String.format("http://image.baidu.com/i?tn=baiduimagejson&width=&height=&word=%s&rn=60&pn=60", keyword);
	    	is = Utils.urlToInputStream(url);
	    	parse(is);
		}
		
		public void parse(InputStream is) {
			try {
				parseInner(is);
			} catch (IOException e) {
				// TODO 
				e.printStackTrace();
			}
		}
		
		public void parseInner(InputStream is) throws IOException {
			JsonReader reader = new JsonReader(new InputStreamReader(is, "gb2312"));
			reader.beginObject();
			while (reader.hasNext()) {
				if (reader.nextName().equals("data")) {
					reader.beginArray();
					int i = 1;
					while (reader.hasNext()) {
						reader.beginObject();
						HeaderInfo headerInfo = new HeaderInfo();
						while (reader.hasNext()) {
							String name = reader.nextName();
							if (name.equals("thumbURL")) {
								headerInfo.url = reader.nextString();
//								Log.d(TAG, "url=" + headerInfo.url);
							} else if (name.equals("fromPageTitleEnc")) {
								headerInfo.title = i + ". " + reader.nextString();
//								Log.d(TAG, "title=" + headerInfo.title);
							} else {
								reader.skipValue();
							}
						}
//						Log.d(TAG, headerInfo.toString());
						publishProgress(headerInfo);
						reader.endObject();
						++i;
					}
					reader.endArray();
				} else {
					reader.skipValue();
				}
			}
			reader.close();
	    }
	}
}
