package com.tl.core.exception;

/**
 * TLException
 *
 * @author WangPanYong
 * @since 2023/07/12 11:15
 */
public class TLException extends RuntimeException {
	public TLException() {
		super();
	}

	public TLException(String message) {
		super(message);
	}

	public TLException(String message, Throwable cause) {
		super(message, cause);
	}

	public static TLException of(String message) {
		return new TLException(message);
	}
}
