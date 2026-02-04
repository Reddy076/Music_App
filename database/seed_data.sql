-- RevPlay Seed Data
-- Sample data for testing

USE revplay_db;

-- =====================================================
-- INSERT SAMPLE USERS
-- =====================================================
-- Password is 'password123' hashed with BCrypt (cost factor 12)
INSERT INTO users (email, password, username, role, security_question, security_answer) VALUES
('john.listener@email.com', '$2a$12$xoUdX5azVmtRxN3qvRLX8unl.RBm70v3sJLBP1wlCOI8ar.3GNzK2', 'john_listener', 'USER', 'What is your pet name?', 'fluffy'),
('jane.listener@email.com', '$2a$12$xoUdX5azVmtRxN3qvRLX8unl.RBm70v3sJLBP1wlCOI8ar.3GNzK2', 'jane_music', 'USER', 'What is your favorite color?', 'blue'),
('mike.listener@email.com', '$2a$12$xoUdX5azVmtRxN3qvRLX8unl.RBm70v3sJLBP1wlCOI8ar.3GNzK2', 'mike_beats', 'USER', 'What is your birth city?', 'new york'),
('artist1@email.com', '$2a$12$xoUdX5azVmtRxN3qvRLX8unl.RBm70v3sJLBP1wlCOI8ar.3GNzK2', 'taylor_swift', 'ARTIST', 'What is your pet name?', 'meredith'),
('artist2@email.com', '$2a$12$xoUdX5azVmtRxN3qvRLX8unl.RBm70v3sJLBP1wlCOI8ar.3GNzK2', 'ed_sheeran', 'ARTIST', 'What is your favorite color?', 'orange'),
('artist3@email.com', '$2a$12$xoUdX5azVmtRxN3qvRLX8unl.RBm70v3sJLBP1wlCOI8ar.3GNzK2', 'weeknd_official', 'ARTIST', 'What is your birth city?', 'toronto'),
('artist4@email.com', '$2a$12$xoUdX5azVmtRxN3qvRLX8unl.RBm70v3sJLBP1wlCOI8ar.3GNzK2', 'dua_lipa', 'ARTIST', 'What is your pet name?', 'dexter'),
('artist5@email.com', '$2a$12$xoUdX5azVmtRxN3qvRLX8unl.RBm70v3sJLBP1wlCOI8ar.3GNzK2', 'arijit_singh', 'ARTIST', 'What is your favorite color?', 'white');

-- =====================================================
-- INSERT SAMPLE ARTISTS
-- =====================================================
INSERT INTO artists (user_id, artist_name, bio, genre, profile_image) VALUES
(4, 'Taylor Swift', 'American singer-songwriter known for narrative songs about her personal life.', 'Pop', 'taylor_swift.jpg'),
(5, 'Ed Sheeran', 'English singer-songwriter known for acoustic pop and folk music.', 'Pop', 'ed_sheeran.jpg'),
(6, 'The Weeknd', 'Canadian singer known for his sonic versatility and dark lyricism.', 'R&B', 'weeknd.jpg'),
(7, 'Dua Lipa', 'English singer known for her disco-influenced pop music.', 'Pop', 'dua_lipa.jpg'),
(8, 'Arijit Singh', 'Indian playback singer known for Bollywood songs.', 'Bollywood', 'arijit_singh.jpg');

-- =====================================================
-- INSERT SAMPLE ALBUMS
-- =====================================================
INSERT INTO albums (artist_id, title, description, release_date, genre) VALUES
(1, '1989', 'Fifth studio album marking transition to synth-pop.', '2014-10-27', 'Pop'),
(1, 'Midnights', 'Tenth studio album exploring sleepless nights.', '2022-10-21', 'Pop'),
(2, 'Divide', 'Third studio album featuring Castle on the Hill.', '2017-03-03', 'Pop'),
(2, 'Equals', 'Fourth studio album about love and loss.', '2021-10-29', 'Pop'),
(3, 'After Hours', 'Fourth studio album with Blinding Lights.', '2020-03-20', 'R&B'),
(3, 'Dawn FM', 'Fifth studio album with 80s synth-pop influence.', '2022-01-07', 'R&B'),
(4, 'Future Nostalgia', 'Second studio album with disco-pop sound.', '2020-03-27', 'Pop'),
(5, 'Best of Arijit Singh', 'Collection of greatest Bollywood hits.', '2020-01-01', 'Bollywood');

-- =====================================================
-- INSERT SAMPLE SONGS
-- =====================================================
INSERT INTO songs (title, artist_id, album_id, genre, duration, release_date, play_count) VALUES
-- Taylor Swift Songs
('Shake It Off', 1, 1, 'Pop', 219, '2014-08-18', 15000),
('Blank Space', 1, 1, 'Pop', 231, '2014-10-27', 18000),
('Style', 1, 1, 'Pop', 231, '2014-10-27', 12000),
('Anti-Hero', 1, 2, 'Pop', 200, '2022-10-21', 25000),
('Lavender Haze', 1, 2, 'Pop', 202, '2022-10-21', 14000),

