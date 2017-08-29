package com.ds.web.crawler.config;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.ds.web.crawler.frontier.Frontier;
import com.ds.web.crawler.index.IndexData;
import com.ds.web.crawler.mongo.entity.CrawledURL;
import com.ds.web.crawler.mongo.entity.ParsedData;
import com.ds.web.crawler.mongo.repo.CrawledURLRepository;
import com.ds.web.crawler.parser.ParserFactory;
import com.ds.web.crawler.threads.CrawlerWorker;
import com.ds.web.crawler.threads.ListeningThreadPoolTaskExecutor;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Component
public class InitalizeThreadPoolExecutor {
	
	private static final Logger logger = LoggerFactory.getLogger(InitalizeThreadPoolExecutor.class);
	
	@Autowired
	private Frontier frontier;

	@Autowired
	private WebCrawlerConfig webCrawlerConfig;
	
	@Autowired
	@Qualifier("mongoIndex")
	private IndexData indexData;
	
	@Autowired
	private CrawledURLRepository crawledURLRepository;
	
	@Autowired
	private ParserFactory parserFactory;
	
	@Autowired
	private WebCrawlerConfig config;
	
	private AtomicInteger spawnedThreadCount;

	@Bean
	public TaskExecutor taskExecutor() {
		logger.info("************ Listenable Thread Pool Executor Initialized.");
		spawnedThreadCount = new AtomicInteger(0);
		final ThreadPoolTaskExecutor executor = new ListeningThreadPoolTaskExecutor();
		executor.setCorePoolSize(webCrawlerConfig.getCorePoolSize());
		executor.setMaxPoolSize(webCrawlerConfig.getMaxPoolSize());
		executor.setQueueCapacity(Integer.MAX_VALUE);
		return executor;
	}

	/**
	 * Scheduling runner. Crawling is performed by anywhere from one to
	 * potentially hundreds of threads, each of which loops through the URLs by
	 * taking them from the frontier. These threads may be run in a single
	 * process, or be partitioned amongst multiple processes running at
	 * different nodes of a distributed system. This is just a simple
	 * implementation of spawning controlled threads in a VM. This
	 * implementation shall be even scalable to even run the threads on a
	 * distributed system.
	 * 
	 * @param service
	 *            the service
	 * @return the command line runner
	 */
	//@Scheduled(fixedRate = 30000)
	@Scheduled(fixedRateString = "${crawler.scheduleRate}")
	public void initializeMainThread() {
		logger.info("************ Main Thread Scheduled");
		final ListeningThreadPoolTaskExecutor service = (ListeningThreadPoolTaskExecutor) ApplicationContextProvider
				.getApplicationContext().getBean(TaskExecutor.class);
		final List<CrawledURL> crawledURLs = frontier.getUrls(webCrawlerConfig.getMaxConcurrentCrawlers());
		logger.info("********************* In while loop, fetching URLs: [" + crawledURLs + "]");
		for (final CrawledURL crawledURL : crawledURLs) {
			final CrawlerWorker crawlerWorker = new CrawlerWorker();
			crawlerWorker.setCrawledURL(crawledURL);
			crawlerWorker.setParserFactory(parserFactory);
			crawlerWorker.setFrontier(frontier);
			crawlerWorker.setWebCrawlerConfig(config);
			final ListenableFuture<ParsedData> parsedData = (ListenableFuture<ParsedData>) service
					.submit(crawlerWorker);
			logger.info("############################# Thread Spawned: [" + spawnedThreadCount.incrementAndGet() + "] for URL: "+crawledURL.getURL());
			Futures.addCallback(parsedData, new FutureCallback<ParsedData>() {

				/* we want this handler to run immediately */
				public void onSuccess(final ParsedData parsedData) {
					indexData.index(parsedData);
					final CrawledURL crawledUrl = crawledURLRepository.findByUrl(parsedData.getUrl());
					crawledUrl.setIndexed(true);
					crawledURLRepository.save(crawledUrl);
					
					indexData.index(parsedData);
					logger.info("************************* On Success **********************************");
					logger.info(parsedData.toString());
					logger.info("************************* URL End **********************************");
				}

				public void onFailure(final Throwable thrown) {
					logger.error("Thread OnFailure Encountered for URL: " + crawledURL.getURL(), thrown);
				}
			});
		}
	}
}
