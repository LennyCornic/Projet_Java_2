package fr.isen.java2.db.daos;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDaoTestCase {

	@BeforeEach
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getConnection();
		Statement stmt = connection.createStatement();

		stmt.executeUpdate(
			"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name VARCHAR(50) NOT NULL);"
		);

		stmt.executeUpdate(
			"CREATE TABLE IF NOT EXISTS movie ("
			+ "idmovie INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "title VARCHAR(100) NOT NULL,"
			+ "release_date DATETIME NULL,"
			+ "genre_id INT NOT NULL,"
			+ "duration INT NULL,"
			+ "director VARCHAR(100) NOT NULL,"
			+ "summary MEDIUMTEXT NULL,"
			+ "CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre)"
			+ ");"
		);

		stmt.executeUpdate("DELETE FROM movie");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='movie'");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='genre'");

		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");

		stmt.executeUpdate(
			"INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
			+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00', 1, 120, 'director 1', 'summary 1')"
		);

		stmt.executeUpdate(
			"INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
			+ "VALUES (2, 'Title 2', '2015-11-14 12:00:00', 2, 114, 'director 2', 'summary 2')"
		);

		stmt.executeUpdate(
			"INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
			+ "VALUES (3, 'Title 3', '2015-12-12 12:00:00', 2, 176, 'director 3', 'summary 3')"
		);

		stmt.close();
		connection.close();
	}

	@Test
	public void shouldListMovies() {
		MovieDao movieDao = new MovieDao();

		List<Movie> movies = movieDao.listMovies();

		assertThat(movies).hasSize(3);
	}

	@Test
	public void shouldListMoviesByGenre() {
		MovieDao movieDao = new MovieDao();

		List<Movie> movies = movieDao.listMoviesByGenre("Comedy");

		assertThat(movies).hasSize(2);
		assertThat(movies)
			.extracting(m -> m.getGenre().getName())
			.containsOnly("Comedy");
	}

	@Test
	public void shouldAddMovie() {
		MovieDao movieDao = new MovieDao();
		Genre comedy = new Genre(2, "Comedy");

		Movie movie = new Movie(
			"New Movie",
			LocalDate.of(2020, 1, 1),
			comedy,
			100,
			"New Director",
			"New summary"
		);

		Movie insertedMovie = movieDao.addMovie(movie);

		assertThat(insertedMovie).isNotNull();
		assertThat(insertedMovie.getId()).isNotNull();
		assertThat(insertedMovie.getTitle()).isEqualTo("New Movie");
	}
}