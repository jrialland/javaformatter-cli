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
package com.github.jrialland.javaformatter;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class FormatterCli {

	protected static void showHelp(Options opts) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(FormatterCli.class.getSimpleName(), opts);
		return;
	}

	public static void main(String[] args) throws Exception {

		Options opts = new Options();

		Option conf = Option.builder("c").longOpt("conf").required(false).numberOfArgs(1)
				.desc("Eclipse configuration file to use").argName("eclipseConf").build();
		opts.addOption(conf);

		Option level = Option.builder("l").longOpt("level").required(false).numberOfArgs(1).argName("javaVersion")
				.desc("source level").build();
		opts.addOption(level);

		Option header = Option.builder("H").longOpt("header").required(false).numberOfArgs(1).argName("txtFile")
				.desc("source file header").build();
		opts.addOption(header);

		Option encoding = Option.builder("e").longOpt("encoding").required(false).numberOfArgs(1).argName("charset")
				.desc("source encoding").build();
		opts.addOption(encoding);

		Option lsep = Option.builder("s").longOpt("linesep").required(false).hasArg(true).argName("crlf_value").build();
		opts.addOption(lsep);

		Option help = Option.builder("h").longOpt("help").hasArg(false).desc("Shows this help").build();
		opts.addOption(help);

		CommandLine cmd = new DefaultParser().parse(opts, args);

		if (cmd.hasOption("help")) {
			showHelp(opts);
			return;
		}

		JavaFormatter formatter;

		if (cmd.hasOption("conf")) {
			formatter = new JavaFormatter(Paths.get(cmd.getOptionValue("conf")).toUri().toURL());
		} else {
			formatter = new JavaFormatter();
		}

		if (cmd.hasOption("level")) {
			formatter.setSource(cmd.getOptionValue("level"));
		}

		if (cmd.hasOption("encoding")) {
			formatter.setEncoding(cmd.getOptionValue("encoding"));
		}

		if (cmd.hasOption("linesep")) {
			String linesep = cmd.getOptionValue("linesep");
			if (!Arrays.asList("lf", "cr", "crlf").contains(linesep)) {
				throw new IllegalArgumentException("linesep");
			}
			linesep = linesep.toLowerCase().replaceAll("cr", "\r").replaceAll("lf", "\n");
			formatter.setLineSep(linesep);
		}

		if (cmd.hasOption("header")) {
			formatter.setHeader(Paths.get(cmd.getOptionValue("header")).toUri().toURL());
		}

		Path path = Paths.get(args[args.length - 1]);

		if (args.length == 0) {
			System.out.println("Missing file or directory parameter.");
			showHelp(opts);
			System.exit(255);
		}

		if (Files.isRegularFile(path)) {
			formatter.formatFile(path);
		} else if (Files.isDirectory(path)) {
			final JavaFormatter finalFormatter = formatter;

			Files.walkFileTree(path, new FileVisitor<Path>() {
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (file.getFileName().toString().endsWith(".java")) {
						finalFormatter.formatFile(file);
					} else if (file.getFileName().toString().endsWith(".xml")) {
						new XmlFormatter().formatFile(file);
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					throw new RuntimeException("Error during formatting");
				}
			});

		} else {
			throw new IllegalArgumentException("File or Directory not found : " + path);
		}

	}
}
