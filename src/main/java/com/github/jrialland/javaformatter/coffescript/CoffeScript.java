package com.github.jrialland.javaformatter.coffescript;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import com.github.jrialland.javaformatter.Transpiler;

public class CoffeScript implements Transpiler {

	private Context context;

	private Scriptable scope;

	private Function fnct;

	public CoffeScript() {
		context = Context.enter();
		scope = context.initStandardObjects();

		InputStream coffeCompiler = CoffeScript.class.getClassLoader()
				.getResourceAsStream("META-INF/resources/webjars/coffe-script/1.10.0/coffe-script.min.js");
		try {
			context.evaluateReader(scope, new InputStreamReader(coffeCompiler), "coffe-script.min.js", 1, null);
			fnct = context.compileFunction(scope, "function(src){return CoffeScript.compile(src, {bare: true});}",
					"fnct", 1, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean accept(Path path) {
		return Files.isRegularFile(path) && path.toString().endsWith(".coffe");
	}

	@Override
	public String getName() {
		return "CoffeScript compiler";
	}

	@Override
	public String getType() {
		return "coffescript";
	}

	@Override
	public void transpile(Path file) {
		try {
			byte[] data = Files.readAllBytes(file);
			String compiled = fnct.call(context, scope, scope, new Object[] { new String(data) }).toString();
			Path output = Paths.get(file.toAbsolutePath().toString().replaceFirst("\\.coffe$", ".coffe.js"));
			Files.write(output, compiled.getBytes(), StandardOpenOption.WRITE);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