-- Ed Sheeran Songs
('Shape of You', 2, 3, 'Pop', 234, '2017-01-06', 50000),
('Castle on the Hill', 2, 3, 'Pop', 261, '2017-01-06', 22000),
('Perfect', 2, 3, 'Pop', 263, '2017-03-03', 35000),
('Bad Habits', 2, 4, 'Pop', 231, '2021-06-25', 28000),
('Shivers', 2, 4, 'Pop', 207, '2021-09-10', 20000),

-- The Weeknd Songs
('Blinding Lights', 3, 5, 'R&B', 200, '2019-11-29', 60000),
('Save Your Tears', 3, 5, 'R&B', 215, '2020-03-20', 32000),
('After Hours', 3, 5, 'R&B', 361, '2020-02-19', 18000),
('Take My Breath', 3, 6, 'R&B', 279, '2021-08-06', 15000),
('Sacrifice', 3, 6, 'R&B', 189, '2022-01-07', 12000),

-- Dua Lipa Songs
('Don''t Start Now', 4, 7, 'Pop', 183, '2019-11-01', 40000),
('Levitating', 4, 7, 'Pop', 203, '2020-03-27', 45000),
('Physical', 4, 7, 'Pop', 194, '2020-01-31', 25000),
('Break My Heart', 4, 7, 'Pop', 222, '2020-03-27', 20000),

-- Arijit Singh Songs
('Tum Hi Ho', 5, 8, 'Bollywood', 262, '2013-04-08', 55000),
('Channa Mereya', 5, 8, 'Bollywood', 289, '2016-09-28', 42000),
('Kesariya', 5, 8, 'Bollywood', 268, '2022-07-17', 38000),
('Raabta', 5, 8, 'Bollywood', 248, '2012-06-08', 28000);

-- =====================================================
-- INSERT SAMPLE PODCASTS
-- =====================================================
INSERT INTO podcasts (title, artist_id, description, genre, duration, release_date, play_count) VALUES
('Music Journey Episode 1', 1, 'Taylor discusses her songwriting process', 'Talk', 1800, '2023-01-15', 5000),
('Behind the Hits', 2, 'Ed Sheeran talks about his biggest hits', 'Talk', 2400, '2023-02-20', 4500),
('R&B Evolution', 3, 'The Weeknd explores R&B history', 'Music', 3000, '2023-03-10', 3800);

-- =====================================================
-- INSERT SAMPLE PLAYLISTS
-- =====================================================
INSERT INTO playlists (user_id, name, description, is_public) VALUES
(1, 'My Favorites', 'Collection of my all-time favorite songs', FALSE),
(1, 'Workout Mix', 'High energy songs for gym sessions', TRUE),
(2, 'Chill Vibes', 'Relaxing songs for unwinding', TRUE),
(2, 'Road Trip', 'Perfect songs for long drives', TRUE),
(3, 'Party Playlist', 'Best party anthems', TRUE),
(3, 'Study Music', 'Focus-friendly instrumental tracks', FALSE);

-- =====================================================
-- INSERT SAMPLE PLAYLIST SONGS
-- =====================================================
INSERT INTO playlist_songs (playlist_id, song_id, position) VALUES
-- My Favorites
(1, 1, 1), (1, 6, 2), (1, 11, 3), (1, 17, 4), (1, 20, 5),
-- Workout Mix
(2, 6, 1), (2, 11, 2), (2, 16, 3), (2, 17, 4), (2, 9, 5),
-- Chill Vibes
(3, 8, 1), (3, 4, 2), (3, 21, 3), (3, 22, 4),
-- Road Trip
(4, 1, 1), (4, 7, 2), (4, 11, 3), (4, 16, 4), (4, 17, 5),
-- Party Playlist
(5, 6, 1), (5, 11, 2), (5, 16, 3), (5, 17, 4), (5, 9, 5), (5, 1, 6);

-- =====================================================
-- INSERT SAMPLE FAVORITES
-- =====================================================
INSERT INTO favorites (user_id, song_id) VALUES
(1, 6), (1, 11), (1, 17), (1, 20), (1, 8),
(2, 1), (2, 4), (2, 11), (2, 21), (2, 22),
(3, 6), (3, 9), (3, 16), (3, 17), (3, 11);

-- =====================================================
-- INSERT SAMPLE LISTENING HISTORY
-- =====================================================
INSERT INTO listening_history (user_id, song_id, played_at, duration_played) VALUES
(1, 6, '2024-01-15 10:30:00', 234),
(1, 11, '2024-01-15 10:35:00', 200),
(1, 17, '2024-01-15 10:40:00', 203),
(1, 6, '2024-01-16 09:00:00', 234),
(2, 1, '2024-01-15 14:00:00', 219),
(2, 4, '2024-01-15 14:05:00', 200),
(2, 21, '2024-01-15 14:10:00', 262),
(3, 16, '2024-01-15 20:00:00', 183),
(3, 17, '2024-01-15 20:05:00', 203),
(3, 11, '2024-01-15 20:10:00', 200);
