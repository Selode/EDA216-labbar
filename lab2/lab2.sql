-- delete the tables if they exist
-- Disable foreign key checks, so the tables can be dropped in arbitrary order
PRAGMA foreign_keys=OFF;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS theaters;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS showings;
DROP TABLE IF EXISTS reservations;
PRAGMA foreign_keys=ON;

-- create the tables
CREATE TABLE users{
	username varchar(20) PRIMARY KEY,
	name varchar(20),
	address varchar(40),
	telephone varchar(10) NOT NULL
};

CREATE TABLE theaters{
	name varchar(20) PRIMARY KEY,
	seats integer NOT NULL
};

CREATE TABLE movies{
	name varchar(30) PRIMARY KEY
};

CREATE TABLE showings{
	movie_name varchar(30) REFERENCES movies(name),
	theater_name varchar(20) REFERENCES theaters(name),
	day date,
	PRIMARY KEY(movie_name, day)
};

CREATE TABLE reservations{
	nbr integer PRIMARY KEY,
	username varchar(20) REFERENCES users(username),
	movie_name varchar(30),
	day date,
	FOREIGN KEY (movie_name, day) REFERENCES showings(movie_name,day),
	UNIQUE (username, movie_name, day)
};

INSERT INTO users (username, address, telephone) VALUES
('BruceWayne', null, '0 123 456'),
('ClarkKent', null, '0 234 567'),
('JessicaJones', null, '1 345 678'),
('DianaPrince', null, '1 234 567'),
('ArthurCurry', null, '1 234 567'),
('LukeSkywalker', null, '1 234 567'),
('LeiaOrgana', null, '1 234 567');

INSERT INTO theaters (name, seats) VALUES
('Malmö', 350),
('Lund', 80),
('Helsingborg', 140);

INSERT INTO movies (name) VALUES
('Star Wars: The Last Jedi'),
('Logan'),
('Guardians of the Galaxy: vol 2'),
('Baywatch'),
('Wonder Woman'),
('John Wick'),
('Spiderman: Homecoming'),
('La la land'),
('Justice League'),
('LEGO: Batman'),

INSERT INTO showings (movie_name, theater_name, day) VALUES
('Logan', 'Malmö', '2017-03-01'),
('Logan', 'Lund', '2017-03-02'),
('Logan', 'Helsingborg', '2017-03-03');

INSERT INTO reservations (username, movie_name, day) VALUES
('LukeSkywalker', 'Logan', '2017-03-01'),
('BruceWayne', 'Logan', '2017-03-02');

SELECT "1) SELECT * FROM movies";
SELECT * FROM movies;

SELECT "2) SELECT day FROM showings WHERE movie_name = 'Logan'";
SELECT day FROM showings WHERE movie_name = 'Logan';

SELECT "SELECT nbr, username FROM reservations WHERE movie_name = 'Logan' AND day = '2017-03-01';";
SELECT nbr, username FROM reservations WHERE movie_name = 'Logan' AND day = '2017-03-04';
