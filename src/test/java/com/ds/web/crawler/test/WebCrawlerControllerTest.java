package com.ds.web.crawler.test;

import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.ds.web.crawler.WebCrawlerController;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebCrawlerController.class)
@WebAppConfiguration
public class WebCrawlerControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(WebCrawlerControllerTest.class);

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Before
	public void beforeMethod() {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void testAddSeedDefault() {
		try {
			logger.info("########### Test Method Seeded ########");
			this.mockMvc.perform(get("/addSeed")).andExpect(status().isOk());
		} catch (Exception e) {
			fail("Exception Adding Seed");
		}
	}
	
	@Test
	public void testAddSeedURL() {
		try {
			logger.info("########### Test Method Seeded ########");
			this.mockMvc.perform(get("/addSeed").param("url", "http://wiprodigital.com")).andExpect(status().isOk());
		} catch (Exception e) {
			fail("Exception Adding Seed");
		}
	}

}
