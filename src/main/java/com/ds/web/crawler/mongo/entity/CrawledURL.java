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

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * The Class CrawledURL MongoDB Entity.
 */
public class CrawledURL implements Serializable {

	private static final long serialVersionUID = 742520371599474444L;

	@Id
	public String id;
	
	@Indexed(unique = true)
	private String url;
	private String parentId;
	private String parentUrl;
	private long when;
	private boolean seen;
	private boolean indexed;

	public CrawledURL() {
		super();
		this.when = System.currentTimeMillis();
	}

	public long getWhen() {
		return when;
	}

	public void setWhen(long when) {
		this.when = when;
	}

	/**
	 * @return unique document id assigned to this Url.
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return Url string
	 */
	public void setURL(String url) {
		this.url = url;
	}

	/**
	 * @return Url string
	 */
	public String getURL() {
		return url;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentUrl() {
		return parentUrl;
	}

	public void setParentUrl(String parentUrl) {
		this.parentUrl = parentUrl;
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}

	public boolean isSeen() {
		return seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public boolean isIndexed() {
		return indexed;
	}

	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	@Override
	public String toString() {
		return url + " - [ID: " + this.id + "]";
	}

}