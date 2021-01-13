package xyz.finlaym.schedulemailer.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM `id_pairs`;");
		ResultSet rs = statement.executeQuery();
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
	public java.util.Date getLastUpdated(String id) throws Exception{
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM `id_updated` WHERE `id` = ?;");
		statement.setString(1, id);
		ResultSet rs = statement.executeQuery();
		if(!rs.next())
			return null;
		java.util.Date d = rs.getDate("date");
		return d;
	}
	public void setLastUpdated(String id, java.util.Date date) throws Exception{
		Date sqlDate = new Date(date.getTime());
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM `id_updated` WHERE `id` = ?;");
		statement.setString(1, id);
		ResultSet rs = statement.executeQuery();
		rs.last();
		if(rs.getRow() == 0) {
			PreparedStatement pS = conn.prepareStatement("INSERT INTO `id_updated` (`id`, `date`) VALUES(?,?);");
			pS.setString(1, id);
			pS.setDate(2, sqlDate);
			pS.executeUpdate();
		}else {
			PreparedStatement pS = conn.prepareStatement("UPDATE `id_updated` SET `date` = ? WHERE `id` = ?;");
			pS.setString(2, id);
			pS.setDate(1, sqlDate);
			pS.executeUpdate();
		}
	}
}
