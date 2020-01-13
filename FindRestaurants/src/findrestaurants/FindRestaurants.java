/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package findrestaurants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author Luat
 */
public class FindRestaurants {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        JdbcCheckup();
    }

    static void JdbcCheckup() {
        String dbSys = null;

        String user = "luatpham";
        String password = "luatpham";
        String connStr = "jdbc:oracle:thin:@localhost:1521:dbs3";

        System.out.println("using connection string: " + connStr);
        System.out.print("Connecting to the database...");
        System.out.flush();

        // Connect to the database
        // Use finally clause to close connection
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(connStr, user, password);
            System.out.println("connected.");
            findRestaurants(conn);
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

    static void findRestaurants(Connection conn) throws SQLException {

        Scanner in = new Scanner(System.in);
        int apartNo;
        String category;
        System.out.println("Enter category: ");
        category = in.nextLine();
        System.out.println("Enter a listing apartment number: ");
        apartNo = in.nextInt();
        Statement stmt = conn.createStatement();
        ResultSet rset = null;
        String strqr = "SELECT apartment_stats("+ apartNo + ",'" + category+ "') FROM DUAL";
        System.out.println(category + " within 200 meters of the aparment listing " + apartNo);
        System.out.printf("%20s %20s\n","Category","Count");
        rset = stmt.executeQuery(strqr);
        while(rset.next()){
            System.out.printf("%20s %20s\n", category,rset.getString(1));
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
}
