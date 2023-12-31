/*
 * This file is generated by jOOQ.
 */
package com.onebyte_llc.imageviewer.backend.db.jooq;


import com.onebyte_llc.imageviewer.backend.db.jooq.tables.Collection;
import com.onebyte_llc.imageviewer.backend.db.jooq.tables.CollectionPath;
import com.onebyte_llc.imageviewer.backend.db.jooq.tables.Directory;
import com.onebyte_llc.imageviewer.backend.db.jooq.tables.Image;
import com.onebyte_llc.imageviewer.backend.db.jooq.tables.Schema;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DefaultSchema extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>DEFAULT_SCHEMA</code>
     */
    public static final DefaultSchema DEFAULT_SCHEMA = new DefaultSchema();

    /**
     * The table <code>collection</code>.
     */
    public final Collection COLLECTION = Collection.COLLECTION;

    /**
     * The table <code>collection_path</code>.
     */
    public final CollectionPath COLLECTION_PATH = CollectionPath.COLLECTION_PATH;

    /**
     * The table <code>directory</code>.
     */
    public final Directory DIRECTORY = Directory.DIRECTORY;

    /**
     * The table <code>image</code>.
     */
    public final Image IMAGE = Image.IMAGE;

    /**
     * The table <code>schema</code>.
     */
    public final Schema SCHEMA = Schema.SCHEMA;

    /**
     * No further instances allowed
     */
    private DefaultSchema() {
        super("", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Collection.COLLECTION,
            CollectionPath.COLLECTION_PATH,
            Directory.DIRECTORY,
            Image.IMAGE,
            Schema.SCHEMA
        );
    }
}
