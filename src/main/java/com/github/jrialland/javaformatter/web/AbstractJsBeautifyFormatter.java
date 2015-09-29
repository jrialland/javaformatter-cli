/* Copyright (c) 2015, Julien Rialland
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
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
