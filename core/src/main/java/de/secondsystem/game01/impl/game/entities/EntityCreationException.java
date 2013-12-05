package de.secondsystem.game01.impl.game.entities;

@SuppressWarnings("serial")
public final class EntityCreationException extends RuntimeException {

	public EntityCreationException() {
		super();
	}

	public EntityCreationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EntityCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityCreationException(String message) {
		super(message);
	}

	public EntityCreationException(Throwable cause) {
		super(cause);
	}

}
