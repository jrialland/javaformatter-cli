
package com.github.jrialland.javaformatter.freemarker;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jrialland.javaformatter.Transpiler;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;

public class Freemarker implements Transpiler {

  private static final Logger LOGGER = LoggerFactory.getLogger(Freemarker.class);

  private static final Logger getLog() {
    return LOGGER;
  }

  @Override
  public boolean accept(Path path) {
    return Files.isRegularFile(path) && path.toString().endsWith(".ftl");
  }

  @Override
  public String getName() {
    return "Freemarker template";
  }

  @Override
  public String getType() {
    return "ftl";
  }

  @Override
  public void transpile(Path file) {
    try {
      Configuration configuration = new Configuration(new Version(2, 3, 23));
      configuration.setDirectoryForTemplateLoading(file.toFile().getParentFile());
      configuration.setOutputEncoding("utf-8");
      configuration.setAutoFlush(true);
      Template template = configuration.getTemplate(file.getFileName().toString());
      Map<String, Object> model = new HashMap<String, Object>();
      for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
        String key = entry.getKey().toString();
        String value = entry.getValue().toString();
        insertValue(model, key, value);
      }

      for (Entry<String, String> entry : System.getenv().entrySet()) {
        String key = "env." + entry.getKey();
        String value = entry.getValue();
        insertValue(model, key, value);
      }

      System.out.println(model);

      Path outFile = Paths.get(file.toAbsolutePath().toString().replaceFirst("\\.ftl$", ""));
      FileWriter fw = new FileWriter(outFile.toFile());
      template.process(model, fw);
      fw.flush();
      fw.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void insertValue(Map<String, Object> root, String key, String value) {
    String[] parts = key.split("\\.");
    if (parts.length == 1) {
      root.put(key, value);
      return;
    }

    Map<String, Object> current = root;
    for (int i = 0; i < parts.length - 1; i++) {
      String part = parts[i];
      Object objChild = current.get(part);
      if (objChild instanceof String) {
        part = part + "_";
      }
      Map<String, Object> child = (Map<String, Object>) current.get(part);
      if (child == null) {
        child = new HashMap<>();
        current.put(part, child);
      }
      current = child;
    }
    current.put(parts[parts.length - 1], value);
  }

  @Override
  public String getShortdesc() {
    return "Runs freemarker template, applies on *.ftl files";
  }

}
