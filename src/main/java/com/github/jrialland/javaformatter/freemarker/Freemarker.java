package com.github.jrialland.javaformatter.freemarker;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.jrialland.javaformatter.Transpiler;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;

public class Freemarker implements Transpiler {

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
			for(Entry<Object, Object> entry : System.getProperties().entrySet()) {
				model.put(entry.getKey().toString(), entry.getValue().toString());
			}
			Path outFile = Paths.get(file.toAbsolutePath().toString().replaceFirst("\\.ftl$", ""));
			FileWriter fw = new FileWriter(outFile.toFile());
			template.process(model, fw);
			fw.flush();
			fw.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getShortdesc() {
	  return "Runs freemarker template, applies on *.ftl files";
	}

}
