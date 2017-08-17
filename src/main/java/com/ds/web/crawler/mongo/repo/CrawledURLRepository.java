package com.ds.web.crawler.mongo.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ds.web.crawler.mongo.entity.CrawledURL;

/**
 * The Interface CrawledURLRepository.
 */
public interface CrawledURLRepository extends MongoRepository<CrawledURL, String> {
	CrawledURL findByUrl(String url);
}
