package com.ds.web.crawler.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ParserFactory {

	@Autowired
	@Qualifier("html")
	private Parse htmlParser;

	@Autowired
	@Qualifier("pdf")
	private Parse pdfParser;

	public Parse getParser(final String contentType) {
		Parse parser = null;
		switch (contentType) {
		case "application/pdf":
			parser = pdfParser;
		default:
			parser = htmlParser;
		}
		return parser;
	}

}
