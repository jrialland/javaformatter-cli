package com.github.jrialland.javaformatter.web;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import com.github.jrialland.javaformatter.SourceFormatter;

public abstract class AbstractJsBeautifyFormatter implements SourceFormatter{

	private Context context;

	private Scriptable scope;

	private Function fnct;
	
	public AbstractJsBeautifyFormatter(String script, String fnct, String options) {
		try {
			initContext(script, fnct, options);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void initContext(String scriptPath, String fnctName, String options) throws IOException {
		context = Context.enter();
		scope = context.initStandardObjects();
		context.evaluateString(scope, "global={}", "init", 1, null);
		Reader in = new InputStreamReader(
				JsFormatter.class.getClassLoader().getResourceAsStream(scriptPath));
		context.evaluateReader(scope, in, "beautify.js", 1, null);
		fnct = context.compileFunction(scope, "function(txt){return global."+fnctName+"(txt,"+options+");}", "script", 1,null);
	}

	@Override
	public String apply(String js) {
		Object result = fnct.call(context, scope, scope, new Object[] { js });
		return result.toString();
	}
}
