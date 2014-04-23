package norg.elwin.smoothy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "MainActivity";
	private Context mContext = this;
	
	private ListView mListView;
	private ImageSearchAdapter mSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_stub);
        
//        final ImageView imageView = (ImageView) findViewById(R.id.imageView1);
//        findViewById(R.id.girl_button).setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
////				loadImage(imageView, "http://pic19.nipic.com/20120315/7044702_190131486105_2.jpg");
//				loadImage(imageView, "http://imgt3.bdstatic.com/it/u=2986005735,2710018150&fm=21&gp=0.jpg");
//				parseBaiduAPI("baby");
//			}
//		});
    }
    
	@Override
	public void onClick(View v) {
		Button button = (Button) v;
		String keyword = button.getText().toString();
		Log.d(TAG, "in onClick, button text=" + keyword);
		
		inflateForFirstTime();
		startTransform(keyword);
	}

	private boolean isInflated;		// static local
	private void inflateForFirstTime() {
		if (!isInflated) {
			ViewStub stub = (ViewStub) findViewById(R.id.stub);
			stub.inflate();
			mListView = (ListView) findViewById(R.id.contentStub);
			mSearchAdapter = new ImageSearchAdapter(this);
			mListView.setAdapter(mSearchAdapter);
			isInflated = true;
		}
	}

	private OnHeaderInfoListener mOnHeaderInfoListener;
    private void startTransform(String keyword) {
		if (mOnHeaderInfoListener == null) {
			mOnHeaderInfoListener = new OnHeaderInfoListener() {
				ProgressDialog progressDialog;
				@Override
				public void onPrePerform() {
					progressDialog = ProgressDialog.show(mContext, null, "Loading...");
					mSearchAdapter.updateData(null);
				}
				
				@Override
				public void onFinish() {
				}
				
				@Override
				public void OnHeaderInfo(HeaderInfo headInfo) {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					mSearchAdapter.addData(headInfo);
				}
			};
		}
		
		new HeadersLoadTask(mOnHeaderInfoListener).execute(keyword);
	}

	
	private void loadImage(ImageView imageView, String url) {
    	new BitmapLoadTask(imageView).execute(url);
    }
    
    
    
    
}
