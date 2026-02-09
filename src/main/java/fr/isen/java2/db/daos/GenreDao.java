package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.isen.java2.db.entities.Genre;

public class GenreDao {

	public List<Genre> listGenres() {
		List<Genre> genres = new ArrayList<>();

		String sql = "SELECT idgenre, name FROM genre";

		try (Connection connection = DataSourceFactory.getConnection();
		     Statement statement = connection.createStatement();
		     ResultSet rs = statement.executeQuery(sql)) {

			while (rs.next()) {
				genres.add(new Genre(
						rs.getInt("idgenre"),
						rs.getString("name")
				));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return genres;
	}

	public Optional<Genre> getGenre(String name) {
		String sql = "SELECT idgenre, name FROM genre WHERE name = ?";

		try (Connection connection = DataSourceFactory.getConnection();
		     PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, name);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(new Genre(
							rs.getInt("idgenre"),
							rs.getString("name")
					));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return Optional.empty();
	}

	public void addGenre(String name) {
		String sql = "INSERT INTO genre(name) VALUES (?)";

		try (Connection connection = DataSourceFactory.getConnection();
		     PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, name);
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}