# WebCrawler
Web crawling is the process by which we gather pages from the Web, in order to index them and support a search engine. The objective of crawling is to quickly and efficiently gather as many useful web pages as possible, together with the link structure that interconnects them. It is my attempt to create a WebCrawler. 
It is in Beta...

This is my perhaps my attempt to create a WebCrawler.

# What does it have and do
Crawling is performed by anywhere from one to potentially hundreds of threads, each of which loops through the URLs by taking them from the frontier. These threads may be run in a single process, or be partitioned amongst multiple processes running at different nodes of a distributed system. This is just a simple implementation of spawning controlled threads in a VM. This implementation shall be even scalable to even run the threads on a distributed system.

The URL frontier maintains the URLs in the frontier and regurgitates them in some order whenever a crawler thread seeks a URL. This needs to be further improved since a Frontier shall be much more than currently it is.  Two important considerations govern the order in which URLs are returned by the frontier.  First, high-quality pages that change frequently should be prioritized for frequent crawling, it is a `TODO`.  The second consideration is politeness: we must avoid repeated fetch requests to a host within a short time span.

It takes care RobotsTxt, URLFilters for including excluding domains, images etc.



# How to execute

Download MongoDB and run it using defaults.
Download the project and resolve the maven dependencies.
Its a Spring Boot Project and one needs to execute com.ds.web.crawler.WebCrawlerController either as a Spring Application or Java application.

Execute the below REST API to add a seed.
http://localhost:8080/addSeed?url=
It takes care of the default.

And then check the console. It will show all the links, images etc on the console or log file.


