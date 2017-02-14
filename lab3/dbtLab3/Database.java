package app;

import java.sql.*;
import java.util.*;
import dbtLab3.*;


/**
 * Database is a class that specifies the interface to the movie
 * database. Uses JDBC.
 */
public class Database {

    /**
     * The database connection.
     */
    private Connection conn;

    /**
     * Create the database interface object. Connection to the
     * database is performed later.
     */
    public Database() {
        conn = null;
    }

    /**
     * Open a connection to the database, using the specified user
     * name and password.
     */
    public boolean openConnection(String filename) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + filename);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Close the connection to the database.
     */
    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the connection to the database has been established
     * 
     * @return true if the connection has been established
     */
    public boolean isConnected() {
        return conn != null;
    }

    /* --- insert own code here --- */
    public void loginUser(String userId) {
        PreparedStatement ps = null;

        try {
            String sql = "SELECT username FROM users WHERE username = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                userId = rs.getString("username");
                CurrentUser.instance().loginAs(userId);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            try {
                ps.close(); 
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public Collection<String> getMovies() {
        Statement stmt = null;
        ArrayList<String> movies = new ArrayList<String>();

        try {
            String sql = "SELECT name FROM movies";
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                movies.add(rs.getString("name"));
            }
            return movies;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public Map<String, String> getPerformanceData(String movieName, String date) {
        PreparedStatement ps = null;
        Map<String, String> performance = new HashMap<String, String>(4);
    
        try {
            String sql = "SELECT p.day, p.movie_name, p.theater_name, t.seats - COUNT(r.nbr) AS free_seats FROM performances p LEFT JOIN reservations r ON (p.movie_name = r.movie_name AND p.day = r.day) JOIN theaters t ON (p.theater_name = t.name) WHERE p.movie_name = ? AND p.day = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, movieName);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                performance.put("day", rs.getString("day"));
                performance.put("movie_name", rs.getString("movie_name"));
                performance.put("theater_name", rs.getString("free_seats"));

                return performance;
            }

            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            } 
        }
    }

    public boolean bookTicket(String movieName, String date) {
        PreparedStatement ps = null;
        ArrayList<Map<String, String>> performances = new ArrayList<Map<String, String>>();

        try {
            //atomic
            conn.setAutoCommit(false);

            String sql = "INSERT INTO reservations(username, day, movie_name) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, CurrentUser.instance().getCurrentUserId());
            ps.setString(2, day);
            ps.setString(3, movieName);
            ps.executeUpdate();
            ps.close();

            sql = "SELECT t.seats - COUNT(r.nbr) AS free_seats FROM performances p JOIN theaters t ON(t.name = p.theater_name) LEFT JOIN reservations r ON(p.day = r.day AND p.movie_name = r.movie_name) WHERE p.movie_name = ? AND p.day = ? ";
            ps = conn.prepareStatement(sql);
            ps.setString(1, movieName);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            rs.next();

            if(rs.getInt("free_seats") < 0) {
                conn.rollback();
                return false;
            } else {
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
