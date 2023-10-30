--  TABLES
--     - collection (id, name, path, depth)
--     - path (id, directory)
--     - image (id, path_id, collection_id, im_create_date, fs_modify_time)

CREATE TABLE IF NOT EXISTS collection
(
    id   INTEGER PRIMARY KEY,
    name TEXT
);

CREATE TABLE IF NOT EXISTS collection_path
(
    id            INTEGER PRIMARY KEY,
    collection_id INTEGER,
    directory     TEXT,
    depth         INTEGER,
    CONSTRAINT fk_collection_id
        FOREIGN KEY (collection_id) REFERENCES collection (id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS collection_path_collection_id_index ON collection_path (collection_id);

CREATE TABLE IF NOT EXISTS directory
(
    id   INTEGER PRIMARY KEY,
    path TEXT UNIQUE
);
CREATE INDEX IF NOT EXISTS directory_path_index ON directory (path);

CREATE TABLE IF NOT EXISTS image
(
    id               INTEGER PRIMARY KEY,
    directory_id     INTEGER,
    filename         TEXT,
    im_original_date DATETIME,
    fs_modify_time   DATETIME,
    CONSTRAINT fk_directory_id
        FOREIGN KEY (directory_id) REFERENCES directory (id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS image_directory_id_index ON image (directory_id);

-- NOTE - on schema updates always update the schema
-- last in case the original schema was partially created
CREATE TABLE IF NOT EXISTS schema
(
    version INTEGER
);

INSERT INTO schema (version)
VALUES (0);