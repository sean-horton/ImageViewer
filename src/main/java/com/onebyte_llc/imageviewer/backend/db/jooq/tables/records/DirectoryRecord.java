/*
 * This file is generated by jOOQ.
 */
package com.onebyte_llc.imageviewer.backend.db.jooq.tables.records;


import com.onebyte_llc.imageviewer.backend.db.jooq.tables.Directory;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DirectoryRecord extends UpdatableRecordImpl<DirectoryRecord> implements Record2<Integer, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>directory.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>directory.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>directory.path</code>.
     */
    public void setPath(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>directory.path</code>.
     */
    public String getPath() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<Integer, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<Integer, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Directory.DIRECTORY.ID;
    }

    @Override
    public Field<String> field2() {
        return Directory.DIRECTORY.PATH;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getPath();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getPath();
    }

    @Override
    public DirectoryRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public DirectoryRecord value2(String value) {
        setPath(value);
        return this;
    }

    @Override
    public DirectoryRecord values(Integer value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DirectoryRecord
     */
    public DirectoryRecord() {
        super(Directory.DIRECTORY);
    }

    /**
     * Create a detached, initialised DirectoryRecord
     */
    public DirectoryRecord(Integer id, String path) {
        super(Directory.DIRECTORY);

        setId(id);
        setPath(path);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised DirectoryRecord
     */
    public DirectoryRecord(com.onebyte_llc.imageviewer.backend.db.jooq.tables.pojos.Directory value) {
        super(Directory.DIRECTORY);

        if (value != null) {
            setId(value.getId());
            setPath(value.getPath());
            resetChangedOnNotNull();
        }
    }
}