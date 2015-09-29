package com.github.jrialland.javaformatter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class PathUtil {

	public static interface FileOp {
		public String doWithContent(Path path, String content) throws Exception;
	}

	public static void doWithTmpFile(Path path, FileOp operation) {
		byte[] data = null;
		Path tmpFile = Paths.get(path.toAbsolutePath().toString() + "~");
		// backup
		try {
			data = Files.readAllBytes(path);
			Files.move(path, tmpFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			String newContent = operation.doWithContent(path, new String(data));
			Files.copy(new ByteArrayInputStream(newContent.getBytes()), path, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			// revert
			try {
				Files.move(tmpFile, path, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e2) {
				throw new RuntimeException(e2);
			}
		}

	}
}
