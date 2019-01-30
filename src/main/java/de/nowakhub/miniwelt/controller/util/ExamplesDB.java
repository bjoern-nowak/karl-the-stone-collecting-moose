package de.nowakhub.miniwelt.controller.util;

import de.nowakhub.miniwelt.model.Model;
import de.nowakhub.miniwelt.model.World;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class ExamplesDB {

    // database
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String DB = "jdbc:derby:miniworldDB;create=false";
    private static final String DB_CREATE = "jdbc:derby:miniworldDB;create=true";

    // tables
    private static final String TABLE_EXAMPLES = "EXAMPLES";
    private static final String TABLE_EXAMPLES_TAGS = "EXAMPLES_TAGS";

    // jdbc Connection
    private static Connection conn = null;

    public static void open() {
        try {
            // register driver
            Class.forName(DRIVER).newInstance();

            // get connection
            try {
                if (conn == null)
                    conn = DriverManager.getConnection(DB);
            } catch (SQLException ex) {
                if (ex.getSQLState().equals("XJ004")) {
                    conn = DriverManager.getConnection(DB_CREATE);
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("CREATE TABLE " + TABLE_EXAMPLES + "(name VARCHAR(50) PRIMARY KEY, program CLOB(10k) NOT NULL, world BLOB(10K) NOT NULL)");
                        stmt.execute("CREATE TABLE " + TABLE_EXAMPLES_TAGS + "(name VARCHAR(50) NOT NULL, tag VARCHAR(50) NOT NULL, PRIMARY KEY (name, tag))");
                    }
                }
            }
        } catch (Exception ex) {
            Alerts.showException(ex);
        }
    }

    public static void close() {
        try {
            if (conn != null) conn.close();
            conn = null;
        } catch (SQLException ex) {
            //Alerts.showException(ex);
        }

    }

    public static boolean save(String name, Collection<String> tags, Model model) {
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement prepStmt1 = conn.prepareStatement("INSERT INTO " + TABLE_EXAMPLES + " VALUES ('" + name + "', ?, ?)");
                 PreparedStatement prepStmt2 = conn.prepareStatement("INSERT INTO " + TABLE_EXAMPLES_TAGS + " VALUES ('" + name + "', ?)");) {

                // add example data
                prepStmt1.setString(1, model.program.get());

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                     ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    oos.writeObject(model.getWorld());
                    byte[] modelAsBytes = baos.toByteArray();
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(modelAsBytes)) {
                        prepStmt1.setBinaryStream(2, bais, modelAsBytes.length);
                    }
                }
                prepStmt1.executeUpdate();

                // add examples tags
                for (String tag : tags) {
                    prepStmt2.setString(1, tag);
                    prepStmt2.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (Exception ex) {
                Alerts.showException(ex);
                conn.rollback();
            }
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            Alerts.showException(ex);
        }
        return false;
    }

    public static Collection<String> filter(String tag) {
        Collection<String> names = new ArrayList<>();
        try (Statement stmt = conn.createStatement();) {
            ResultSet result = stmt.executeQuery("SELECT et.name FROM " + TABLE_EXAMPLES_TAGS + " et WHERE et.tag LIKE '%" + tag + "%'");
            while (result.next()) {
                names.add(result.getString(1));
            }
        } catch (SQLException ex) {
            Alerts.showException(ex);
        }
        return names;
    }

    public static Model load(String name) {
        try (Statement stmt = conn.createStatement();) {
            ResultSet result = stmt.executeQuery("SELECT e.program, e.world FROM " + TABLE_EXAMPLES + " e WHERE e.name LIKE '" + name + "'");

            if (result.next()) {
                String program = result.getString(1);
                byte[] worldAsBytes = result.getBytes(2);

                try (ByteArrayInputStream bais = new ByteArrayInputStream(worldAsBytes);
                     ObjectInputStream ois = new ObjectInputStream(bais);) {
                    World world = (World) ois.readObject();

                    Model model = new Model(program);
                    model.setWorld(world);
                    return model;
                }
            }
        } catch (Exception ex) {
            Alerts.showException(ex);
        }
        return null;
    }
}
