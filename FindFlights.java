/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package findflights;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author LuatPham
 */
public class FindFlights {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        // TODO code application logic here
        // Check the connection to server

        JdbcCheckup();

    }

    static void JdbcCheckup() {
        String dbSys = null;

        String user = "luatpham";
        String password = "luatpham";
        String connStr = "jdbc:oracle:thin:@localhost:1521:dbs3";
        String yesNo;

        System.out.println("using connection string: " + connStr);
        System.out.print("Connecting to the database...");
        System.out.flush();

        // Connect to the database
        // Use finally clause to close connection
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(connStr, user, password);
            System.out.println("connected.");
            findflights(conn);
        } catch (SQLException e) {
            System.out.println("Problem with JDBC Connection\n");
            printSQLException(e);
            System.exit(4);
        } finally {
            // Close the connection, if it was obtained, no matter what happens
            // above or within called methods
            if (conn != null) {
                try {
                    conn.close(); // this also closes the Statement and
                    // ResultSet, if any
                } catch (SQLException e) {
                    System.out.println("Problem with closing JDBC Connection\n");
                    printSQLException(e);
                    System.exit(5);
                }
            }
        }
    }

    static void findflights(Connection conn) throws SQLException {
        // Input Information
        String origin, destination;
        Scanner input = new Scanner(System.in);
        System.out.println("Enter Flight Origin: ");
        origin = input.nextLine();
        System.out.println("Enter Flight Destination: ");
        destination = input.nextLine();
        // Create a statement
        Statement stmt = conn.createStatement();
        String strqr = "select * from flights f where f.origin = '" + origin
                + "' and f.destination = '" + destination + "'";
        ResultSet rset = null;
        try {
            rset = stmt.executeQuery(strqr);
            Timestamp ts1;
            Timestamp ts2;
            System.out.println("Flight Information\n" +
                        "Flight No. \tOrigin  \tDestination    \tDistance   \tDuration");
            while (rset.next()) {
                ts1 = rset.getTimestamp(5);
                ts2 = rset.getTimestamp(6);          
                System.out.printf( "%5s %20s %15s %10s %17s\n",
                        rset.getString(1), 
                        rset.getString(2),
                        rset.getString(3),
                        rset.getString(4),
                        getTimeDifference(ts1, ts2));
            }
        } finally {   // Note: try without catch: let the caller handle
            // any exceptions of the "normal" db actions. 
            stmt.close(); // clean up statement resources, incl. rset
        }
    }

    // print out all exceptions connected to e by nextException or getCause
    static void printSQLException(SQLException e) {
        // SQLExceptions can be delivered in lists (e.getNextException)
        // Each such exception can have a cause (e.getCause, from Throwable)
        while (e != null) {
            System.out.println("SQLException Message:" + e.getMessage());
            Throwable t = e.getCause();
            while (t != null) {
                System.out.println("SQLException Cause:" + t);
                t = t.getCause();
            }
            e = e.getNextException();
        }
    }

    static String getTimeDifference(Timestamp ts1, Timestamp ts2) {
        // get time difference in seconds
        long milliseconds = ts2.getTime() - ts1.getTime();
        int seconds = (int) milliseconds / 1000;

        // calculate hours minutes and seconds
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = (seconds % 3600) % 60;
        String time_diff = hours + ":" + minutes +":" +seconds;
        return time_diff;
    }

    // super-simple prompted input from user
    public static String readEntry(Scanner in, String prompt)
            throws IOException {
        System.out.print(prompt);
        return in.nextLine().trim();
    }
}
