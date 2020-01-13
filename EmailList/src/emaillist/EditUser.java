/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emaillist;

import static emaillist.EmailList.addMember;
import static emaillist.EmailList.getConnected;
import static emaillist.EmailList.readEntry;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luat
 */
public class EditUser {
     public static void main(String[] args) {
        String dbSys = null;
		Scanner in = null;
		try {
			in = new Scanner(System.in);
			System.out.println("Please enter information for connection to the database");
			dbSys = readEntry(in, "Using Oracle (o) or MySql (m)? ");

		} catch (IOException e) {
			System.out.println("Problem with user input, please try again\n");
			System.exit(1);
		}
		// Prompt the user for connect information
		String username = null;
		String password = null;
		String connStr = null;
		String yesNo;
		try {
			if (dbSys.equals("o")) {
				username = readEntry(in, "Oracle username: ");
				password = readEntry(in, "Oracle password: ");
				yesNo = readEntry(in, "use canned Oracle connection string (y/n): ");
				if (yesNo.equals("y")) {
					String host = readEntry(in, "host: ");
					String port = readEntry(in, "port (often 1521): ");
					String sid = readEntry(in, "sid (site id): ");
					connStr = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;
				} else {
					connStr = readEntry(in, "connection string: ");
				}
			} else if (dbSys.equals("m")) {// MySQL--
				username = readEntry(in, "MySQL username: ");
				password = readEntry(in, "MySQL password: ");
				yesNo = readEntry(in, "use canned MySql connection string (y/n): ");
				if (yesNo.equals("y")) {
					String host = readEntry(in, "host: ");
					String port = readEntry(in, "port (often 3306): ");
					String db = username + "db";
					connStr = "jdbc:mysql://" + host + ":" + port + "/" + db;
				} else {
					connStr = readEntry(in, "connection string: ");
				}
			}
		} catch (IOException e) {
			System.out.println("Problem with user input, please try again\n");
			System.exit(3);
		}
		System.out.println("using connection string: " + connStr);
		System.out.print("Connecting to the database...");
		Connection conn = null;
		try {
			conn = getConnected(connStr, username, password);
			UserDB.setConnection(conn);  // let UserDB know connection
                        editMember(in,conn);
		} catch (SQLException e) {
			System.out.println("Problem with JDBC Connection\n");
			printSQLException(e);
			System.exit(4);
		} finally {
			closeConnection(conn);
		}
	}
     
     public static void editMember(Scanner in, Connection conn){
         String firstName;
         String lastName;
         String email;
         System.out.println("To edit a member of the email list, enter user email:");
         email = in.nextLine();
         try {
             if(UserDB.emailExists(email)){
                 User user = UserDB.selectUser(email);
                 System.out.println("Please enter your new first name: ");
                 firstName = in.nextLine();
                 System.out.println("Please enter your new last name:");
                 lastName = in.nextLine();
                 if(firstName.isEmpty() && lastName.isEmpty()){
                     System.out.println("Your information has no change.");
                     return;
                 }else if(firstName.isEmpty() || lastName.isEmpty()){
                     if(firstName.isEmpty()){
                         user.setLastName(lastName);
                     }else{
                         user.setFirstName(firstName);
                     }
                 }else{
                     user.setFirstName(firstName);
                     user.setLastName(lastName);
                 }
                 User user2 = user;
                 UserDB.delete(user);
                 UserDB.insert(user2);
                 System.out.println("Your email information has been changed !");
             }else{
                 System.out.println("The email you entered does not exit in the email list.");
             }
         } catch (SQLException ex) {
             Logger.getLogger(EditUser.class.getName()).log(Level.SEVERE, null, ex);
         }
     }
     
     public static Connection getConnected(String connStr, String user, String password) throws SQLException {

		System.out.println("using connection string: " + connStr);
		System.out.print("Connecting to the database...");
		System.out.flush();

		// Connect to the database
		Connection conn = DriverManager.getConnection(connStr, user, password);
		System.out.println("connected.");
		return conn;
	}
	
	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close(); // this also closes the Statement and
								// ResultSet, if any
			} catch (SQLException e) {
				System.out
						.println("Problem with closing JDBC Connection\n");
				printSQLException(e);
			}
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

	// super-simple prompted input from user
	public static String readEntry(Scanner in, String prompt) throws IOException {
		System.out.print(prompt);
		return in.nextLine().trim();
	}
    
}
