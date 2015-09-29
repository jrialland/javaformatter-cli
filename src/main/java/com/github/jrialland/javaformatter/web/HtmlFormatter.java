package com.github.jrialland.javaformatter.web;

import java.nio.file.Files;
import java.nio.file.Path;

public class HtmlFormatter extends AbstractJsBeautifyFormatter{

	public HtmlFormatter() {
		super("bower_components/js-beautify/js/lib/beautify-html.js", "style_html","{'indent_inner_html': false,'indent_size': 2,'indent_char': ' ','wrap_line_length': 78,'brace_style': 'expand','unformatted': ['a', 'sub', 'sup', 'b', 'i', 'u'],'preserve_newlines': true,'max_preserve_newlines': 5,'indent_handlebars': false,'extra_liners': ['/html']}");
	}

	@Override
	public String getName() {
		return "Html beautifier";
	}
	
	@Override
	public String getType() {
		return "html";
	}
	
	@Override
	public boolean mayApplyOn(Path file) {
		return Files.isRegularFile(file) && file.toString().endsWith(".html");
	}
}
