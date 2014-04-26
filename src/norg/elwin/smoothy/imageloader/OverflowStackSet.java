package norg.elwin.smoothy.imageloader;

import java.util.LinkedList;

import junit.framework.Assert;

/**
 * a linear collection that has these feature: 
 * 1. Has a fix size, will overflow item if it's full.
 * 2. First in last out.
 * 3. Won't contain same item.
 * 4. MultiThread safety. 
 * @author Elwin
 *
 * @param <E>
 */
class OverflowStackSet<E> {
	private LinkedList<E> innerList;
	private int capacity;
	
	public OverflowStackSet(int capacity) {
		this.capacity = capacity;
		this.innerList = new LinkedList<E>();
	}
	
	/**
	 * 
	 * @param item
	 * @return The overflowed item if has.
	 */
	public E push(E item) {
		synchronized (this) {
			return pushLocked(item);
		}
	}

	public E pop() {
		synchronized (this) {
			return popLocked();
		}
	}
	
	public E pushLocked(E item) {
		Assert.assertTrue(innerList.size() <= capacity);		// won't large than, except has a bug.

		E overflowedItem = null;
		boolean isContain = innerList.remove(item);
		if (!isContain && innerList.size() == capacity) {
			overflowedItem = innerList.removeLast();
		}
		innerList.addFirst(item);

		return overflowedItem;
	}

	public E popLocked() {
		if (innerList.size() == 0) {
			return null;
		}
		E result = innerList.pop();
		return result;
	}
	
	
}