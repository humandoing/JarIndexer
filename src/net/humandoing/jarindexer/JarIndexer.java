package net.humandoing.jarindexer;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;
import java.sql.SQLException;

/**
 * User: danwin
 * Date: Jun 21, 2005
 * Time: 4:11:01 PM
 */
public class JarIndexer {

    private File startPath;

    public JarIndexer( File startPath ) {
        this.startPath = startPath;
    }

    /**
     * Builds an index of all jar files and their contents in
     * an embedded relational database. Uses the startPath
     * File passed into the constructor as a starting point
     * to recursively search for jar files.
     */
    public void index() throws IOException {
        findAndIndexJars( startPath );
    }

    /**
     * Searches for jar files - when it finds them - it indexes
     * them.
     *
     * @param file
     * @throws IOException
     */
    private void findAndIndexJars( File file ) throws IOException {
        if ( file.isDirectory() ) {
            File[] files = file.listFiles();
            for ( int count = 0; count < files.length; count++ ) {
                findAndIndexJars( files[count] );
            }
        } else {
            if ( file.getName().endsWith( ".jar" ) ) {
                //SimpleLogger.debug(file.getAbsolutePath());
                String fileName = file.getName();
                String absolutePath = file.getAbsolutePath();
                String directory = file.getParentFile().getAbsolutePath();
                String libraryName = fileName.substring( 0, fileName.indexOf( ".jar" ) );
                String libraryVersion = null;
                try {
                    int jarFileId = Database.getInstance().insertJarFileRow( fileName, absolutePath, directory, libraryName, libraryVersion );
                    indexJarEntries( jarFileId, file );
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void indexJarEntries( int jarFileId, File file ) throws IOException {
        ZipFile zip = new ZipFile( file );
        try {
            // Loop through the zip entries and print the name of each one.
            for ( Enumeration list = zip.entries(); list.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) list.nextElement();
                String absolutePath = entry.getName();
                //SimpleLogger.debug( entry.getName() );
                if ( absolutePath.endsWith( ".class" ) ) {
                    //First - trim the ".class" off the end - we don't want to store that.
                    absolutePath = absolutePath.substring( 0, absolutePath.indexOf( ".class" ) );

                    //Second - parse out the ACTUAL class name. Take into account that there
                    //         may be classes that are NOT in a package (e.g. they're in the
                    //         root of the jar file).
                    String className = null;
                    if ( absolutePath.indexOf( "/" ) != -1 ) {
                        className = absolutePath.substring( absolutePath.lastIndexOf( "/" ) + 1, absolutePath.length() );
                    } else {
                        className = absolutePath;
                    }

                    //Third - replaces the '/' with '.', we'd rather store it this way.
                    String absolutePathForStorage = absolutePath.replace( '/', '.' );

                    //Fourth - insert that bad boy.
                    try {
                        Database.getInstance().insertJarFileEntryRow( jarFileId, absolutePathForStorage, className );
                    } catch ( SQLException e ) {
                        e.printStackTrace();
                    }
                } else if ( !entry.isDirectory() ) {
                    //Replaces the '/' with '.', we'd rather store it this way.
                    String absolutePathForStorage = absolutePath.replace( '/', '.' );

                    //If it doesn't end with .class - then just index it, whatever it is.
                    //As long as it isn't a directory.
                    try {
                        Database.getInstance().insertJarFileEntryRow( jarFileId, absolutePathForStorage, absolutePath );
                    } catch ( SQLException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            zip.close();
        }
    }


}
