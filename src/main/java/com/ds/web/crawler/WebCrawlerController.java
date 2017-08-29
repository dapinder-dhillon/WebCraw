package com.ds.web.crawler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ds.web.crawler.config.WebCrawlerConfig;
import com.ds.web.crawler.frontier.Frontier;
import com.ds.web.crawler.mongo.entity.CrawledURLBuilder;
import com.ds.web.crawler.mongo.entity.ParsedData;
import com.ds.web.crawler.mongo.repo.ParsedDataRepository;

/**
 * The Class SpringBootController.
 */
@Controller
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class WebCrawlerController {

	private static final Logger logger = LoggerFactory.getLogger(WebCrawlerController.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(WebCrawlerController.class, args);
	}

	@Autowired
	private Frontier frontier;

	@Autowired
	private ParsedDataRepository parsedDataRepository;

	@Autowired
	private WebCrawlerConfig webCrawlerConfig;

	@RequestMapping(value = "/addSeed", method = RequestMethod.GET)
	@ResponseBody
	public String addSeed(@RequestParam(value = "url", defaultValue = "http://wiprodigital.com/") String url) {
		logger.info("URL to be seed: " + url);
		frontier.schedule(new CrawledURLBuilder.Builder().setUrl(url).build());
		return "URL Seeded Successfully";
	}

	@RequestMapping("/crawledData")
	@ResponseBody
	public List<ParsedData> crawledData() {
		return parsedDataRepository.findAll();
	}

	@RequestMapping(value = "/stopCrawl", method = RequestMethod.GET)
	@ResponseBody
	public String stopCrawl() {
		webCrawlerConfig.setStopCrawl(true);
		return "WebCrawer Stopped Successfully";
	}
}
