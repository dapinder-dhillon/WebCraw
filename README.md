# WebCrawler
Web crawling is the process by which we gather pages from the Web, in order to index them and support a search engine. The objective of crawling is to quickly and efficiently gather as many useful web pages as possible, together with the link structure that interconnects them. It is my attempt to create a WebCrawler. 
It is in Beta...

This is my perhaps my attempt to create a WebCrawler.

## Features

### Parallel Threads (Controlled) 
Crawling is performed by anywhere from one to potentially hundreds of threads, each of which loops through the URLs by taking them from the frontier. These threads may run in a single process, or be partitioned amongst multiple processes running at different nodes of a distributed system. This is just a simple implementation of parallel threads in a single Virtual Machine. It uses Spring Scheduling to schedule a Main thread responsible for spawning threads and uses Google Guava's Listenable Future<>. A ListenableFuture allows you to register callbacks to be executed once the computation is complete, or if the computation is already complete, immediately. This simple addition makes it possible to efficiently support many operations that the basic Future interface cannot support. With the ability to add callback when Future completes, we can asynchronously and effectively respond to incoming events.

### Robustness
The Web contains servers that create spider traps, which are generators of web pages that mislead crawlers into getting stuck fetching an infinite number of pages in a particular domain. The WebCrawler is designed to be resilient to such traps.

### Politeness
Web servers have both implicit and explicit policies regulating the rate at which a crawler can visit them. These politeness policies must be respected and WebCrawler here is honoring the one. The politeness delay can be configured as an externalized property. 

### URL Frontier
The URL frontier maintains the URLs in the frontier and regurgitates them in some order whenever a crawler thread seeks a URL. Two important considerations govern the order in which URLs are returned by the frontier.  First, high-quality pages that change frequently should be prioritized for frequent crawling, it is a `TODO`.  The second consideration is politeness: we must avoid repeated fetch requests to a host within a short time span.
The first one is dependent on real-time and quality indexing.

### Robots.txt
Web site owners use the /robots.txt file to give instructions about their site to web robots; this is called The Robots Exclusion Protocol. WebCrawler honors the robots.txt file of each domain/site and crawls only if the User-Agent is allowed.

### Configurable Allowed URLs, Images etc.
WebCrawler honors allowed/not allowed URLs, Images etc. in a flexible manner. One place to configure everything.

### Uses NoSQL Database
WebCrawler uses NoSQL database which is a much faster alternative then keeping data in-memory or using a Bloom Filter. It helps in fetching and acting on the data faster. It uses MongoDB along with Spring Data which allows the flexibility of using some other NoSQL with minimal development effort.

### Support Proxy
Supports proxy connection to crawled URLs.

## Configuration (application.yml)

- [followRedirects: true] : Should WebCrawler follow redirects?
- [maxOutgoingLinksToFollow: 20] : Maximum number of pages to fetch. For unlimited number of pages, this parameter should be set to -1.
- [maxConcurrentCrawlers: 4] : Maximum concurrent threads to crawl web.
- [politeness: -1] : We must avoid repeated fetch requests to a host within a short time span. -1 to disable.
- [proxy: true] : Whether make a connection using proxy.
- [proxyHost: 10.6.13.87] : Proxy Details
- [proxyPort: 8080] : Proxy Details
- [proxyScheme: http] : Proxy Details
- [maxPoolSize: 10]
- [scheduleRate: 30000] : Main Thread Crawling Rate.

## Installation

### Pre-requisites
- [MongoDB, Download MongoDB and make sure it is running on default port i.e. 127.0.0.1:27017]  
- [Clone or Download the WebCrawler]
- [To Build executable Spring Boot Jar, 'mvn clean install'. This will build WebCrawler-0.1.0.jar inside target directory.]
- [Execute the JAR from command line 'java -jar target\WebCrawler-0.1.0.jar']
- [Added a Seed URL by execute http://localhost:8080/addSeed and wait for the response message 'URL Seeded Successfully' (The REST API uses the default URL: http://wiprodigital.com/)]
- [WebCrawler threads will start crawling the URL. By default (though configurable) WebCrawler wont span more than four parallel threads.]
- [It will insert the Seed URL in Mongo along with all out going links found through the Frontier - Check Document crawledURL in Mongo]
- [The URLs indexed will be stored in another Document - Check Document parsedData in Mongo]
- [Much easier to see the crawled Data, execute REST API http://localhost:8080/crawledData. Though no UX/UI is here but it uses Pretty Jackson configuration to show JSON in a pretty manner.]

You can even check the logs directory for webcrawler.log file for more details.


