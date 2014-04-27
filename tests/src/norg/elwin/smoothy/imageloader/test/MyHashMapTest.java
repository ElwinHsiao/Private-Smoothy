package norg.elwin.smoothy.imageloader.test;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;
import norg.elwin.smoothy.imageloader.StackHashMap;

public class MyHashMapTest extends TestCase {

	private StackHashMap<String, String> mStackHashMap;

	public MyHashMapTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mStackHashMap = new StackHashMap<String, String>(2);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPutGet() {
		mStackHashMap.put("1", "111");
		Assert.assertEquals("111", mStackHashMap.get("1"));
	}
	
	public void testRemoveKey() {
		mStackHashMap.put("1", "111");
		mStackHashMap.put("2", "222");
		mStackHashMap.remove("1");
		Assert.assertNull(mStackHashMap.get("1"));
		Assert.assertNotNull(mStackHashMap.get("2"));
	}
	
	public void testContainsValue() {
		mStackHashMap.put("1", "111");
		mStackHashMap.put("2", "222");
		Assert.assertTrue(mStackHashMap.containsValue("111"));
		Assert.assertTrue(mStackHashMap.containsValue("222"));
	}
	
	public void testAccessOrderNormal() {
		mStackHashMap.put("1", "111");
		mStackHashMap.put("2", "222");
		Set<Entry<String, String>> entrySet = mStackHashMap.entrySet();
		Iterator<Entry<String, String>> iterator = entrySet.iterator();
		Entry<String, String> entry = iterator.next();
		Assert.assertEquals("1", entry.getKey());
		Assert.assertEquals("111", entry.getValue());
		entry = iterator.next();
		Assert.assertEquals("2", entry.getKey());
		Assert.assertEquals("222", entry.getValue());
	}
	
	public void testAccessOrderDescend() {
		Map<String, String> map = new StackHashMap<String, String>(10, 1.0f, true);
		map.put("1", "111");
		map.put("2", "222");
		map.put("1", "11");
		Set<Entry<String, String>> entrySet = map.entrySet();
		Iterator<Entry<String, String>> iterator = entrySet.iterator();
		Entry<String, String> entry = iterator.next();
		Assert.assertEquals("2", entry.getKey());
		Assert.assertEquals("222", entry.getValue());
		entry = iterator.next();
		Assert.assertEquals("1", entry.getKey());
		Assert.assertEquals("11", entry.getValue());
	}
	
	public void testPushOrder() {
		Entry<String, String> entry = null;
		mStackHashMap.push("1", "111");
		mStackHashMap.push("2", "222");
		Set<Entry<String, String>> entrySet = mStackHashMap.entrySet();
		Iterator<Entry<String, String>> iterator = entrySet.iterator();
		entry = iterator.next();
		Assert.assertEquals("1", entry.getKey());
		Assert.assertEquals("111", entry.getValue());
		entry = iterator.next();
		Assert.assertEquals("2", entry.getKey());
		Assert.assertEquals("222", entry.getValue());
		mStackHashMap.push("2", "22");
		Assert.assertEquals("22", mStackHashMap.get("2"));

	}
	
	public void testPop() {
		Entry<String, String> entry = null;
		mStackHashMap.push("1", "111");
		mStackHashMap.push("2", "222");
		
		entry = mStackHashMap.pop();
		Assert.assertEquals(entry.getKey(), "2");
		Assert.assertEquals(entry.getValue(), "222");
		entry = mStackHashMap.pop();
		Assert.assertEquals(entry.getKey(), "1");
		Assert.assertEquals(entry.getValue(), "111");
		Assert.assertEquals(0, mStackHashMap.size());
		entry = mStackHashMap.pop();
		Assert.assertNull(entry);
	}
	
	public void testPushSpill() {
		Entry<String, String> entry = null;
		entry = mStackHashMap.push("1", "111");
		Assert.assertNull(entry);
		entry = mStackHashMap.push("2", "222");
		Assert.assertNull(entry);
		entry = mStackHashMap.push("3", "333");
		Assert.assertEquals(2, mStackHashMap.size());
		Assert.assertNotNull(entry);
		Assert.assertEquals("1", entry.getKey());
		entry = mStackHashMap.push("3", "33");
		Assert.assertNull(entry);
		
		mStackHashMap.push("1", "111");
		mStackHashMap.push("2", "222");
		mStackHashMap.push("3", "333");
		entry = mStackHashMap.pop();
		Assert.assertEquals(entry.getKey(), "3");
		Assert.assertEquals(entry.getValue(), "333");
		entry = mStackHashMap.pop();
		Assert.assertEquals(entry.getKey(), "2");
		Assert.assertEquals(entry.getValue(), "222");
		entry = mStackHashMap.pop();
		Assert.assertEquals(0, mStackHashMap.size());
		Assert.assertNull(entry);
	}

}
