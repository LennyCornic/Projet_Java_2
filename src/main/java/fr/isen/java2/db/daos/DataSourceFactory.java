package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSourceFactory {

	private static final String URL = "jdbc:sqlite:sqlite.db";

	private DataSourceFactory() {
		throw new IllegalStateException("Utility class");
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL);
	}
}