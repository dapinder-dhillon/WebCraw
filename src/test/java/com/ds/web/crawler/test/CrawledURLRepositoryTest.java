package com.ds.web.crawler.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ds.web.crawler.mongo.entity.CrawledURL;
import com.ds.web.crawler.mongo.entity.CrawledURLBuilder;
import com.ds.web.crawler.mongo.repo.CrawledURLRepository;
import com.ds.web.crawler.test.config.TestConfig;
import com.ds.web.crawler.test.config.TestMongoConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class, TestMongoConfig.class })
@EnableMongoRepositories(basePackages = "com.ds.web.crawler.mongo.repo")
public class CrawledURLRepositoryTest {

	@Autowired
	private CrawledURLRepository crawledURLRepository;

	@Test
	public void testURLSave() {

		final CrawledURL crawledURL = new CrawledURLBuilder.Builder().setUrl("http://wiprodigital.com/who-we-are")
				.setParentUrl("http://wiprodigital.com/").build();

		CrawledURL crawledURLRtn = crawledURLRepository.save(crawledURL);
		assertNotNull(crawledURLRtn);
		assertNotNull(crawledURLRtn.getId());
	}

	@Test
	public void testURLGet() {

		CrawledURL crawledURLRtn1 = crawledURLRepository.findByUrl("http://wiprodigital.com/who-we-are");
		assertNotNull(crawledURLRtn1);
		assertNotNull(crawledURLRtn1.getId());

	}

}
