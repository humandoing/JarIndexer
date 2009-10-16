package net.humandoing.jarindexer;

/**
 * User: danwin
 * Date: Jun 23, 2005
 * Time: 11:17:42 AM
 */
public class JarFileEntry {
  private int jarFileId;
  private String absolutePath;
  private String className;

  public JarFileEntry(int jarFileId, String absolutePath, String className) {
    this.jarFileId = jarFileId;
    this.absolutePath = absolutePath;
    this.className = className;
  }

  public int getJarFileId() {
    return jarFileId;
  }

  public String getAbsolutePath() {
    return absolutePath;
  }

  public String getClassName() {
    return className;
  }

  public String toString(){
    return absolutePath;
  }
}
