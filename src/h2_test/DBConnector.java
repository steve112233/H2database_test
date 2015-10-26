package h2_test;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Properties;

public class DBConnector implements Serializable {
	private Connection conn;
	private PreparedStatement stmtNumbercountTable;
	private PreparedStatement stmtSelectAllFonNumberCountTable;
	private PreparedStatement stmtgetValByKey;
	private PreparedStatement stmtupdateRow;
	private PreparedStatement stmtInsertRow;

	private String colNameKey = "number";
	private String colNameVal = "count";
	private String tableName = "";
	private String databasePath = "";

	public DBConnector() {
		this("TempTable");
	}

	public DBConnector(String tableName) {
		this(tableName, "/tmp/mytest.h2.db");
	}

	public DBConnector(String tableName, String pathToDatabase) {
		this.tableName = tableName;
		this.databasePath = pathToDatabase;
		this.connect(pathToDatabase);
		try {

			Statement stmt = conn.createStatement();
			
			try {
				stmt.execute("DROP TABLE " + this.tableName);
				System.out.println("Table Dropped");
			} catch (Exception e) {
				//do Nothing
			}
			String s = "CREATE TABLE " + this.tableName + " (" + colNameKey + " INT PRIMARY KEY," + colNameVal
					+ " INT NOT NULL)";
			System.out.println(s);
			stmtNumbercountTable = conn.prepareStatement(s);
			stmtNumbercountTable.execute();
			stmtSelectAllFonNumberCountTable = conn
					.prepareStatement("SELECT " + colNameKey + ", " + colNameVal + " FROM " + this.tableName);
			stmtgetValByKey = conn.prepareStatement(
					"SELECT " + this.colNameVal + " FROM " + this.tableName + " " + "WHERE " + colNameKey + " = ?");
			stmtupdateRow = conn.prepareStatement("update " + this.tableName + " set " + colNameVal + " = " + colNameVal
					+ "+1 where " + colNameKey + " = ?");
			stmtInsertRow = conn.prepareStatement(
					"insert into " + this.tableName + " (" + colNameKey + ", " + colNameVal + ") values (?, 1)");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void connect(String dbName) {
		conn = null;
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection("jdbc:h2:" + dbName);
			conn.setAutoCommit(true);
			System.out.println("Opened database " + dbName + " successfully");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

	}

	public void disconnect() {
		try {
			conn.commit();
			conn.close();
			System.out.println("Closed database successfully");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

	}

	public void createNumbercountTable() {
		try {
			boolean ok = stmtNumbercountTable.execute();
			if (ok) {
				System.out.println("Table " + this.tableName + " successfully created");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean addOrIncreaseValue(int key) {
		try {
			// performanter wäre erst updaten und bei fehler Datensatz
			// hinzufügen
			stmtgetValByKey.setInt(1, key);
			ResultSet res = stmtgetValByKey.executeQuery();
			if (res.next()) {
				stmtupdateRow.setInt(1, key);
				stmtupdateRow.executeUpdate();
			} else {
				stmtInsertRow.setInt(1, key);
				stmtInsertRow.executeUpdate();
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Map<Integer, Integer> tableToKVMap() {
		Map<Integer, Integer> resMap = new HashMap<Integer, Integer>();
		try {
			ResultSet result = stmtSelectAllFonNumberCountTable.executeQuery();
			while (result.next()) {
				resMap.put(result.getInt(this.colNameKey), result.getInt(this.colNameVal));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resMap;
	}

	public void logDBValues() {
		Map<Integer, Integer> res = this.tableToKVMap();
		Set<Entry<Integer, Integer>> test = res.entrySet();
		for (Entry<Integer, Integer> entry : test) {
			System.out.println(entry.getKey() + ", " + entry.getValue());
		}
	}

}
