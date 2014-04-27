package norg.elwin.smoothy.imageloader.test;

import norg.elwin.smoothy.imageloader.MyHashMap;
import junit.framework.Assert;
import junit.framework.TestCase;

public class MyHashMapTest extends TestCase {

	private MyHashMap<String, String> mMyHashMap;

	public MyHashMapTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mMyHashMap = new MyHashMap<String, String>();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPutGet() {
		mMyHashMap.put("1", "111");
		Assert.assertEquals("111", mMyHashMap.get("1"));
	}
	
	public void testRemoveKey() {
		mMyHashMap.put("1", "111");
		mMyHashMap.put("2", "222");
		mMyHashMap.remove("1");
		Assert.assertNull(mMyHashMap.get("1"));
		Assert.assertNotNull(mMyHashMap.get("2"));
	}
	
	public void testContainsValue() {
		mMyHashMap.put("1", "111");
		mMyHashMap.put("2", "222");
		;
		Assert.assertTrue(mMyHashMap.containsValue("111"));
		Assert.assertTrue(mMyHashMap.containsValue("222"));
	}
}
