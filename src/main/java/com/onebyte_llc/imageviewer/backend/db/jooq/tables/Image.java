/*
 * This file is generated by jOOQ.
 */
package com.onebyte_llc.imageviewer.backend.db.jooq.tables;


import com.onebyte_llc.imageviewer.backend.db.jooq.DefaultSchema;
import com.onebyte_llc.imageviewer.backend.db.jooq.Indexes;
import com.onebyte_llc.imageviewer.backend.db.jooq.Keys;
import com.onebyte_llc.imageviewer.backend.db.jooq.tables.records.ImageRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function5;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Image extends TableImpl<ImageRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>image</code>
     */
    public static final Image IMAGE = new Image();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ImageRecord> getRecordType() {
        return ImageRecord.class;
    }

    /**
     * The column <code>image.id</code>.
     */
    public final TableField<ImageRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>image.directory_id</code>.
     */
    public final TableField<ImageRecord, Integer> DIRECTORY_ID = createField(DSL.name("directory_id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>image.filename</code>.
     */
    public final TableField<ImageRecord, String> FILENAME = createField(DSL.name("filename"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>image.im_original_date</code>.
     */
    public final TableField<ImageRecord, LocalDateTime> IM_ORIGINAL_DATE = createField(DSL.name("im_original_date"), SQLDataType.LOCALDATETIME(0), this, "");

    /**
     * The column <code>image.fs_modify_time</code>.
     */
    public final TableField<ImageRecord, LocalDateTime> FS_MODIFY_TIME = createField(DSL.name("fs_modify_time"), SQLDataType.LOCALDATETIME(0), this, "");

    private Image(Name alias, Table<ImageRecord> aliased) {
        this(alias, aliased, null);
    }

    private Image(Name alias, Table<ImageRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>image</code> table reference
     */
    public Image(String alias) {
        this(DSL.name(alias), IMAGE);
    }

    /**
     * Create an aliased <code>image</code> table reference
     */
    public Image(Name alias) {
        this(alias, IMAGE);
    }

    /**
     * Create a <code>image</code> table reference
     */
    public Image() {
        this(DSL.name("image"), null);
    }

    public <O extends Record> Image(Table<O> child, ForeignKey<O, ImageRecord> key) {
        super(child, key, IMAGE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.IMAGE_DIRECTORY_ID_INDEX);
    }

    @Override
    public UniqueKey<ImageRecord> getPrimaryKey() {
        return Keys.IMAGE__PK_IMAGE;
    }

    @Override
    public List<ForeignKey<ImageRecord, ?>> getReferences() {
        return Arrays.asList(Keys.IMAGE__FK_DIRECTORY_ID);
    }

    private transient Directory _directory;

    /**
     * Get the implicit join path to the <code>directory</code> table.
     */
    public Directory directory() {
        if (_directory == null)
            _directory = new Directory(this, Keys.IMAGE__FK_DIRECTORY_ID);

        return _directory;
    }

    @Override
    public Image as(String alias) {
        return new Image(DSL.name(alias), this);
    }

    @Override
    public Image as(Name alias) {
        return new Image(alias, this);
    }

    @Override
    public Image as(Table<?> alias) {
        return new Image(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Image rename(String name) {
        return new Image(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Image rename(Name name) {
        return new Image(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Image rename(Table<?> name) {
        return new Image(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Integer, Integer, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function5<? super Integer, ? super Integer, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function5<? super Integer, ? super Integer, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
