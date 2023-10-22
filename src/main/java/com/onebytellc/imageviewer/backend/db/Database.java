package com.onebytellc.imageviewer.backend.db;

import com.onebytellc.imageviewer.backend.db.jooq.tables.records.CollectionRecord;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.ImageRecord;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.PathRecord;
import com.onebytellc.imageviewer.reactive.Observable;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

import static com.onebytellc.imageviewer.backend.db.jooq.tables.Path.PATH;

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
            Class.forName("org.sqlite.JDBC");
            String savePath = "/Users/shorton/imageviewtest/db/";
            conn = DriverManager.getConnection("jdbc:sqlite:" + savePath + "/imageview.db");

            DSLContext ctx = DSL.using(conn, SQLDialect.SQLITE);
            String text = new Scanner(
                    Database.class.getResourceAsStream("/sql/v0-schema.sql"), "UTF-8")
                    .useDelimiter("\\A").next();

            String[] split = text.split(";");
            for (String s : split) {
                ctx.execute(s);
            }

            return ctx;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        return null;
    }

//    public List<ImageRecord> imageRecords() {
//
//    }
//
//    public List<CollectionRecord> collectionRecords() {
//
//    }

    public List<PathRecord> path(int collectionId) {
        return context.select().from(PATH).fetch()
                .map(r -> r.into(PathRecord.class));
    }

}
