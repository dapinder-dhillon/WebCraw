package com.ds.web.crawler.exception;

public class WebCrawlerRuntimeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2012298802398415515L;

	public WebCrawlerRuntimeException() {
		super();
	}

	public WebCrawlerRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public WebCrawlerRuntimeException(String arg0) {
		super(arg0);
	}

	public WebCrawlerRuntimeException(Throwable arg0) {
		super(arg0);
	}

}
