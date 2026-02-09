package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDao {

	public List<Movie> listMovies() {
		List<Movie> movies = new ArrayList<>();

		String sql = """
				SELECT m.*, g.idgenre, g.name
				FROM movie m
				JOIN genre g ON m.genre_id = g.idgenre
				""";

		try (Connection connection = DataSourceFactory.getConnection();
		     Statement statement = connection.createStatement();
		     ResultSet rs = statement.executeQuery(sql)) {

			while (rs.next()) {
				movies.add(buildMovie(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return movies;
	}

	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> movies = new ArrayList<>();

		String sql = """
				SELECT m.*, g.idgenre, g.name
				FROM movie m
				JOIN genre g ON m.genre_id = g.idgenre
				WHERE g.name = ?
				""";

		try (Connection connection = DataSourceFactory.getConnection();
		     PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, genreName);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					movies.add(buildMovie(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return movies;
	}

	public Movie addMovie(Movie movie) {
		String sql = """
				INSERT INTO movie(title, release_date, genre_id, duration, director, summary)
				VALUES (?, ?, ?, ?, ?, ?)
				""";

		try (Connection connection = DataSourceFactory.getConnection();
		     PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, movie.getTitle());
			ps.setTimestamp(2, Timestamp.valueOf(movie.getReleaseDate().atStartOfDay()));
			ps.setInt(3, movie.getGenre().getId());
			ps.setInt(4, movie.getDuration());
			ps.setString(5, movie.getDirector());
			ps.setString(6, movie.getSummary());

			ps.executeUpdate();

			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (keys.next()) {
					movie.setId(keys.getInt(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return movie;
	}

	private Movie buildMovie(ResultSet rs) throws SQLException {
		Genre genre = new Genre(
				rs.getInt("idgenre"),
				rs.getString("name")
		);

		return new Movie(
				rs.getInt("idmovie"),
				rs.getString("title"),
				rs.getTimestamp("release_date").toLocalDateTime().toLocalDate(),
				genre,
				rs.getInt("duration"),
				rs.getString("director"),
				rs.getString("summary")
		);
	}
}