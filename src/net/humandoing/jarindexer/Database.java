package net.humandoing.jarindexer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;

/**
 * User: danwin
 * Date: Jun 22, 2005
 * Time: 9:25:25 AM
 */
public class Database {

    public static final String DRIVER = "org.hsqldb.jdbcDriver";

    public static final String CONNECTION_STRING = "jdbc:hsqldb:file:" + System.getProperty("user.home") + System.getProperty("file.separator") + ".JarIndexer" + System.getProperty("file.separator") + "data";
    public static final String USER = "sa";
    public static final String PASSWORD = "";

    /**
     * HSQLDB error code for Table Already Exists
     */
    public static final int TABLE_ALREADY_EXISTS = -21;

    /**
     * Our instance of ourself
     */
    private static Database self;


    /**
     * Constructor
     */
    private Database() {
    }

    /**
     * Get Instance
     */
    public synchronized static Database getInstance() {
        if (self == null) {
            SimpleLogger.debug(CONNECTION_STRING);
            self = new Database();
        }
        return self;
    }

    /**
     * Shutdown method
     */
    public void shutdown() throws Exception {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeQuery("SHUTDOWN");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Search for Jar entries
     *
     * @param searchText
     */
    public ArrayList search(String searchText) throws SQLException {
        SimpleLogger.debug("Searching for: " + searchText);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet results = null;
        ArrayList out = new ArrayList();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM JAR_FILE_ENTRY WHERE ABSOLUTE_PATH LIKE ? ORDER BY ABSOLUTE_PATH ASC");
            searchText = searchText + "%%";
            if (searchText.startsWith("*")) {
                searchText = "%%" + searchText.substring(1, searchText.length());
            }

            SimpleLogger.debug("Really Searching for: " + searchText);
            stmt.setString(1, searchText);
            results = stmt.executeQuery();
            while (results.next()) {
                int jarFileId = results.getInt(1);
                String absolutePath = results.getString(2);
                String className = results.getString(3);
                JarFileEntry entry = new JarFileEntry(jarFileId, absolutePath, className);
                out.add(entry);
            }
            return out;
        } finally {
            close(results);
            close(stmt);
            close(conn);
        }
    }

