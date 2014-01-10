package de.secondsystem.game01.model;

public class GameException extends RuntimeException {
	private static final long serialVersionUID = -3251929949820569650L;

	public GameException() {
	}

	public GameException(String message) {
		super(message);
	}

	public GameException(Throwable cause) {
		super(cause);
	}

	public GameException(String message, Throwable cause) {
		super(message, cause);
	}

	public GameException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
