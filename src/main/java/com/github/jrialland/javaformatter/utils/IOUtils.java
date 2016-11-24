
package com.github.jrialland.javaformatter.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public final class IOUtils {

  public static long copy(Reader reader, Writer writer) throws IOException {
    char[] buff = new char[1024];
    int c = 0;
    long w = 0;
    while ((c = reader.read(buff)) > -1) {
      writer.write(buff, 0, c);
      w += c;
    }
    reader.close();
    writer.flush();
    return w;
  }
}
