/*
 * This file is generated by jOOQ.
 */
package com.onebyte_llc.imageviewer.backend.db.jooq.tables;


import com.onebyte_llc.imageviewer.backend.db.jooq.DefaultSchema;
import com.onebyte_llc.imageviewer.backend.db.jooq.Indexes;
import com.onebyte_llc.imageviewer.backend.db.jooq.Keys;
import com.onebyte_llc.imageviewer.backend.db.jooq.tables.records.DirectoryRecord;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function2;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row2;
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
public class Directory extends TableImpl<DirectoryRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>directory</code>
     */
    public static final Directory DIRECTORY = new Directory();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DirectoryRecord> getRecordType() {
        return DirectoryRecord.class;
    }

    /**
     * The column <code>directory.id</code>.
     */
    public final TableField<DirectoryRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>directory.path</code>.
     */
    public final TableField<DirectoryRecord, String> PATH = createField(DSL.name("path"), SQLDataType.CLOB, this, "");

    private Directory(Name alias, Table<DirectoryRecord> aliased) {
        this(alias, aliased, null);
    }

    private Directory(Name alias, Table<DirectoryRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>directory</code> table reference
     */
    public Directory(String alias) {
        this(DSL.name(alias), DIRECTORY);
    }

    /**
     * Create an aliased <code>directory</code> table reference
     */
    public Directory(Name alias) {
        this(alias, DIRECTORY);
    }

    /**
     * Create a <code>directory</code> table reference
     */
    public Directory() {
        this(DSL.name("directory"), null);
    }

    public <O extends Record> Directory(Table<O> child, ForeignKey<O, DirectoryRecord> key) {
        super(child, key, DIRECTORY);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.DIRECTORY_PATH_INDEX);
    }

    @Override
    public UniqueKey<DirectoryRecord> getPrimaryKey() {
        return Keys.DIRECTORY__PK_DIRECTORY;
    }

    @Override
    public List<UniqueKey<DirectoryRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.DIRECTORY__UK_DIRECTORY_97053330);
    }

    @Override
    public Directory as(String alias) {
        return new Directory(DSL.name(alias), this);
    }

    @Override
    public Directory as(Name alias) {
        return new Directory(alias, this);
    }

    @Override
    public Directory as(Table<?> alias) {
        return new Directory(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Directory rename(String name) {
        return new Directory(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Directory rename(Name name) {
        return new Directory(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Directory rename(Table<?> name) {
        return new Directory(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Integer, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function2<? super Integer, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function2<? super Integer, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
