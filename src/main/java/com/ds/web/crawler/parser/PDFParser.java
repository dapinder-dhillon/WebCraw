package com.ds.web.crawler.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.ds.web.crawler.exception.WebCrawlerRuntimeException;
import com.ds.web.crawler.mongo.entity.HTMLPage;
import com.ds.web.crawler.mongo.entity.ParsedData;

@Component("pdf")
public class PDFParser implements Parse {

	private static final Logger logger = LoggerFactory.getLogger(PDFParser.class);

	@Override
	public ParsedData parse(HTMLPage htmlPage) {

		final ParsedData parsedData = new ParsedData();
		final BodyContentHandler handler = new BodyContentHandler();
		final Metadata metadata = new Metadata();
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(writeEntityToFile(htmlPage.getPageContent()));
		} catch (FileNotFoundException e1) {
			throw new WebCrawlerRuntimeException(e1);
		}
		final ParseContext pcontext = new ParseContext();

		// parsing the document using PDF parser
		org.apache.tika.parser.pdf.PDFParser pdfparser = new org.apache.tika.parser.pdf.PDFParser();
		try {
			pdfparser.parse(inputstream, handler, metadata, pcontext);
		} catch (IOException | SAXException | TikaException e) {
			throw new WebCrawlerRuntimeException(e);
		}

		logger.info("Contents of the PDF:");
		parsedData.setText(handler.toString());

		final Map<String, String> metaTags = new HashMap<>();
		// getting metadata of the document
		logger.info("Metadata of the PDF:");
		String[] metadataNames = metadata.names();
		for (String name : metadataNames) {
			metaTags.put(name, metadata.get(name));
		}
		return null;
	}

	/**
	 * Write HttpEntity to a temp file.
	 *
	 * @param entity
	 *            the entity
	 * @return the file
	 */
	private File writeEntityToFile(HttpEntity entity) {
		File temp = null;
		try {
			temp = File.createTempFile("tempfile", ".tmp");
			if (entity != null) {
				InputStream instream = entity.getContent();
				FileOutputStream output = new FileOutputStream(temp);
				try {
					int l;
					byte[] tmp = new byte[2048];
					while ((l = instream.read(tmp)) != -1) {
						output.write(tmp, 0, l);
					}
				} finally {
					output.close();
					instream.close();
				}
			}
		} catch (UnsupportedOperationException | IOException e) {
			throw new WebCrawlerRuntimeException(e);
		}
		return temp;
	}
}
