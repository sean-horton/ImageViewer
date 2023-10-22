--  TABLES
--     - collection (id, name, path, depth)
--     - path (id, directory)
--     - image (id, path_id, collection_id, im_create_date, fs_modify_time)

CREATE TABLE IF NOT EXISTS schema
(
    version INTEGER
);

INSERT INTO schema (version)
VALUES (0);

CREATE TABLE IF NOT EXISTS collection
(
    id   INTEGER PRIMARY KEY,
    name TEXT
);

CREATE TABLE IF NOT EXISTS path
(
    id            INTEGER PRIMARY KEY,
    collection_id INTEGER,
    directory     TEXT,
    depth         INTEGER,
    FOREIGN KEY (collection_id) REFERENCES collection (id)
);

CREATE TABLE IF NOT EXISTS image
(
    id               INTEGER PRIMARY KEY,
    path_id          INTEGER,
    im_original_date DATETIME,
    fs_modify_time   DATETIME,
    FOREIGN KEY (path_id) REFERENCES path (id)
);