package com.github.jrialland.javaformatter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlFormatter {

	private static final Logger LOGGER = LoggerFactory.getLogger(XmlFormatter.class);

	public void formatFile(Path xmlFile) throws IOException {
		LOGGER.info("format " + xmlFile.toString());
		Files.copy(xmlFile, Paths.get(xmlFile.toString() + "~"), StandardCopyOption.REPLACE_EXISTING);
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StringWriter sw = new StringWriter();
			StreamResult r = new StreamResult(sw);
			StreamSource s = new StreamSource(xmlFile.toUri().toURL().openStream());
			transformer.transform(s, r);
			String xml = sw.toString();
			Files.copy(new ByteArrayInputStream(xml.getBytes()), xmlFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			LOGGER.error("while formatting file", e);
			Files.copy(Paths.get(xmlFile.toString() + "~"), xmlFile, StandardCopyOption.REPLACE_EXISTING);
		}
	}

}
