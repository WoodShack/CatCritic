-- Passwords below are bcrypt-hashed; see README.md for the plaintext demo credentials.

INSERT INTO USERS (id, username, password_hash, role, enabled, created_at)
SELECT 1, 'demo', '$2b$10$yBP.GfROUrToWtWDjEqo6ehI1Tb/HvH9QsrIlTI4cZFzkf8XASAJK', 'ADMIN', TRUE, '2026-07-01 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM USERS WHERE id = 1);

INSERT INTO USERS (id, username, password_hash, role, enabled, created_at)
SELECT 2, 'mod', '$2b$10$ebH1IZhUySvg4yuhA0p1a.xbEuZhMvEKblrgGu1mvqyuvVCZJpxFC', 'CAT_OWNER', TRUE, '2026-07-02 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM USERS WHERE id = 2);

INSERT INTO USERS (id, username, password_hash, role, enabled, created_at)
SELECT 3, 'critic', '$2b$10$0fA49/H/zcBsZuFfCRCW9es49YTo4fKZUUQUMNY.WegKeEResMHtS', 'CAT_VIEWER', TRUE, '2026-07-03 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM USERS WHERE id = 3);


INSERT INTO CATS (id, name, age, breed, description, image_url, owner_username, created_at, rating_count, rating_total)
SELECT 1, 'Mittens', 3, 'DOMESTIC_SHORTHAIR', 'Loves sunbeams and knocking cups off tables.', 'https://placehold.co/400x300/ffd1e6/5a3245?text=Mittens', 'demo', '2026-07-01 10:00:00', 4, 34
WHERE NOT EXISTS (SELECT 1 FROM CATS WHERE id = 1);

INSERT INTO CATS (id, name, age, breed, description, image_url, owner_username, created_at, rating_count, rating_total)
SELECT 2, 'Leo', 5, 'MAINE_COON', 'A gentle giant who thinks he is a lap cat.', 'https://placehold.co/400x300/ffd1e6/5a3245?text=Leo', 'demo', '2026-07-05 12:30:00', 6, 54
WHERE NOT EXISTS (SELECT 1 FROM CATS WHERE id = 2);

INSERT INTO CATS (id, name, age, breed, description, image_url, owner_username, created_at, rating_count, rating_total)
SELECT 3, 'Luna', 1, 'SIAMESE', 'Talks constantly and demands attention every hour.', 'https://placehold.co/400x300/ffd1e6/5a3245?text=Luna', 'demo', '2026-07-08 08:15:00', 3, 21
WHERE NOT EXISTS (SELECT 1 FROM CATS WHERE id = 3);

INSERT INTO CATS (id, name, age, breed, description, image_url, owner_username, created_at, rating_count, rating_total)
SELECT 4, 'Simba', 7, 'PERSIAN', 'Extremely fluffy and extremely lazy.', 'https://placehold.co/400x300/ffd1e6/5a3245?text=Simba', 'demo', '2026-07-10 17:45:00', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM CATS WHERE id = 4);

INSERT INTO CATS (id, name, age, breed, description, image_url, owner_username, created_at, rating_count, rating_total)
SELECT 5, 'Coco', 2, 'RAGDOLL', 'Goes limp with joy whenever she is picked up.', 'https://placehold.co/400x300/ffd1e6/5a3245?text=Coco', 'demo', '2026-07-12 14:00:00', 5, 46
WHERE NOT EXISTS (SELECT 1 FROM CATS WHERE id = 5);

INSERT INTO CATS (id, name, age, breed, description, image_url, owner_username, created_at, rating_count, rating_total)
SELECT 6, 'Tiger', 4, 'BENGAL', 'Athletic, vocal, and convinced he is part wildcat.', 'https://placehold.co/400x300/ffd1e6/5a3245?text=Tiger', 'demo', '2026-07-15 09:20:00', 2, 14
WHERE NOT EXISTS (SELECT 1 FROM CATS WHERE id = 6);

INSERT INTO CATS (id, name, age, breed, description, image_url, owner_username, created_at, rating_count, rating_total)
SELECT 7, 'Nala', 6, 'BRITISH_SHORTHAIR', 'Round, calm, and permanently unimpressed.', 'https://placehold.co/400x300/ffd1e6/5a3245?text=Nala', 'demo', '2026-07-18 11:10:00', 7, 63
WHERE NOT EXISTS (SELECT 1 FROM CATS WHERE id = 7);

INSERT INTO CATS (id, name, age, breed, description, image_url, owner_username, created_at, rating_count, rating_total)
SELECT 8, 'Oscar', 0, 'SPHYNX', 'A curious kitten with zero fur and infinite energy.', 'https://placehold.co/400x300/ffd1e6/5a3245?text=Oscar', 'demo', '2026-07-20 16:30:00', 1, 8
WHERE NOT EXISTS (SELECT 1 FROM CATS WHERE id = 8);

INSERT INTO CATS (id, name, age, breed, description, image_url, owner_username, created_at, rating_count, rating_total)
SELECT 9, 'Willow', 9, 'RUSSIAN_BLUE', 'Quiet, elegant, and suspicious of strangers.', 'https://placehold.co/400x300/ffd1e6/5a3245?text=Willow', 'demo', '2026-07-22 13:05:00', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM CATS WHERE id = 9);

INSERT INTO CATS (id, name, age, breed, description, image_url, owner_username, created_at, rating_count, rating_total)
SELECT 10, 'Bean', 2, 'SCOTTISH_FOLD', 'Sits like a tiny gremlin and stares into the void.', 'https://placehold.co/400x300/ffd1e6/5a3245?text=Bean', 'demo', '2026-07-24 19:40:00', 3, 27
WHERE NOT EXISTS (SELECT 1 FROM CATS WHERE id = 10);


ALTER TABLE USERS ALTER COLUMN ID RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM USERS);

ALTER TABLE CATS ALTER COLUMN ID RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM CATS);