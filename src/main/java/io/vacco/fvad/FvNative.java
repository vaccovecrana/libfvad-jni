package io.vacco.fvad;

import java.io.*;
import java.nio.file.*;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class FvNative {

  public static final String NATIVE_FOLDER_PATH_PREFIX = "fvad-jni";

  private static File tmpDir;

  public static void loadLibraryFromJar(String path) {
    try {
      var parts = path.split("/");
      var fName = parts[parts.length - 1];
      if (tmpDir == null) {
        tmpDir = new File(System.getProperty("java.io.tmpdir"), NATIVE_FOLDER_PATH_PREFIX);
        tmpDir.deleteOnExit();
      }
      var temp = new File(tmpDir, fName);
      temp.mkdirs();
      try (var is = FvNative.class.getResourceAsStream(path)) {
        Files.copy(Objects.requireNonNull(is), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.load(temp.getAbsolutePath());
      }
    } catch (Exception e) {
      throw new IllegalStateException("Unable to load native dependency: " + path, e);
    }
  }

}