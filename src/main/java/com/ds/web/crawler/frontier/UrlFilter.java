package com.ds.web.crawler.frontier;

/**
 * URL filter is used to determine whether the extracted URL should be excluded
 * from the frontier based on one of several tests. For instance, the crawl may
 * seek to exclude certain domains (say, all .com URLs or specific images etc) -
 * in this case the test would simply filter out the URL if it were from the
 * .com domain.
 * 
 * @author 26144
 *
 */
public interface UrlFilter {

	/**
	 * Check if a given url is a target.
	 *
	 * @param url
	 *            URL
	 * @return true if url is matched
	 */
	boolean match(String url);

	/**
	 * Add an url pattern as a target.
	 *
	 * @param urlPattern
	 *            Regular expression that is crawled
	 */
	void addInclude(String urlPattern);

	/**
	 * Add an url pattern as a non-target.
	 *
	 * @param urlPattern
	 *            Regular expression that is not crawled
	 */
	void addExclude(String urlPattern);

	/**
	 * Clear this filter.
	 */
	void clear();

}
