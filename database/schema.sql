-- RevPlay Database Schema
-- Music Streaming Application

-- Drop existing tables if they exist
DROP DATABASE IF EXISTS revplay_db;revplay_db
CREATE DATABASE revplay_db;
USE revplay_db;

-- =====================================================
-- USERS TABLE
-- =====================================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL,
    role ENUM('USER', 'ARTIST') NOT NULL DEFAULT 'USER',
    security_question VARCHAR(255),
    security_answer VARCHAR(255),
    password_hint VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- =====================================================
-- ARTISTS TABLE
-- =====================================================
CREATE TABLE artists (
    artist_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    artist_name VARCHAR(100) NOT NULL,
    bio TEXT,
    genre VARCHAR(50),
    profile_image VARCHAR(255),
    facebook_link VARCHAR(255),
    twitter_link VARCHAR(255),
    instagram_link VARCHAR(255),
    spotify_link VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- =====================================================
-- ALBUMS TABLE
-- =====================================================
CREATE TABLE albums (
    album_id INT PRIMARY KEY AUTO_INCREMENT,
    artist_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    cover_image VARCHAR(255),
    release_date DATE,
    genre VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (artist_id) REFERENCES artists(artist_id) ON DELETE CASCADE
);

-- =====================================================
-- SONGS TABLE
-- =====================================================
CREATE TABLE songs (
    song_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    artist_id INT NOT NULL,
    album_id INT,
    genre VARCHAR(50),
    duration INT NOT NULL COMMENT 'Duration in seconds',
    release_date DATE,
    file_path VARCHAR(255),
    cover_image VARCHAR(255),
    play_count INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (artist_id) REFERENCES artists(artist_id) ON DELETE CASCADE,
    FOREIGN KEY (album_id) REFERENCES albums(album_id) ON DELETE SET NULL
);

-- =====================================================
-- PODCASTS TABLE
-- =====================================================
CREATE TABLE podcasts (
    podcast_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    artist_id INT NOT NULL,
    description TEXT,
    genre VARCHAR(50),
    duration INT NOT NULL COMMENT 'Duration in seconds',
    release_date DATE,
    file_path VARCHAR(255),
    cover_image VARCHAR(255),
    play_count INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (artist_id) REFERENCES artists(artist_id) ON DELETE CASCADE
);

-- =====================================================
-- PLAYLISTS TABLE
-- =====================================================
CREATE TABLE playlists (
    playlist_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    cover_image VARCHAR(255),
    is_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- =====================================================
-- PLAYLIST_SONGS TABLE (Junction Table)
-- =====================================================
CREATE TABLE playlist_songs (
    playlist_id INT NOT NULL,
    song_id INT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    position INT,
    PRIMARY KEY (playlist_id, song_id),
    FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE
);

-- =====================================================
-- FAVORITES TABLE
-- =====================================================
CREATE TABLE favorites (
    favorite_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    song_id INT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_favorite (user_id, song_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE
);

-- =====================================================
-- LISTENING_HISTORY TABLE
-- =====================================================
CREATE TABLE listening_history (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    song_id INT NOT NULL,
    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    duration_played INT COMMENT 'Duration played in seconds',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE
);

-- =====================================================
-- RECENTLY_PLAYED TABLE
-- =====================================================
CREATE TABLE recently_played (
    user_id INT NOT NULL,
    song_id INT NOT NULL,
    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    play_count INT DEFAULT 1 COMMENT 'Number of times this song was played',
    PRIMARY KEY (user_id, song_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE
);

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================
CREATE INDEX idx_songs_artist ON songs(artist_id);
CREATE INDEX idx_songs_album ON songs(album_id);
CREATE INDEX idx_songs_genre ON songs(genre);
CREATE INDEX idx_songs_title ON songs(title);
CREATE INDEX idx_albums_artist ON albums(artist_id);
CREATE INDEX idx_playlists_user ON playlists(user_id);
CREATE INDEX idx_favorites_user ON favorites(user_id);
CREATE INDEX idx_history_user ON listening_history(user_id);
CREATE INDEX idx_history_played_at ON listening_history(played_at);

-- =====================================================
-- VIEWS FOR COMMON QUERIES
-- =====================================================

-- View: Song details with artist name
CREATE VIEW song_details AS
SELECT 
    s.song_id,
    s.title AS song_title,
    s.genre,
    s.duration,
    s.release_date,
    s.play_count,
    s.cover_image,
    a.artist_id,
    a.artist_name,
    al.album_id,
    al.title AS album_title
FROM songs s
JOIN artists a ON s.artist_id = a.artist_id
LEFT JOIN albums al ON s.album_id = al.album_id
WHERE s.is_active = TRUE;

-- View: Popular songs
CREATE VIEW popular_songs AS
SELECT * FROM song_details
ORDER BY play_count DESC
LIMIT 50;

-- View: Recent songs
CREATE VIEW recent_songs AS
SELECT * FROM song_details
ORDER BY release_date DESC
LIMIT 50;