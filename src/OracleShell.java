import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class OracleShell {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		String db_ip, db_port, db_sid; 
		String db_user;
		String db_password;
		
		System.err.println("OracleShell (C) 2016 KIM Taegyoon");
		System.err.println("Enter a SQL statement without an ending semicolon.");
		
		System.err.print("DB IP: ");
		db_ip = in.readLine();
		
		System.err.print("DB Port(e.g. 1521): ");
		db_port = in.readLine();
		
		System.err.print("DB SID: ");
		db_sid = in.readLine();

		// "jdbc:oracle:thin:@IP:PORT:SID";
		String db_url = String.format("jdbc:oracle:thin:@%s:%s:%s", db_ip, db_port, db_sid);
		System.err.println("DB URL: " + db_url);
		
		System.err.print("DB User: ");
		db_user = in.readLine();
		
		System.err.print("DB Password: ");
		db_password = in.readLine();

		try {
			Connection conn;

			conn = DriverManager.getConnection(db_url, db_user, db_password);
			conn.setAutoCommit(false);;
			System.err.println("Autocommit: " + conn.getAutoCommit());
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				Statement stmt = null;
				ResultSet rs = null;
				try {
					System.err.print("SQL> ");
					// String query = "select * from foo";
					String query = br.readLine();
					if (query == null) break;
					
					stmt = conn.createStatement();
					
					if (query.toLowerCase().startsWith("select ")) {	
						rs = stmt.executeQuery(query);

						// header
						ResultSetMetaData rsmd = rs.getMetaData();
						int numCol = rsmd.getColumnCount();
						for (int i = 1; i <= numCol; i++) {
							if (i > 1)
								System.out.print("\t");
							System.out.print(rsmd.getColumnLabel(i));
						}
						System.out.println();

						// data
						while (rs.next()) {
							for (int i = 1; i <= numCol; i++) {
								if (i > 1)
									System.out.print("\t");
								System.out.print(rs.getString(i));
							}
							System.out.println();
						}
					} else {
						// insert, delete, update: returns number of affected rows
						// create, drop: returns -1
						// commit: returns 0
						int ret = stmt.executeUpdate(query);
						System.out.println("Result: " + ret);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (stmt != null) stmt.close();
					if (rs != null) rs.close();
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
