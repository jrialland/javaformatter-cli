
package com.github.jrialland.javaformatter.coffescript;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.script.Bindings;
import javax.script.ScriptContext;
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
      if(!Files.isRegularFile(output)){
        Files.createFile(output);
      }
      Bindings bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
      bindings.put("coffescript_src", new String(data));
      Object coffeSrc = scriptEngine.eval("CoffeeScript.compile(coffescript_src, {bare: true})", bindings);
      Files.write(output, coffeSrc.toString().getBytes(), StandardOpenOption.WRITE);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getShortdesc() {
    return "Runs coffescript compiler on *.coffee files";
  }

  public static void main(String[] args) {
    new CoffeeScript().transpile(Paths.get("/home/jrialland/dev/gitprojects/androidtest/test/testcoffee.coffee"));
  }
}
