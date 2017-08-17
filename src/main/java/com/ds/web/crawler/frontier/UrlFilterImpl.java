package com.ds.web.crawler.frontier;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author 26144
 *
 */
@Component
public class UrlFilterImpl implements UrlFilter {

	private static final Logger logger = LoggerFactory.getLogger(UrlFilterImpl.class);

	private List<Pattern> cachedIncludeSet = new LinkedList<>();

	private List<Pattern> cachedExcludeSet = new LinkedList<>();

	
	/* (non-Javadoc)
	 * @see com.ds.web.crawler.frontier.UrlFilter#addExclude(java.lang.String)
	 */
	@Override
	public void addExclude(final String urlPattern) {
		try {
			cachedExcludeSet.add(Pattern.compile(urlPattern));
		} catch (final Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Invalid exclude pattern: " + urlPattern);
			}
			return;
		}
	}

	
	/* (non-Javadoc)
	 * @see com.ds.web.crawler.frontier.UrlFilter#addInclude(java.lang.String)
	 */
	@Override
	public void addInclude(final String urlPattern) {
		try {
			cachedIncludeSet.add(Pattern.compile(urlPattern));
		} catch (final Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Invalid include pattern: " + urlPattern);
			}
			return;
		}
	}

	
	/* (non-Javadoc)
	 * @see com.ds.web.crawler.frontier.UrlFilter#clear()
	 */
	@Override
	public void clear() {
		cachedIncludeSet.clear();
		cachedExcludeSet.clear();
	}

	/* (non-Javadoc)
	 * @see com.ds.web.crawler.frontier.UrlFilter#match(java.lang.String)
	 */
	@Override
	public boolean match(final String url) {
		if (!this.cachedIncludeSet.isEmpty()) {
			boolean match = false;
			for (final Pattern pattern : this.cachedIncludeSet) {
				final Matcher matcher = pattern.matcher(url);
				if (matcher.matches()) {
					match = true;
				}
			}
			if (!match) {
				return false;
			}
		}

		if (!this.cachedExcludeSet.isEmpty()) {
			boolean match = false;
			for (final Pattern pattern : this.cachedExcludeSet) {
				final Matcher matcher = pattern.matcher(url);
				if (matcher.matches()) {
					match = true;
				}
			}
			if (match) {
				return false;
			}
		}

		return true;
	}
}
