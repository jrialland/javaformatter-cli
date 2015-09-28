package com.github.jrialland.javaformatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {

	public static String toJavaComment(String txt) {
		try {
			BufferedReader br = new BufferedReader(new StringReader(txt));
			StringWriter sw = new StringWriter();
			String line = null;
			boolean first = true;
			while ((line = br.readLine()) != null) {
				if (first) {
					sw.append("/* ");
					first = false;
				} else {
					sw.append(" * ");
				}
				sw.append(line.replaceAll("\r|\n", ""));
				sw.append("\n");
			}
			sw.append(" */\n");
			return sw.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String insertHeader(String header, String javaSource) {
		Matcher m = Pattern.compile("package|import|public|protected|private|class|interface|@enum|enum").matcher(javaSource);
		if(m.find()) {
			return header + javaSource.substring(m.start());
		} else {
			return javaSource;
		}
	}
}
