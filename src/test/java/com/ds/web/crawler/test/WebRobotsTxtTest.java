package com.ds.web.crawler.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ds.web.crawler.robotstxt.WebRobotsTxt;
import com.ds.web.crawler.test.config.TestConfig;
import com.ds.web.crawler.test.config.TestMongoConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class, TestMongoConfig.class })
@EnableMongoRepositories(basePackages = "com.ds.web.crawler.mongo.repo")
public class WebRobotsTxtTest {

	@Autowired
	private WebRobotsTxt webRobotsTxt;

	@Test
	public void testAllows() {
		for (String userAgent : new String[] { "GOOGLEBOT", "GoogleBot", "googlebot", "Any" }) {
			
			boolean result = webRobotsTxt.allows(userAgent, "http://wiprodigital.com");
			assertTrue(result);

			result = webRobotsTxt.allows(userAgent, "http://wiprodigital.com/who-we-are#wdteam_meetus");
			assertTrue(result);

			result = webRobotsTxt.allows(userAgent, "http://wiprodigital.com/who-we-are/#wdteam-vid");
			assertTrue(result);

			result = webRobotsTxt.allows(userAgent, "http://wiprodigital.com/who-we-are#wdteam_leaders");
			assertTrue(result);

			result = webRobotsTxt.allows(userAgent, "http://wiprodigital.com/who-we-are");
			assertTrue(result);
		}

	}

}
