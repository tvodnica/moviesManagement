CREATE DATABASE Movies
GO
USE Movies
GO

/*------ ADMINISTRATION --------*/

CREATE TABLE Users(
IDUser int PRIMARY KEY IDENTITY,
UserName NVARCHAR(50) UNIQUE NOT NULL,
UserPassword NVARCHAR(MAX) NOT NULL,
UserRole NVARCHAR(50)
)
GO 

INSERT INTO Users(UserName, UserPassword, UserRole)
VALUES ('admin', 'admin', 'admin')
GO

CREATE PROCEDURE createUser
@username NVARCHAR(50), @password NVARCHAR(50)
AS
BEGIN
INSERT INTO Users(UserName, UserPassword, UserRole)
VALUES (@username, @password, 'user')
END
GO

CREATE PROCEDURE login
@username NVARCHAR(50), @password NVARCHAR(50),
@userRole NVARCHAR(50) OUTPUT
AS
BEGIN
SELECT @userRole = u.UserRole FROM Users AS u
WHERE u.UserName = @username AND u.UserPassword = @password
END


/*---- CREATE TABLES ----*/

CREATE TABLE Movie(
IDMovie INT PRIMARY KEY IDENTITY,
Title NVARCHAR(50),
PublishedDate NVARCHAR(50),
Description NVARCHAR(500),
OriginalName NVARCHAR(50),
Duration INT,
PicturePath NVARCHAR(100)
)

GO
CREATE TABLE Actor(
IDActor INT PRIMARY KEY IDENTITY,
Name NVARCHAR(50),
LastName NVARCHAR(50)
)

GO
CREATE TABLE Genre(
IDGenre INT PRIMARY KEY IDENTITY,
Name NVARCHAR(50)
)

GO
CREATE TABLE MovieActors(
IDMovieActors INT PRIMARY KEY IDENTITY,
MovieID INT FOREIGN KEY REFERENCES Movie(IDMovie),
ActorID INT FOREIGN KEY REFERENCES Actor(IDActor)
)

GO
CREATE TABLE MovieDirectors(
IDMovieDirectors INT PRIMARY KEY IDENTITY,
MovieID INT FOREIGN KEY REFERENCES Movie(IDMovie),
ActorID INT FOREIGN KEY REFERENCES Actor(IDActor),
)
GO
CREATE TABLE MovieGenres(
IDGenres INT PRIMARY KEY IDENTITY,
MovieID INT FOREIGN KEY REFERENCES Movie(IDMovie),
GenreID INT FOREIGN KEY REFERENCES Genre(IDGenre),
)

GO

/*---- UPLOAD ----*/

GO
CREATE PROCEDURE uploadMovie
@title NVARCHAR(50),
@publishedDate DATETIME,
@description NVARCHAR(500),
@originalName NVARCHAR(50),
@duration INT,
@picturePath NVARCHAR(50),
@id INT OUTPUT
AS
BEGIN
INSERT INTO Movie(Title, PublishedDate, Description, OriginalName, Duration, PicturePath)
VALUES (@title, @publishedDate,@description, @originalName, @duration, @picturePath)
SET @id = SCOPE_IDENTITY()
END
GO
CREATE PROCEDURE uploadMovieActors
@movieid INT, @firstName NVARCHAR(50), @lastName NVARCHAR(50)
AS
BEGIN

IF
(SELECT COUNT(*) FROM Actor as a
WHERE a.Name = @firstName AND a.LastName = @lastName) = 0
	BEGIN 
		INSERT INTO Actor(Name, LastName) VALUES(@firstName, @lastName)
		INSERT INTO MovieActors(ActorID, MovieID) VALUES(SCOPE_IDENTITY(), @movieid)
	END

ELSE BEGIN
DECLARE @actorID INT
SELECT @actorID = a.IDActor FROM Actor as a
WHERE a.Name = @firstName AND a.LastName = @lastName
INSERT INTO MovieActors(ActorID, MovieID) VALUES(@actorID, @movieid)
END

END
GO
ALTER PROCEDURE uploadMovieDirectors
@movieid INT, @firstName NVARCHAR(50), @lastName NVARCHAR(50)
AS
BEGIN
IF
(SELECT COUNT(*) FROM Actor as a
WHERE a.Name = @firstName AND a.LastName = @lastName) = 0
	BEGIN 
		INSERT INTO Actor(Name, LastName) VALUES(@firstName, @lastName)
		INSERT INTO MovieDirectors(ActorID, MovieID) VALUES(SCOPE_IDENTITY(), @movieid)
	END
	
ELSE BEGIN
DECLARE @directorID INT
SELECT @directorID = a.IDActor FROM Actor as a
WHERE a.Name = @firstName AND a.LastName = @lastName
INSERT INTO MovieDirectors(ActorID, MovieID) VALUES(@directorID, @movieid)
END
END

