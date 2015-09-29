package com.github.jrialland.javaformatter.compass;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import com.darrinholst.sass_java.Compiler;
import com.github.jrialland.javaformatter.Transpiler;

/**
 * Reads a file named compass.rb with compass configuration, and apply it on the
 * project.
 * 
 * Sample configuration file :
 * 
 * <code>
 * preferred_syntax = :sass
 * http_path = '/'
 * css_dir = 'assets/stylesheets'
 * sass_dir = 'assets/sass'
 * images_dir = 'assets/images'
 * javascripts_dir = 'assets/javascripts'
 * relative_assets = true
 * line_comments = true
 * output_style = :compressed
 * </code>
 * 
 * @author Julien.Rialland
 *
 */
public class Compass implements Transpiler {

	private Callable<Compiler> callable;

	private File configFile;
	
	public Compass() {
		callable = new Callable<Compiler>() {
			@Override
			public Compiler call() throws Exception {
				final Compiler compiler = new Compiler();
				callable = new Callable<Compiler>() {
					@Override
					public Compiler call() throws Exception {
						return compiler;
					}
				};
				return compiler;
			}
		};
	}

	protected Compiler getCompiler() {
		try {
			return callable.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean accept(Path path) {
		if( Files.isRegularFile(path) && path.getFileName().toString().equals("compass.rb")) {
			setConfigFile(path.toFile());
			return true;
		}
		return false;
	}

	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}
	
	@Override
	public void transpile(Path file) {
		Compiler compiler = getCompiler();
		compiler.setConfigLocation(configFile);
		compiler.compile();
	}

	@Override
	public String getName() {
		return "Sass";
	}

	@Override
	public String getType() {
		return "sass";
	}

}
