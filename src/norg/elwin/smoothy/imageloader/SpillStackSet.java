package norg.elwin.smoothy.imageloader;

import java.util.concurrent.LinkedBlockingDeque;

import junit.framework.Assert;

/**
 * This collection has the flow features:
 * 1. Won't contain same items.
 * 2. The item which was used least recently will glide to the end.
 * 3. The {@link #enqueue(Object)} will spill the last item when is full.
 * @author Elwin
 *
 * @param <E>
 */
public class SpillStackSet<E> extends LinkedBlockingDeque<E>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8553578647948735252L;
	private final int mCapacity;

	public SpillStackSet(int capacity) {
		super(capacity);
		mCapacity = capacity;
	}
	
	/**
	 * 
	 * @param item
	 * @return The overflowed item if has.
	 */
	public E enqueue(E item) {
		Assert.assertNotNull(item);
		
		E spillItem = null;
		int size = size();
		int remain = mCapacity - size;
		boolean isContain = remove(item);
		if (!isContain && remain == 0) {
			spillItem = pollLast();
		}
		offerFirst(item);
		
		return spillItem;
	}
	
	
}
