package com.ds.web.crawler.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
public class TestMongoConfig extends AbstractMongoConfiguration {
	@Override
	protected String getDatabaseName() {
		return "test";
	}

	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient("127.0.0.1", 27017);
	}

	@Override
	protected String getMappingBasePackage() {
		return "com.ds.web.crawler.mongo";
	}
}
