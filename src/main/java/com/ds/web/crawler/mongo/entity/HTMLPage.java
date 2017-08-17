package com.ds.web.crawler.mongo.entity;

import java.io.Serializable;

import org.apache.http.Header;
import org.jsoup.nodes.Document;

public class HTMLPage implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4651907667255024395L;

	private String id;

	private int statusCode;

	private Document pageContent;

	private Header[] fetchResponseHeaders;

	private String url;

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

	public Document getPageContent() {
		return pageContent;
	}

	public void setPageContent(Document pageContent) {
		this.pageContent = pageContent;
	}

	public Header[] getFetchResponseHeaders() {
		return fetchResponseHeaders;
	}

	public void setFetchResponseHeaders(Header[] fetchResponseHeaders) {
		this.fetchResponseHeaders = fetchResponseHeaders;
	}
}
