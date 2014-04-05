package de.secondsystem.game01.impl.gui;

public class SimpleRwValueRef<T> implements RwValueRef<T> {

	private T value;

	public SimpleRwValueRef() {
	}
	public SimpleRwValueRef(T value) {
		this.value = value;
	}
	
	@Override
	public T getValue() {
		return value;
	}

	@Override
	public void setValue(T value) {
		this.value = value;
	}

}
