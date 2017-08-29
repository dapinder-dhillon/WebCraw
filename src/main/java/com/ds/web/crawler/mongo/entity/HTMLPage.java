package com.ds.web.crawler.mongo.entity;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class HTMLPage  {

	private String id;

	private String url;
	
	private int statusCode;
	
	private HttpEntity pageContent;

	private Header[] fetchResponseHeaders;
	
	private String contentType;


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public HttpEntity getPageContent() {
		return pageContent;
	}

	public void setPageContent(HttpEntity pageContent) {
		this.pageContent = pageContent;
	}

	public Header[] getFetchResponseHeaders() {
		return fetchResponseHeaders;
	}

	public void setFetchResponseHeaders(Header[] fetchResponseHeaders) {
		this.fetchResponseHeaders = fetchResponseHeaders;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
