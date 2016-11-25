
package com.github.jrialland.javaformatter.minify;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jrialland.javaformatter.Transpiler;
import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class Minifier implements Transpiler {

  private static final Logger LOGGER = LoggerFactory.getLogger(Minifier.class);

  private static final Logger getLog() {
    return LOGGER;
  }

  @Override
  public boolean accept(Path path) {
    boolean hasMinify = Files.isRegularFile(path) && path.getFileName().toString().contains(".minify");
    String extension = path.getFileName().toString().toLowerCase().replaceFirst("^.*\\.([^\\.]+)$", "$1");
    return hasMinify && Arrays.asList("js", "css").contains(extension);
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
        getLog().warn(message);
      } else {
        LOGGER.warn(line + ':' + lineOffset + ':' + message);
      }
    }

    @Override
    public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
      error(message, sourceName, line, lineSource, lineOffset);
      throw new RuntimeException(message);
    }

    @Override
    public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
      if (line < 0) {
        getLog().error(message);
      } else {
        getLog().error(String.format("[%d:%d] : %s", line, lineOffset, message));
      }
    }
  };

  @Override
  public void transpile(Path file) {
    Path output = Paths.get(file.toAbsolutePath().toString().replaceFirst("\\.minify", ".min"));
    try {
      String data = new String(Files.readAllBytes(file)).trim();
      FileWriter out = new FileWriter(output.toFile());
      if (!data.isEmpty()) {
        if (file.toString().toLowerCase().endsWith(".css")) {
          CssCompressor cssCompressor = new CssCompressor(new FileReader(file.toFile()));
          cssCompressor.compress(out, 0);
        } else if (file.toString().toLowerCase().endsWith(".js")) {
          JavaScriptCompressor jsCompressor = new JavaScriptCompressor(new FileReader(file.toFile()), reporter);
          jsCompressor.compress(out, 0, true, false, false, false);

        }
      }
      out.flush();
      out.close();
    } catch (Exception e) {
      try {
        Files.deleteIfExists(output);
      } catch (IOException e2) {
        getLog().error("could not delete file", e2);
      }
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getShortdesc() {
    return "Minifies css and js files. Applies on files named *.css.minify or *.js.minify";
  }

}
