package norg.elwin.smoothy;

import android.view.View;

public class Transformer {

	public void startTransform() {
		// TODO Auto-generated method stub
		
	}

	public interface OnTransformListener {
		void onPreTransform();
		void onPostTransform(View view);
	}
}
