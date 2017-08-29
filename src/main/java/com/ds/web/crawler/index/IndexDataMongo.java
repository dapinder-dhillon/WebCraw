package com.ds.web.crawler.index;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ds.web.crawler.mongo.entity.ParsedData;
import com.ds.web.crawler.mongo.repo.ParsedDataRepository;

@Component("mongoIndex")
public class IndexDataMongo implements IndexData {

	@Autowired
	private ParsedDataRepository parsedDataRepository;

	@Override
	public void index(final ParsedData parsedData) {
		parsedDataRepository.save(parsedData);
	}

}
