package com.ds.web.crawler.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * The Class CustomWebMvcConfiguration shows how to enable JSON pretty print
 * using Jackson APIs. This will allow to have a better JSON view on the
 * browser.
 */
@Configuration
public class CustomWebMvcConfiguration extends WebMvcConfigurationSupport {
	@Override
	protected void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
		for (final HttpMessageConverter<?> converter : converters) {
			if (converter instanceof MappingJackson2HttpMessageConverter) {
				final MappingJackson2HttpMessageConverter jacksonConverter = (MappingJackson2HttpMessageConverter) converter;
				jacksonConverter.setPrettyPrint(true);
			}
		}
	}
}
