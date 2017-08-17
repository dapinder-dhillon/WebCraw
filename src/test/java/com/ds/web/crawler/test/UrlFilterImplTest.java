package com.ds.web.crawler.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.ds.web.crawler.frontier.UrlFilterImpl;

public class UrlFilterImplTest {
	public UrlFilterImpl urlFilter = new UrlFilterImpl();

	@Test
	public void test() {

		urlFilter.addInclude("http://wiprodigital.com/.*");

		assertTrue(urlFilter.match("http://wiprodigital.com/"));
		assertTrue(urlFilter.match("http://wiprodigital.com/who-we-are#wdteam_meetus"));
		assertTrue(urlFilter.match("http://wiprodigital.com/who-we-are#wdteam_leaders"));
		assertTrue(urlFilter.match("http://wiprodigital.com/?s=&post_type[]=news"));
		assertFalse(urlFilter.match(
				"http://17776-presscdn-0-6.pagely.netdna-cdn.com/wp-content/themes/wiprodigital/images/logo.png"));
		assertFalse(urlFilter.match("https://plus.google.com/+Wiprodigital"));

	}

}
