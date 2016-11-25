package com.github.jrialland.javaformatter;

import java.nio.file.Path;

public interface Transpiler {

	public boolean accept(Path path);

	public void transpile(Path file);

	public String getName();

	public String getType();

	public String getShortdesc();
}
