package net.humandoing.jarindexer;

/**
 * User: danwin
 * Date: Jun 22, 2005
 * Time: 10:51:13 AM
 */
public class SimpleLogger {

  private static final boolean DEBUG = true;

  public static void debug( Object o ){
    if ( DEBUG ){
      System.out.println( o );
    }
  }

  public static void log( Object o ){
      System.out.println( o );
  }

}
