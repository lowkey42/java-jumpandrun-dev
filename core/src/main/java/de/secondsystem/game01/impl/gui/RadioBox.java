package de.secondsystem.game01.impl.gui;


public class RadioBox<T> extends CheckBox {
	
	private static final class _VRef<T> implements RwValueRef<Boolean> {

		private final RwValueRef<T> state;
		private final T val;
		
		public _VRef(RwValueRef<T> state, T val) {
			this.state = state;
			this.val = val;
		}
		
		@Override
		public Boolean getValue() {
			return val.equals(state.getValue());
		}

		@Override
		public void setValue(Boolean value) {
			if( value )
				state.setValue(val);
		}
		
	}
	
	protected RadioBox(float x, float y, RwValueRef<T> groupState, T val, ElementContainer owner) {
		super(x, y, new _VRef<T>(groupState, val), getParentStyle(owner).radioBoxTexture, owner);
	}

}
