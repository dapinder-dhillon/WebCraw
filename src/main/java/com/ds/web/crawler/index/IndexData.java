package com.ds.web.crawler.index;

import com.ds.web.crawler.mongo.entity.ParsedData;

public interface IndexData {
	
	void index(ParsedData parsedData);

}
