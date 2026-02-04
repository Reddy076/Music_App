-- RevPlay Seed Data with Actual Music Files
-- Royalty-free music from Pixabay and Free Music Archive

USE revplay_db;

-- Clear existing data
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE listening_history;
TRUNCATE TABLE favorites;
TRUNCATE TABLE playlist_songs;
TRUNCATE TABLE playlists;
TRUNCATE TABLE podcasts;
TRUNCATE TABLE songs;
TRUNCATE TABLE albums;
TRUNCATE TABLE artists;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- INSERT USERS (Password: password123)
-- =====================================================
INSERT INTO users (email, password, username, role, security_question, security_answer) VALUES
('john@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqJgP7QO3hPzN7fYrDgK3PMqJnZ1m', 'john_music', 'USER', 'What is your pet name?', 'fluffy'),
('jane@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqJgP7QO3hPzN7fYrDgK3PMqJnZ1m', 'jane_beats', 'USER', 'What is your favorite color?', 'blue'),
('mike@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqJgP7QO3hPzN7fYrDgK3PMqJnZ1m', 'mike_vibes', 'USER', 'What is your birth city?', 'new york'),
('artist1@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqJgP7QO3hPzN7fYrDgK3PMqJnZ1m', 'chad_crouch', 'ARTIST', 'What is your pet name?', 'buddy'),
('artist2@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqJgP7QO3hPzN7fYrDgK3PMqJnZ1m', 'pixel_beats', 'ARTIST', 'What is your favorite color?', 'purple'),
('artist3@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqJgP7QO3hPzN7fYrDgK3PMqJnZ1m', 'dream_sounds', 'ARTIST', 'What is your birth city?', 'los angeles');

-- =====================================================
-- INSERT ARTISTS
-- =====================================================
INSERT INTO artists (user_id, artist_name, bio, genre, profile_image) VALUES
(4, 'Chad Crouch', 'Ambient and instrumental music producer creating relaxing soundscapes.', 'Ambient', 'assets/images/covers/artist1.jpg'),
(5, 'Pixel Beats', 'Electronic music producer specializing in upbeat and energetic tracks.', 'Electronic', 'assets/images/covers/artist2.jpg'),
(6, 'Dream Sounds', 'Lo-fi and chill music creator perfect for relaxation and study.', 'Lo-Fi', 'assets/images/covers/artist3.jpg');

-- =====================================================
-- INSERT ALBUMS
-- =====================================================
INSERT INTO albums (artist_id, title, description, release_date, genre) VALUES
(1, 'Peaceful Journeys', 'A collection of ambient tracks for relaxation', '2024-01-15', 'Ambient'),
(2, 'Energy Boost', 'Upbeat electronic tracks to energize your day', '2024-02-20', 'Electronic'),
(3, 'Chill Sessions', 'Lo-fi beats for studying and relaxation', '2024-03-10', 'Lo-Fi'),
(2, 'Summer Collection', 'Feel-good summer vibes', '2024-06-01', 'Electronic');

-- =====================================================
-- INSERT SONGS (with actual music files)
-- =====================================================
INSERT INTO songs (title, artist_id, album_id, genre, duration, release_date, file_path, cover_image, play_count) VALUES
-- Chad Crouch - Ambient
('Shipping Lanes', 1, 1, 'Ambient', 327, '2024-01-15', 'assets/music/shipping_lanes.mp3', 'assets/images/covers/shipping_lanes.jpg', 1250),
('Relaxing Dreams', 1, 1, 'Ambient', 114, '2024-01-20', 'assets/music/relaxing_music.mp3', 'assets/images/covers/relaxing.jpg', 890),

-- Pixel Beats - Electronic
('Electronic Future', 2, 2, 'Electronic', 196, '2024-02-20', 'assets/music/electronic_future.mp3', 'assets/images/covers/electronic.jpg', 3400),
('Happy Day', 2, 2, 'Electronic', 148, '2024-02-25', 'assets/music/happy_day.mp3', 'assets/images/covers/happy.jpg', 2800),
('Summer Vibes', 2, 4, 'Electronic', 254, '2024-06-01', 'assets/music/summer_vibes.mp3', 'assets/images/covers/summer.jpg', 4200),

-- Dream Sounds - Lo-Fi
('Lo-Fi Chill', 3, 3, 'Lo-Fi', 156, '2024-03-10', 'assets/music/lofi_chill.mp3', 'assets/images/covers/lofi.jpg', 5600),
('Dreamy Nights', 3, 3, 'Lo-Fi', 5, '2024-03-15', 'assets/music/dreamy.mp3', 'assets/images/covers/dreamy.jpg', 1800),
('Inspiring Moments', 3, 3, 'Inspirational', 3, '2024-03-20', 'assets/music/inspiring.mp3', 'assets/images/covers/inspiring.jpg', 2100);

-- =====================================================
-- INSERT PLAYLISTS
-- =====================================================
INSERT INTO playlists (user_id, name, description, is_public) VALUES
(1, 'My Favorites', 'Collection of my favorite tracks', FALSE),
(1, 'Study Session', 'Perfect for concentration', TRUE),
(2, 'Workout Mix', 'High energy tracks for exercise', TRUE),
(2, 'Chill Evening', 'Relaxing tracks for evening', TRUE),
(3, 'Morning Boost', 'Energizing morning playlist', TRUE);

-- =====================================================
-- INSERT PLAYLIST SONGS
-- =====================================================
INSERT INTO playlist_songs (playlist_id, song_id, position) VALUES
(1, 1, 1), (1, 3, 2), (1, 6, 3),
(2, 1, 1), (2, 2, 2), (2, 6, 3), (2, 7, 4),
(3, 3, 1), (3, 4, 2), (3, 5, 3),
(4, 1, 1), (4, 2, 2), (4, 6, 3), (4, 7, 4),
(5, 3, 1), (5, 4, 2), (5, 5, 3), (5, 8, 4);

-- =====================================================
-- INSERT FAVORITES
-- =====================================================
INSERT INTO favorites (user_id, song_id) VALUES
(1, 1), (1, 3), (1, 6),
(2, 2), (2, 5), (2, 6),
(3, 3), (3, 4), (3, 5);

-- =====================================================
-- INSERT LISTENING HISTORY
-- =====================================================
INSERT INTO listening_history (user_id, song_id, played_at, duration_played) VALUES
(1, 1, NOW() - INTERVAL 1 HOUR, 327),
(1, 3, NOW() - INTERVAL 2 HOUR, 196),
(1, 6, NOW() - INTERVAL 3 HOUR, 156),
(2, 5, NOW() - INTERVAL 1 HOUR, 254),
(2, 4, NOW() - INTERVAL 2 HOUR, 148),
(3, 3, NOW() - INTERVAL 30 MINUTE, 196);

SELECT 'Seed data with music files loaded successfully!' as Status;
SELECT COUNT(*) as 'Total Songs' FROM songs;
SELECT COUNT(*) as 'Total Artists' FROM artists;
SELECT COUNT(*) as 'Total Playlists' FROM playlists;
