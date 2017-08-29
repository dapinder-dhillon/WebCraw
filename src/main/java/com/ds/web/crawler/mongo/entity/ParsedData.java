/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ds.web.crawler.mongo.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;

/**
 * The Class ParsedData.
 */
public class ParsedData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6248577691559457933L;
	@Id
	public String id;
	private String url;
	private String text;
	private String title;
	private List<String> images;
	private Map<String, String> metaTags;
	private Set<CrawledURL> outgoingUrls;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, String> getMetaTags() {
		return metaTags;
	}

	public void setMetaTags(Map<String, String> metaTags) {
		this.metaTags = metaTags;
	}

	public String getMetaTagValue(String metaTag) {
		return metaTags.getOrDefault(metaTag, "");
	}

	public Set<CrawledURL> getOutgoingUrls() {
		return outgoingUrls;
	}

	public void setOutgoingUrls(Set<CrawledURL> outgoingUrls) {
		this.outgoingUrls = outgoingUrls;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}