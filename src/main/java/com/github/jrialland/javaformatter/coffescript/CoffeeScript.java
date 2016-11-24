
package com.github.jrialland.javaformatter.coffescript;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.github.jrialland.javaformatter.Transpiler;

public class CoffeeScript implements Transpiler {

  private ScriptEngine scriptEngine;

  public CoffeeScript() {

    scriptEngine = new ScriptEngineManager().getEngineByName("js");

    InputStream coffeCompiler = CoffeeScript.class.getClassLoader()
        .getResourceAsStream("META-INF/resources/webjars/coffee-script/1.11.0/coffee-script.min.js");
    try {
      scriptEngine.eval(new InputStreamReader(coffeCompiler));
      scriptEngine.eval("var __coffescript__compile__ = function(src){return CoffeScript.compile(src, {bare: true});}");

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean accept(Path path) {
    return Files.isRegularFile(path) && path.toString().endsWith(".coffee");
  }

  @Override
  public String getName() {
    return "CoffeeScript compiler";
  }

  @Override
  public String getType() {
    return "coffeescript";
  }

  @Override
  public void transpile(Path file) {
    try {
      byte[] data = Files.readAllBytes(file);
      Path output = Paths.get(file.toAbsolutePath().toString().replaceFirst("\\.coffee$", ".coffee.js"));
      Bindings bindings = scriptEngine.createBindings();
      bindings.put("coffescript_src", new String(data));
      Object coffeSrc = scriptEngine.eval("__coffescript__compile__(coffescript_src)", bindings);
      Files.write(output, coffeSrc.toString().getBytes(), StandardOpenOption.WRITE);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getShortdesc() {
    return "Runs coffescript compiler on *.coffe files";
  }

}
