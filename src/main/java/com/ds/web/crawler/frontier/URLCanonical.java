package com.ds.web.crawler.frontier;

import com.ds.web.crawler.exception.WebCrawlerException;

/**
 * The Interface URLCanonical. The canonical tag is useful when indexing
 * documents into a search engine to reduce the number of duplicates or
 * near-variants which share the same value for the tag
 */
public interface URLCanonical {

	String get(String url) throws WebCrawlerException;

}
