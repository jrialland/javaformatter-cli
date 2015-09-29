package com.github.jrialland.javaformatter.minify;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jrialland.javaformatter.Transpiler;
import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class Minifier implements Transpiler {

	private static final Logger LOGGER = LoggerFactory.getLogger(Minifier.class);

	@Override
	public boolean accept(Path path) {
		return Files.isRegularFile(path) && path.getFileName().toString().contains(".minify");
	}

	@Override
	public String getName() {
		return "YUI compressor";
	}

	@Override
	public String getType() {
		return "yui";
	}

	private static ErrorReporter reporter = new ErrorReporter() {

		@Override
		public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
			if (line < 0) {
				LOGGER.warn(message);
			} else {
				LOGGER.warn(line + ':' + lineOffset + ':' + message);
			}
		}

		@Override
		public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource,
				int lineOffset) {
			error(message, sourceName, line, lineSource, lineOffset);
			throw new RuntimeException(message);
		}

		@Override
		public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
			if (line < 0) {
				LOGGER.error(message);
			} else {
				LOGGER.error(line + ':' + lineOffset + ':' + message);
			}
		}
	};

	@Override
	public void transpile(Path file) {
		try {
			Path output = Paths.get(file.toAbsolutePath().toString().replaceFirst("\\.minify", ".min"));
			FileWriter out = new FileWriter(output.toFile());
			if (file.toString().endsWith(".css")) {
				CssCompressor cssCompressor = new CssCompressor(new FileReader(file.toFile()));
				cssCompressor.compress(out, 0);
			} else if (file.toString().endsWith(".js")) {
				JavaScriptCompressor jsCompressor = new JavaScriptCompressor(new FileReader(file.toFile()), reporter);
				jsCompressor.compress(out, 0, true, false, false, false);
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
