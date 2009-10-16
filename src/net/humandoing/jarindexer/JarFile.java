package net.humandoing.jarindexer;

/**
 * User: danwin
 * Date: Jun 23, 2005
 * Time: 11:24:34 AM
 */
public class JarFile {
  private int jarFileId;
  private String filename;
  private String absolutePath;
  private String directory;
  private String libraryName;
  private String libraryVersion;

  public JarFile(int jarFileId, String filename, String absolutePath, String directory, String libraryName, String libraryVersion) {
    this.jarFileId = jarFileId;
    this.filename = filename;
    this.absolutePath = absolutePath;
    this.directory = directory;
    this.libraryName = libraryName;
    this.libraryVersion = libraryVersion;
  }

  public int getJarFileId() {
    return jarFileId;
  }

  public String getFilename() {
    return filename;
  }

  public String getAbsolutePath() {
    return absolutePath;
  }

  public String getDirectory() {
    return directory;
  }

  public String getLibraryName() {
    return libraryName;
  }

  public String getLibraryVersion() {
    return libraryVersion;
  }
}
