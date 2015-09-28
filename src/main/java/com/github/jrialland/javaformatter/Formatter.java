package com.github.jrialland.javaformatter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.RuleSetBase;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.xml.sax.SAXException;

import com.github.jrialland.javaformatter.xml.Profile;
import com.github.jrialland.javaformatter.xml.Profiles;
import com.github.jrialland.javaformatter.xml.Setting;

public class Formatter {

	private String source = "1.8";

	private String lineSep = "\r\n";

	private String encoding = "utf-8";

	private Map<String, String> options;

	private CodeFormatter codeFormatter;

	public Formatter(URL configUrl) throws IOException, SAXException {
		options = readConfigurationFormXml(configUrl);
		codeFormatter = ToolFactory.createCodeFormatter(options);
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public void setLineSep(String lineSep) {
		this.lineSep = lineSep;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}

	public Formatter() {
		super();
		options = new TreeMap<>();
		options.put(JavaCore.COMPILER_SOURCE, source);
		options.put(JavaCore.COMPILER_COMPLIANCE, source);
		options.put(JavaCore.CORE_ENCODING, encoding);
		codeFormatter = ToolFactory.createCodeFormatter(options);
	}

	protected Map<String, String> readConfigurationFormXml(URL configUrl) throws IOException, SAXException {
		Digester digester = new Digester();
		digester.addRuleSet(new RuleSetBase() {
			@Override
			public void addRuleInstances(Digester digester) {
				digester.addObjectCreate("profiles", Profiles.class);
				digester.addObjectCreate("profiles/profile", Profile.class);
				digester.addObjectCreate("profiles/profile/setting", Setting.class);
				digester.addSetNext("profiles/profile", "addProfile");
				digester.addSetNext("profiles/profile/setting", "addSetting");
				digester.addSetProperties("profiles/profile", "kind", "kind");
				digester.addSetProperties("profiles/profile/setting", "id", "id");
				digester.addSetProperties("profiles/profile/setting", "value", "value");
			}
		});
		Object result = digester.parse(configUrl.openStream());
		if (result != null) {
			if (result instanceof Profiles) {
				List<Map<String, String>> list = ((Profiles) result).getProfiles();
				if (list.isEmpty()) {
					return new TreeMap<String, String>();
				} else {
					return list.iterator().next();
				}
			} else {
				throw new IllegalArgumentException("could not read " + configUrl.toExternalForm());
			}
		} else {
			throw new IllegalArgumentException("could not read " + configUrl.toExternalForm());
		}
	}

	public String format(String javaCode) {
		TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT + CodeFormatter.F_INCLUDE_COMMENTS,
				javaCode, 0, javaCode.length(), 0, lineSep);
		if (textEdit == null) {
			throw new IllegalStateException("code format error");
		}
		IDocument doc = new Document(javaCode);
		try {
			textEdit.apply(doc);
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
		return doc.get();
	}

	public void formatFile(Path javaFile) throws IOException {
		// backup
		Files.copy(javaFile, Paths.get(javaFile.toString() + "~"), StandardCopyOption.REPLACE_EXISTING);
		byte[] data = Files.readAllBytes(javaFile);
		String formatted = format(new String(data, encoding));
		Files.copy(new ByteArrayInputStream(formatted.getBytes()), javaFile, StandardCopyOption.REPLACE_EXISTING);
	}

	public static void main(String[] args) {
		String fmt = new Formatter().format("public class Hello{}");
		System.out.println(fmt);
	}
}
