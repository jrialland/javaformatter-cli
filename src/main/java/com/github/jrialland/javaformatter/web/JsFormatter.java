package com.github.jrialland.javaformatter.web;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class JsFormatter extends AbstractJsBeautifyFormatter {

	private static final List<String> extensions = Arrays.asList(".js", ".json");
	
	public JsFormatter() {
		super("bower_components/js-beautify/js/lib/beautify.js", "js_beautify",
				"{space_after_anon_function:true, brace_style:'expand', end_with_newline:true}");
	}

	@Override
	public String getName() {
		return "JavaScript beautifier";
	}
	
	@Override
	public String getType() {
		return "js";
	}
	
	@Override
	public boolean mayApplyOn(Path file) {
		if(Files.isRegularFile(file)) 
		{
			for(String ext : extensions) {
				if(file.toString().endsWith(ext)) {
					return true;
				}
			}
		}
		return false;
	}

}
