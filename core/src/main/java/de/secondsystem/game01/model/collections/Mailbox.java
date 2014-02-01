package de.secondsystem.game01.model.collections;

import java.util.concurrent.atomic.AtomicReference;

public final class Mailbox<T> {

	private AtomicReference<Entry<T>> tail = new AtomicReference<>();
	private Entry<T> head;
	
	/**
	 * [THREADSAVE]
	 * @param message
	 */
	public void push( T message ) {
		Entry<T> mail = new Entry<>(message);
		
		do {
			mail.next = tail.get();
		
		} while( !tail.compareAndSet(mail.next, mail) );
	}
	
	/**
	 * [NOT THREADSAVE]
	 * @return
	 */
	public T pop() {
		Entry<T> r = head;
		
		if( r!=null ) {
			head = r.next;
			return r.message;
		}
		
		do {
			r = tail.get();
			if( r==null )
				return null;
			
		} while( !tail.compareAndSet(r, null) );
		
		while( r!=null ) {
			Entry<T> next = r.next;
			r.next = head;
			head = r;
			r = next;
		}
		
		return pop();
	}
	
	/**
	 * [NOT THREADSAVE]
	 * @return
	 */
	public boolean isEmpty() {
		return tail.get()==null && head==null;
	}


	private static final class Entry<T> {
		volatile Entry<T> next;
		final T message;
		Entry( T m ) {
			message = m;
		}
	}
}
