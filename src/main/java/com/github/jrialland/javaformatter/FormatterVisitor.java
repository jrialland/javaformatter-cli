package com.github.jrialland.javaformatter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormatterVisitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FormatterVisitor.class);

	private static final Logger getLog() {
		return LOGGER;
	}

	private List<SourceFormatter> formatters;

	public FormatterVisitor(List<SourceFormatter> formatters) {
		this.formatters = formatters;
	}

	public void visit(Path dir) {
		try {

			Files.walkFileTree(dir, new FileVisitor<Path>() {
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
					applyAllOnFile(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					throw new RuntimeException("Error during formatting");
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void applyAllOnFile(Path file) throws IOException {
		Path tmpFile = Paths.get(file.toAbsolutePath().toString() + "~");
		for (SourceFormatter formatter : formatters) {
			if (formatter.mayApplyOn(file)) {
				
				//get file data
				byte[] data = Files.readAllBytes(file);

				// backup
				Files.move(file, tmpFile, StandardCopyOption.REPLACE_EXISTING);

				try {

					String strData = new String(data);

					getLog().info("applying : " + formatter.getType() + "\ton " + file.toString());

					String modified = formatter.apply(strData);
					getLog().info(" .. done");
					Files.copy(new ByteArrayInputStream(modified.getBytes()), file,
							StandardCopyOption.REPLACE_EXISTING);

					// rm backup
					Files.delete(tmpFile);
					
				} catch (Exception e) {
					getLog().error("formatter error", e);
					
					// replace file with the backup in case of error
					Files.move(tmpFile, file, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}

}
