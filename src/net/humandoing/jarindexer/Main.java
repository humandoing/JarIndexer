package net.humandoing.jarindexer;



/**
 * User: danwin
 * Date: Jun 21, 2005
 * Time: 4:29:22 PM
 */
public class Main {

  private JarIndexer jarIndexer;

  /**
   * Application entry point
   * @param args
   */
  public static void main(String[] args) {
    try {
      Database.getInstance().shutdown();
      Database.getInstance().init();
      new View();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }






}
