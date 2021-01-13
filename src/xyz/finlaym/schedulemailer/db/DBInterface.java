package xyz.finlaym.schedulemailer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DBInterface {
	private Connection conn;
	public void init(String dbName, String username, String password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbName, username, password);
		}catch(Exception e) {
			System.err.println("UwU DBInterface go not brrrr");
			e.printStackTrace();
		}
	}
	public ArrayList<String[]> getIDPairs() throws Exception{
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `id_pairs`;");
		ArrayList<String[]> idPairs = new ArrayList<String[]>();
		while(rs.next()) {
			String id = rs.getString("id");
			String email = rs.getString("email");
			String[] array = new String[] {id,email};
			idPairs.add(array);
		}
		rs.close();
		return idPairs;
	}
}
