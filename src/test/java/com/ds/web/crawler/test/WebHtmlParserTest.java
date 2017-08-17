package com.ds.web.crawler.test;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.ds.web.crawler.mongo.entity.HTMLPage;
import com.ds.web.crawler.parser.HtmlParser;

public class WebHtmlParserTest {

	@Test
	public void test() {
		
		HTMLPage htmlPage = new HTMLPage();
		Document document = null;
		try {
			document = Jsoup.connect("http://wiprodigital.com").timeout(3000).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		htmlPage.setPageContent(document);
		htmlPage.setUrl("http://wiprodigital.com");
		HtmlParser webHtmlParser = new HtmlParser();
		webHtmlParser.parse(htmlPage);
		
		//fail("Not yet implemented");
	}

}
