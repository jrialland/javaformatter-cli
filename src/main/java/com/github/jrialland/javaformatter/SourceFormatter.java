package com.github.jrialland.javaformatter;

import java.nio.file.Path;

public interface SourceFormatter {

	public String getName();
	
	public String getType();
	
	public boolean mayApplyOn(Path file);
	
	public String apply(String fileContent);
	
}
