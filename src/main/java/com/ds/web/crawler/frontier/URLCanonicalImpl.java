package com.ds.web.crawler.frontier;

import org.springframework.stereotype.Component;

import com.ds.web.crawler.exception.WebCrawlerException;

@Component
public class URLCanonicalImpl implements URLCanonical {

	/* (non-Javadoc)
	 * @see com.ds.web.crawler.frontier.URLCanonical#get(java.lang.String)
	 */
	@Override
	public String get(String url) throws WebCrawlerException{
		return url;
		/*try {
			return new URI(url).normalize().getPath();
		} catch (URISyntaxException e) {
			throw new WebCrawlerException(e);
		}*/
	}

}
