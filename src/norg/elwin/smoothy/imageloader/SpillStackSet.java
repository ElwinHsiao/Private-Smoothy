package norg.elwin.smoothy.imageloader;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import android.os.AsyncTask;

/**
 * This collection has the flow features:
 * 1. Won't contain same items.
 * 2. The item which was used least recently will glide to the end.
 * 3. The {@link #enqueue(Object)} will spill the last item when is full.
 * @author Elwin
 *
 * @param <E>
 */
public class SpillStackSet<E> extends AbstractQueue<E> implements BlockingQueue<E> {

	/**
	 * The fix size.
	 */
	private final int mCapacity;
	private StackHashMap<E, Object> mStackHashMap;

	public SpillStackSet(int capacity) {
//		super(capacity);
		mCapacity = capacity;
		mStackHashMap = new StackHashMap<E, Object>(capacity);
	}
	
	/**
	 * An extra operator 
	 * @param e
	 * @return
	 */
	public E push(E e) {
		Entry<E, Object> entry = mStackHashMap.push(e, this);
		return entry == null ? null : entry.getKey();
	}


	@Override
	public void put(E e) throws InterruptedException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E take() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int remainingCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/*****************************-duplicated method-*****************************/

	@Override
	public boolean offer(E e) {
		mStackHashMap.push(e, this);
		return true;
	}

	@Override
	public E poll() {
		Entry<E, Object> entry = mStackHashMap.pop();
		return entry == null ? null : entry.getKey();
	}

	@Override
	public E peek() {
		Entry<E, Object> entry = mStackHashMap.peek();
		return entry == null ? null : entry.getKey();
	}

	@Override
	public Iterator<E> iterator() {
		return mStackHashMap.keySet().iterator();
	}

	@Override
	public int size() {
		return mStackHashMap.size();
	}


}
