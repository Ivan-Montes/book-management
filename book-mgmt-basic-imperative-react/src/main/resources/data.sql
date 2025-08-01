INSERT INTO PUBLISHERS (creation_timestamp, update_timestamp, publisher_name)
VALUES(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Infinity Publications'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Live and let live Treatise'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'I want to believe Publications'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Legendary Editions'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Mind and Muscle Reviews'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Sunny Almanacs'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Wanderlust Press'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Echoes & Ink Publishing'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Nebula Scrolls'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'The Curious Quill'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Silent Compass Editions'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Storm & Sage Publishing'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Timeless Realms Media'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Crimson Thought Publishers'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Blue Lantern Books'),
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Obsidian Feather House');

INSERT INTO AUTHORS (author_name, author_surname, creation_timestamp, update_timestamp)
VALUES('Isaac', 'Newton', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Koyoharu ', 'Gotouge', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Emilie', 'Bronte', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jane', 'Austen', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Richard', 'Helm', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Erich', 'Gamma', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ralph', 'Johnson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jules', 'Verne', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Herbert George', 'Wells', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Mary', 'Shelley', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO GENRES (genre_name, genre_description, creation_timestamp, update_timestamp)
VALUES
('Drama', 'Unfortunate events in life', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Science Fiction', 'Fantastic literature and horror storytelling', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Manga', 'Japanese-origin comics and graphic novels', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Self-help', 'Personal development and emotional acceptance', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Action', 'Exciting scenes of combat and adventure', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Comedy', 'Humorous stories and comic situations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Futurism', 'Exploration of possible futures and advanced technology', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Romance', 'Love stories and personal relationships', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Horror', 'Frightening scenes and supernatural elements', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fantasy', 'Magical worlds, creatures, and epic adventures', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Animation', 'Movies drawn or computer-generated', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Documentary', 'Exploration of real events and historical facts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Educational', 'Textbooks and learning materials', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO BOOKSHOPS (bookshop_name, creation_timestamp, update_timestamp)
VALUES('The Cornershop', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Books, books, books', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fly you fools Books', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Big Little Shop', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Dark Corner', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Wandering Page', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Once Upon a Shelf', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Inkwell Nook', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Chapter & Verse', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Whispers Between Pages', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bard & Bookmark', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Paperback Jungle', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Plot Twist Emporium', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Booksmithery', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Margins & Metaphors', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO BOOKS (book_isbn, book_title, genre_id, publisher_id, creation_timestamp, update_timestamp)
VALUES(1231231231231, 'Principia Mathematica', 13, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1231231231232, 'Kimetsu No Yaiba Vol I', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1231231231233, 'Design patterns', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1231231231234, 'Wuthering Heights', 1, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1231231231235, 'Pride and Prejudice', 8, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1231231231236, 'Frankenstein', 1, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1231231231237, 'Journey to the Center of the Earth', 10, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1231231231238, 'The Time Machine', 2, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO BOOKS_AUTHORS (book_id, author_id)
VALUES(1,1),
(2,2),
(3,5),
(3,6),
(3,7),
(4,3),
(5,4),
(6,10),
(7,8),
(8,9);

INSERT INTO BOOKS_BOOKSHOPS (book_book_id, bookshop_bookshop_id, price, units)
VALUES(1,3,13,25),
(2,2,9.99,10),
(3,1,17.5,5);
