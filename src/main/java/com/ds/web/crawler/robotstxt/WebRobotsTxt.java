package com.ds.web.crawler.robotstxt;

/**
 * The Interface WebRobotsTxt. Web site owners use the /robots.txt file to give
 * instructions about their site to web robots; this is called The Robots
 * Exclusion Protocol.
 * 
 * 
 */
public interface WebRobotsTxt {

	/**
	 * It works likes this: a robot wants to vists a Web site URL, say
	 * http://www.example.com/welcome.html. Before it does so, it firsts checks
	 * for http://www.example.com/robots.txt, and finds:
	 * 
	 * User-agent: *
	 * 
	 * Disallow: /
	 *
	 * @param userAgent
	 *            the user agent
	 * @param crawledURL
	 *            the web URL
	 * @return true, if successful
	 */
	boolean allows(final String userAgent, final String crawledURL);

}
