package com.onebytellc.imageviewer.backend.db;

import com.onebytellc.imageviewer.backend.db.jooq.tables.records.CollectionPathRecord;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.DirectoryRecord;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.ImageRecord;
import com.onebytellc.imageviewer.backend.image.ImageLoader;
import com.onebytellc.imageviewer.logger.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Scanner;

import static com.onebytellc.imageviewer.backend.db.jooq.tables.CollectionPath.COLLECTION_PATH;
import static com.onebytellc.imageviewer.backend.db.jooq.tables.Directory.DIRECTORY;
import static com.onebytellc.imageviewer.backend.db.jooq.tables.Image.IMAGE;

public class Database {

    private static final Logger LOG = Logger.getInstance(Database.class);

    private final DSLContext context;

    public Database(DSLContext dslContext) {
        this.context = dslContext;
    }

    public static DSLContext initialize(Path saveDirectory) {
        try {
            // format for where the sql db file is saved is
            // "jdbc:sqlite:C:/work/mydatabase.db"
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + saveDirectory + "/imageview.db");

            DSLContext ctx = DSL.using(conn, SQLDialect.SQLITE);
            String text = new Scanner(
                    Database.class.getResourceAsStream("/sql/v0-schema.sql"), "UTF-8")
                    .useDelimiter("\\A").next();

            String[] split = text.split(";");
            for (String s : split) {
                ctx.execute(s);
            }

            LOG.info("Opened database successfully");
            return ctx;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
            throw new IllegalStateException("Database could not be initialized", e);
        }
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