    /**
     * Check to see if a certain directory has already been indexed.
     *
     * @param directoryName
     */
    public boolean isAlreadyIndexed(String directoryName) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet results = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM JAR_FILE WHERE DIRECTORY LIKE ?");
            stmt.setString(1, directoryName + "%%");
            results = stmt.executeQuery();
            if (results.next()) {
                return true;
            } else {
                return false;
            }
        } finally {
            close(results);
            close(stmt);
            close(conn);
        }
    }

    /**
     * Look up a jar file
     *
     * @param jarFileId
     */
    public JarFile getJarFile(int jarFileId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet results = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM JAR_FILE WHERE JAR_FILE_ID = ?");
            stmt.setInt(1, jarFileId);
            results = stmt.executeQuery();
            if (results.next()) {
                String fileName = results.getString(2);
                String absolutePath = results.getString(3);
                String directory = results.getString(4);
                String libraryName = results.getString(5);
                String libraryVersion = results.getString(6);
                return new JarFile(jarFileId, fileName, absolutePath, directory, libraryName, libraryVersion);
            }
        } finally {
            close(results);
            close(stmt);
            close(conn);
        }
        return null;
    }

    /**
     * Returns the number of jar files we have indexed
     */
    public int getJarsIndexedCount() {
        Connection conn = null;
        ResultSet results = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            results = stmt.executeQuery("SELECT COUNT(*) FROM JAR_FILE");
            results.next();
            return results.getInt(1);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return -1;
        } finally {
            close(results);
            close(stmt);
            close(conn);
        }
    }

    /**
     * Returns the number of classes we have indexed.
     *
     */
    public int getClassesIndexedCount() {
        Connection conn = null;
        ResultSet results = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            results = stmt.executeQuery("SELECT COUNT(*) FROM JAR_FILE_ENTRY");
            results.next();
            return results.getInt(1);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return -1;
        } finally {
            close(results);
            close(stmt);
            close(conn);
        }
    }

    /**
     * Insert a jar file row. Return the primary key of the inserted row.
     */
    public int insertJarFileRow(String filename, String absolutePath, String directory, String libraryName, String libraryVersion) throws SQLException {
        //FILENAME        VARCHAR(128)
        //ABSOLUTE_PATH   VARCHAR(512)
        //DIRECTORY       VARCHAR(512)
        //LIBRARY_NAME    VARCHAR(128)
        //LIBRARY_VERSION VARCHAR(32)
        Connection conn = null;
        PreparedStatement preparedStmt = null;
        Statement stmt = null;
        ResultSet results = null;
        try {
            conn = getConnection();
            preparedStmt = conn.prepareStatement("INSERT INTO JAR_FILE ( FILENAME, ABSOLUTE_PATH, DIRECTORY, LIBRARY_NAME, LIBRARY_VERSION ) VALUES (?,?,?,?,?)");
            preparedStmt.setString(1, filename);
            preparedStmt.setString(2, absolutePath);
            preparedStmt.setString(3, directory);
            preparedStmt.setString(4, libraryName);
            preparedStmt.setString(5, libraryVersion);
            preparedStmt.executeUpdate();

            stmt = conn.createStatement();
            results = stmt.executeQuery("SELECT MAX(JAR_FILE_ID) FROM JAR_FILE");
            results.next();
            return results.getInt(1);
        } finally {
            close(results);
            close(preparedStmt);
            close(stmt);
            close(conn);
        }
    }

    /**
     * Insert a jar file entry row
     */
    public void insertJarFileEntryRow(int jarFileId, String absolutePath, String className) throws SQLException {
        //JAR_FILE_ID     INT NOT NULL
        //ABSOLUTE_PATH   VARCHAR(512)
        //CLASS_NAME      VARCHAR(128)
        Connection conn = null;
        PreparedStatement preparedStmt = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            preparedStmt = conn.prepareStatement("INSERT INTO JAR_FILE_ENTRY ( JAR_FILE_ID, ABSOLUTE_PATH, CLASS_NAME ) VALUES (?,?,?)");
            preparedStmt.setInt(1, jarFileId);
            preparedStmt.setString(2, absolutePath);
            preparedStmt.setString(3, className);
            preparedStmt.executeUpdate();
        } finally {
            close(preparedStmt);
            close(stmt);
            close(conn);
        }
    }


    /**
     * Insert a jar file entry row
     */
    public void deleteIndexes() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM JAR_FILE_ENTRY");
            stmt.executeUpdate("DELETE FROM JAR_FILE");
        } finally {
            close(stmt);
            close(conn);
        }
    }


    /**
     * Initialization method
     *
     * @throws Exception
     */
    public void init() throws Exception {
        System.out.println("Indexes will be saved to: " + System.getProperty("user.home") + System.getProperty("file.separator") + ".JarIndexer" + System.getProperty("file.separator") + "data");
        Connection conn = null;
        try {
            conn = getConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/resources/jar_indexer.sql")));
            StringBuffer sql = new StringBuffer();
            String currentLine = null;
            while ((currentLine = reader.readLine()) != null) {
                sql.append(currentLine);
            }
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql.toString());
        } catch (SQLException e) {
            if (e.getErrorCode() != TABLE_ALREADY_EXISTS) {
                e.printStackTrace();
            } else {
                SimpleLogger.debug("Tables have already been created.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn);
        }
    }

    /**
     * Close a database connection, prepared statement, result set, etc.
     *
     * @param obj
     */
    private void close(Object obj) {
        try {
            if (obj != null) {
                Method closeMethod = obj.getClass().getMethod("close", null);
                closeMethod.invoke(null, null);
            }
        } catch (Exception ignored) {
        }
    }


    /**
     * Get a databse connection
     */
    private Connection getConnection() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);
        } catch (Exception sqle) {
            SimpleLogger.log("Could not get connection to database.");
            sqle.printStackTrace();
            return null;
        }
    }

}
