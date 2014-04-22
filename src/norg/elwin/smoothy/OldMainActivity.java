package norg.elwin.smoothy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class OldMainActivity extends Activity implements View.OnClickListener {

	private static final String TAG = "MainActivity";
	private ListView mListView;
	private ImageSearchAdapter mSearchAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public void onClick(View v) {
		Button button = (Button) v;
		String keyWord = button.getText().toString();
		Log.d(TAG, "in onClick, button text=" + keyWord);
		
		inflateForFirstTime();
		startTransform(keyWord);
	}

	private boolean isInflated;		// static local
	private void inflateForFirstTime() {
		if (!isInflated) {
			ViewStub stub = (ViewStub) findViewById(R.id.stub);
			stub.inflate();
			mListView = (ListView) findViewById(R.id.contentStub);
			mSearchAdapter = new ImageSearchAdapter();
			mListView.setAdapter(mSearchAdapter);
			isInflated = true;
		}
	}

	private void startTransform(String keyWord) {
//		Transformer transformer = getTransfor1mer();
//		transformer.startTransform();
		
		new AsyncTask<String, ImageInfo, List<ImageInfo>>() {
			private ProgressDialog progressDialog;
			private boolean isIncrease;
			
			@Override
			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(OldMainActivity.this, "", "refreshing...");
				mSearchAdapter.updateData(null);
			};
			
			@Override
			protected List<ImageInfo> doInBackground(String... params) {
				Log.d(TAG, "begin download");
				InputStream is;
				List<ImageInfo> imageInfos = null;
				if (params[0].equalsIgnoreCase("girl")) {
					is = getResources().openRawResource(R.raw.pic_girl);
				} else if (params[0].equalsIgnoreCase("baby")) {
					is = getResources().openRawResource(R.raw.pic_baby);
					
				} else {
					is = downloadJson(params);
				}
				
				Log.d(TAG, "begin parse");
				if (params[0].equalsIgnoreCase("baby")) {
					this.isIncrease = true;
					try {
						imageInfos = parse(is, new OnImageInfoListener() {
							@Override
							public void onNewImageInfo(ImageInfo imageInfo) {
								publishProgress(imageInfo);
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						imageInfos = parse(is, null);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
//				JsonReader jsonReader = new JsonReader(new InputStreamReader(is));
//				jsonReader.
//				JSONObject jsonObject = new JSONObject(json)
				return imageInfos;
			}
			
			@Override
			protected void onProgressUpdate(ImageInfo... values) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				mSearchAdapter.addData(values[0]);
			};
			
			private InputStream downloadJson(String... params) {
				String path = String.format("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=%s&rsz=8&start=10", params[0]);
				try {
					URL url = new URL(path);
					return url.openStream();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			@Override
			protected void onPostExecute(List<ImageInfo> result) {
				if (!isIncrease) {
					mSearchAdapter.updateData(result);
					progressDialog.dismiss();
				}
			};
		}.execute(keyWord);
	}

//	private Transformer mTransformer;			// static local
//	private Transformer getTransfor1mer() {
//		if (mTransformer == null) {
//			mTransformer = new Transformer();
////			mTransformer.setTransformerListener();
//		}
//		return mTransformer;
//	}
	
//	static class JsonParseHelper {
		public List<ImageInfo> parse(InputStream is, OnImageInfoListener listener) throws IOException {
			JsonReader reader = new JsonReader(new InputStreamReader(is));
			reader.beginObject();
			Log.d(TAG, reader.nextName());
			reader.beginObject();
			Log.d(TAG, reader.nextName());
			reader.beginArray();
			List<ImageInfo> imageInfos = new ArrayList<OldMainActivity.ImageInfo>();
			while (reader.hasNext()) {
				reader.beginObject();
				ImageInfo imageInfo = new ImageInfo();
				while (reader.hasNext()) {
//					Log.d(TAG, reader.peek().name());
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
				imageInfos.add(imageInfo);
				Log.d(TAG, imageInfo.toString());
				if (listener != null) {
					listener.onNewImageInfo(imageInfo);
				}
			}
			reader.close();
			return imageInfos;
		}
		
		
//	}
	
	static class ImageInfo {
		public String title;
		public String url;
		
		@Override
		public String toString() {
			return "ImageInfo: title=" + title + " url=" + url;
		}
	}
	
	public interface OnImageInfoListener {
		void onNewImageInfo(ImageInfo imageInfo);
	}
	
	class ImageSearchAdapter extends BaseAdapter {
		
		private List<ImageInfo> data;

		public void updateData(List<ImageInfo> data) {
			this.data = data;
			notifyDataSetChanged();
		}
		
		public void addData(ImageInfo item) {
			if (this.data == null) {
				this.data = new ArrayList<OldMainActivity.ImageInfo>();
			}
			this.data.add(item);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return data == null ? 0 : data.size();
		}

		@Override
		public Object getItem(int position) {
			return data == null ? null : data.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(OldMainActivity.this).inflate(R.layout.list_item, null);
			}
			
			TextView titleView = (TextView) view.findViewById(R.id.textView1);
//			ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);
			ImageInfo imageInfo = this.data.get(position);
			titleView.setText(imageInfo.title);
//			imageView.setImageBitmap(bm);

			return view;
		}
		
	}
}
