package com.emc.test.common;

public class SystemException extends BaseException {
	private static final long serialVersionUID = -4385778692022807712L;

	public static final String DEFAULT_ERROR_CODE = "error.system";

	public static final String DEFAULT_ERROR_MESSAGE = "System is not available, please try again later.";

	public SystemException() {
		super(DEFAULT_ERROR_CODE, DEFAULT_ERROR_MESSAGE);
	}

	public SystemException(String pErrorCode) {
		super(pErrorCode, DEFAULT_ERROR_MESSAGE);
	}

	public SystemException(Throwable pNext) {
		super(DEFAULT_ERROR_CODE, pNext, DEFAULT_ERROR_MESSAGE);
	}

	public SystemException(String pErrorCode, String pErrorMessage) {
		super(pErrorCode, pErrorMessage);
	}

	public SystemException(String pErrorCode, Throwable pNext,
			String pErrorMessage) {
		super(pErrorCode, pNext, pErrorMessage);
	}
}
