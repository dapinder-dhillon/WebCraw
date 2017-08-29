package com.ds.web.crawler.mongo.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ds.web.crawler.mongo.entity.ParsedData;

/**
 * The Interface ParsedDataRepository.
 */
public interface ParsedDataRepository extends MongoRepository<ParsedData, String> {
	
}
