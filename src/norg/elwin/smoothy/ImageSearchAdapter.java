package norg.elwin.smoothy;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ImageSearchAdapter extends BaseAdapter {
	
	private List<HeaderInfo> data;
	private Context mContext;
	
	public ImageSearchAdapter(Context context) {
		mContext = context;
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
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
		}
		
		TextView titleView = (TextView) view.findViewById(R.id.textView1);
//		ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);
		HeaderInfo imageInfo = this.data.get(position);
		titleView.setText(imageInfo.title);
//		imageView.setImageBitmap(bm);

		return view;
	}
	
}
