package de.secondsystem.game01.impl.map;

@SuppressWarnings("serial")
public final class FormatErrorException extends RuntimeException {

	public FormatErrorException() {
		super();
	}

	public FormatErrorException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FormatErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public FormatErrorException(String message) {
		super(message);
	}

	public FormatErrorException(Throwable cause) {
		super(cause);
	}

}
