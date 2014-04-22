package norg.elwin.smoothy;

public interface OnHeaderInfoListener {
	void onPrePerform();
	void OnHeaderInfo(HeaderInfo headInfo);
	void onFinish();
}
