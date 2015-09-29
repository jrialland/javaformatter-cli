package com.github.jrialland.javaformatter.web;

import java.nio.file.Files;
import java.nio.file.Path;

public class CssFormatter extends AbstractJsBeautifyFormatter {

	public CssFormatter() {
		super("bower_components/js-beautify/js/lib/beautify-css.js", "css_beautify", "{'indent_size': 1,'indent_char': '\t','selector_separator': ' ','end_with_newline': false,'newline_between_rules': true}");
	}
	
	@Override
	public boolean mayApplyOn(Path file) {
		return Files.isRegularFile(file) && file.toString().endsWith(".css");
	}
	
	@Override
	public String getName() {
		return "Css beautifier";
	}
	
	@Override
	public String getType() {
		return "css";
	}
}