GO
ALTER PROCEDURE uploadMovieGenres
@movieid INT, @genreName NVARCHAR(50)
AS
BEGIN
IF
(SELECT COUNT(*) FROM Genre as g
WHERE g.Name = @genreName) = 0
	BEGIN 
		INSERT INTO Genre(Name) VALUES(@genreName)
		INSERT INTO MovieGenres(GenreID, MovieID) VALUES(SCOPE_IDENTITY(), @movieid)
	END

ELSE BEGIN
DECLARE @genreID INT
SELECT @genreID = g.IDGenre FROM GENRE as g
WHERE g.Name = @genreName
INSERT INTO MovieGenres(GenreID, MovieID) VALUES(@genreID, @movieid)
END

END
GO

/*---- DELETE ----*/

ALTER PROCEDURE deleteAllMovies
AS
BEGIN 
SELECT m.picturePath FROM Movie as m
DELETE FROM MovieActors
DELETE FROM MovieDirectors
DELETE FROM MovieGenres
DELETE FROM Movie
END

GO
CREATE PROCEDURE deleteMovie
@movieId INT
AS
BEGIN 
DELETE FROM MovieActors
WHERE MovieID = @movieId
DELETE FROM MovieDirectors
WHERE MovieID = @movieId
DELETE FROM MovieGenres
WHERE MovieID = @movieId
DELETE FROM MOVIE
WHERE IDMovie = @movieId
END

GO

/*---- UPDATE ----*/

CREATE PROCEDURE updateMovie
@id INT,
@title NVARCHAR(50),
@publishedDate NVARCHAR(50),
@description NVARCHAR(500),
@originalName NVARCHAR(50),
@duration INT,
@picturePath NVARCHAR(50)
AS
BEGIN
UPDATE Movie
SET Title = @title,
PublishedDate = @publishedDate,
Description = @description,
OriginalName = @originalName,
Duration = @duration,
PicturePath = @picturePath
WHERE IDMovie = @id

DELETE FROM MovieActors
WHERE MovieID = @id

DELETE FROM MovieDirectors
WHERE MovieID = @id

DELETE FROM MovieGenres
WHERE MovieID = @id

END
GO
CREATE PROCEDURE updateMovieActors
@movieID INT, @firstName NVARCHAR(50), @lastName NVARCHAR(50)
AS
BEGIN

DECLARE @actorID INT
SELECT @actorID = a.IDActor FROM Actor as a
WHERE a.Name = @firstName AND a.LastName = @lastName

INSERT INTO MovieActors(ActorID, MovieID)
VALUES(@actorID, @movieID)

END
GO
CREATE PROCEDURE updateMovieDirectors
@movieID INT, @firstName NVARCHAR(50), @lastName NVARCHAR(50)
AS
BEGIN

DECLARE @directorID INT
SELECT @directorID = a.IDActor FROM Actor as a
WHERE a.Name = @firstName AND a.LastName = @lastName

INSERT INTO MovieDirectors(ActorID, MovieID)
VALUES(@directorID, @movieID)

END
GO
CREATE PROCEDURE updateMovieGenres
@movieID INT, @name NVARCHAR(50)
AS
BEGIN

DECLARE @genreID INT
SELECT @genreID = g.IDGenre FROM Genre as g
WHERE g.Name = @name

INSERT INTO MovieGenres(GenreID, MovieID)
VALUES(@genreID, @movieID)

END
GO

/*---- GET ----*/

CREATE PROCEDURE getAllActors
AS
BEGIN 
SELECT * FROM Actor
END
GO
CREATE PROCEDURE getAllGenres
AS
BEGIN 
SELECT * FROM Genre
END
GO
ALTER PROCEDURE getAllMovies
AS
BEGIN 
SELECT * FROM Movie as m
END
GO
ALTER PROCEDURE getMovieActors
@idmovie int
AS
BEGIN 
SELECT * FROM Actor as a
INNER JOIN MovieActors as ma
ON a.IDActor = ma.ActorID
WHERE ma.MovieID = @idmovie
END

GO
CREATE PROCEDURE getMovieDirectors
@idmovie int
AS
BEGIN 
SELECT * FROM Actor as a
INNER JOIN MovieDirectors as md
ON a.IDActor = md.ActorID
WHERE md.MovieID = @idmovie
END
GO
CREATE PROCEDURE getMovieGenres
@idmovie int
AS
BEGIN 
SELECT * FROM Genre as g
INNER JOIN MovieGenres as md
ON g.IDGenre = md.GenreID
WHERE md.MovieID = @idmovie
END
GO

/*---- TESTS ----*/

SELECT*FROM MOVIE WHERE IDMovie = 1795
SELECT*FROM ACTOR
SELECT * FROM  MovieActors WHERE MovieID = 1852
SELECT * FROM  MovieDirectors
SELECT * FROM  MovieGenres
