package norg.elwin.smoothy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageSearchAdapter extends BaseAdapter {
	private static final String TAG = "ImageSearchAdapter";
	
	private List<HeaderInfo> data;
	private Context mContext;
	private Bitmap mProxyBitmap;
	
	public ImageSearchAdapter(Context context) {
		mContext = context;
		mProxyBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.image_stub);
	}

	public void updateData(List<HeaderInfo> data) {
		this.data = data;
		notifyDataSetChanged();
	}
	
	public void addData(HeaderInfo item) {
		if (this.data == null) {
			this.data = new ArrayList<HeaderInfo>();
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
		Utils.logd(TAG, "in getView, postion=" + position);
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
		}
		
		TextView titleView = (TextView) view.findViewById(R.id.textView1);
		ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);
		
		HeaderInfo imageInfo = this.data.get(position);
		titleView.setText(imageInfo.title);
		
		loadImage(imageView, imageInfo.url);
		
//		/*test-mode*/computeTime();

		return view;
	}


	private void loadImage(ImageView imageView, String url) {
		ProxyDrawable drawable = new ProxyDrawable(mContext.getResources(), mProxyBitmap, imageView);
		imageView.setImageDrawable(drawable);
		drawable.load(url);
	}

	/*test-mode*/private void computeTime() {
		LinkedList<String> linkedList = new LinkedList<String>();
		for (int i = 0; i < 50; ++i) {
			linkedList.push("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+i);
		}
		
		long nanoTime1 = System.nanoTime();
		
		linkedList.remove("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+1);
		long nanoTime2 = System.nanoTime();
		System.out.println(nanoTime2-nanoTime1+"");
		
		linkedList.addFirst("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+1);
		long nanoTime3 = System.nanoTime();
		System.out.println(nanoTime3-nanoTime2+"");
		
//		for (int i = 0; i < linkedList.size(); ++i) {
//			System.out.println(linkedList.get(i));
//		}
	}
}
