package com.ds.web.crawler.mongo.entity;

public class CrawledURLBuilder {

	public static class Builder {
		public String id;
		private String url;
		private String parentId;
		private String parentUrl;

		private CrawledURL crawledURL = new CrawledURL();

		public Builder() {
			crawledURL.setWhen(System.currentTimeMillis());
		}

		public Builder setUrl(String url) {
			this.url = url;
			return this;
		}

		public Builder setParentUrl(String parentUrl) {
			this.parentUrl = parentUrl;
			return this;
		}

		public Builder setParentId(String parentId) {
			this.parentId = parentId;
			return this;
		}

		public CrawledURL build() {
			CrawledURL crawledURL = new CrawledURL();
			crawledURL.setURL(url);
			crawledURL.setParentUrl(parentUrl);
			crawledURL.setParentId(parentId);
			return crawledURL;
		}

	}

}
