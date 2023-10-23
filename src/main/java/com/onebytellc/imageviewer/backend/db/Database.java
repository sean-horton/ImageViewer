package com.onebytellc.imageviewer.backend.db;

import com.onebytellc.imageviewer.backend.db.jooq.tables.records.CollectionPathRecord;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.DirectoryRecord;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.ImageRecord;
import com.onebytellc.imageviewer.backend.image.ImageLoader;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

import static com.onebytellc.imageviewer.backend.db.jooq.tables.CollectionPath.COLLECTION_PATH;
import static com.onebytellc.imageviewer.backend.db.jooq.tables.Directory.DIRECTORY;
import static com.onebytellc.imageviewer.backend.db.jooq.tables.Image.IMAGE;

public class Database {

    // TABLES
    //   - collection (id, name, path, depth)
    //   - path (id, directory)
    //   - image (id, path_id, collection_id, im_create_date, fs_modify_time)

    // A collection is DB entries - UI IS BOUND TO THIS
    // A collection references the file system structure - COLLECTION IS BOUND TO THIS

    // 1. On start we'd fetch from DB the active collection
    // 2. Start an image explorer on that collection, compare to DB images that have that path, add, delete images in DB
    //     - if modify date is newer than one in DB we need to re-index
    //     - indexed images use the DB pk id as the file id - place all indexed images in the file system
    // 3. Display in the UI DB (SELECT * FROM image WHERE collection.id == 'xyz' SORT BY image.im_create_date)

    // ImageExplorer
    //   - keep a saved file of what we thought the directory was (file or DB?)
    //   - then on viewing the directory we have a diff (what was added/removed from last startup)

    private final DSLContext context;

    public Database(DSLContext dslContext) {
        this.context = dslContext;
    }

    public static DSLContext initialize() {
        Connection conn = null;
        Statement stmt = null;

        try {
            // format for where the sql db file is saved is
            // "jdbc:sqlite:C:/work/mydatabase.db"
            // TODO - needs to be passed property
            Class.forName("org.sqlite.JDBC");
            String savePath = "/Users/shorton/imageviewdata/";
            conn = DriverManager.getConnection("jdbc:sqlite:" + savePath + "/imageview.db");

            DSLContext ctx = DSL.using(conn, SQLDialect.SQLITE);
            String text = new Scanner(
                    Database.class.getResourceAsStream("/sql/v0-schema.sql"), "UTF-8")
                    .useDelimiter("\\A").next();

            String[] split = text.split(";");
            for (String s : split) {
                ctx.execute(s);
            }

            List<DirectoryRecord> records = ctx.select().from(DIRECTORY).fetch()
                    .map(r -> r.into(DirectoryRecord.class));

            return ctx;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        return null;
    }

    public List<CollectionPathRecord> getPathsForCollection(int collectionId) {
        return context.select().from(COLLECTION_PATH).fetch()
                .map(r -> r.into(CollectionPathRecord.class));
    }

    public List<ImageRecord> getImagesInDirectory(DirectoryRecord directoryRecord) {
        return context.select().from(IMAGE)
                .where(IMAGE.DIRECTORY_ID.eq(directoryRecord.getId()))
                .fetch()
                .map(r -> r.into(ImageRecord.class));
    }

    public DirectoryRecord getOrAddDirectory(String path) {
        DirectoryRecord record = new DirectoryRecord();
        record.setPath(path);
        context.insertInto(DIRECTORY)
                .set(record)
                .onConflict()
                .where(DIRECTORY.PATH.eq(path))
                .doNothing()
                .execute();

        return context.select().from(DIRECTORY)
                .where(DIRECTORY.PATH.eq(path))
                .fetchOne()
                .map(r -> r.into(DirectoryRecord.class));
    }

    public ImageRecord addImage(DirectoryRecord directory, ImageLoader loader) {
        ImageRecord record = new ImageRecord();
        record.setDirectoryId(directory.getId());
        record.setFilename(loader.getPath().getFileName().toString());
        return context.insertInto(IMAGE)
                .set(record)
                .returning()
                .fetchOne();
    }

}
