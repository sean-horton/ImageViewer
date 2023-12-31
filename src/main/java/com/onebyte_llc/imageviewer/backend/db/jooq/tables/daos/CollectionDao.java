/*
 * This file is generated by jOOQ.
 */
package com.onebyte_llc.imageviewer.backend.db.jooq.tables.daos;


import com.onebyte_llc.imageviewer.backend.db.jooq.tables.Collection;
import com.onebyte_llc.imageviewer.backend.db.jooq.tables.records.CollectionRecord;

import java.util.List;
import java.util.Optional;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CollectionDao extends DAOImpl<CollectionRecord, com.onebyte_llc.imageviewer.backend.db.jooq.tables.pojos.Collection, Integer> {

    /**
     * Create a new CollectionDao without any configuration
     */
    public CollectionDao() {
        super(Collection.COLLECTION, com.onebyte_llc.imageviewer.backend.db.jooq.tables.pojos.Collection.class);
    }

    /**
     * Create a new CollectionDao with an attached configuration
     */
    public CollectionDao(Configuration configuration) {
        super(Collection.COLLECTION, com.onebyte_llc.imageviewer.backend.db.jooq.tables.pojos.Collection.class, configuration);
    }

    @Override
    public Integer getId(com.onebyte_llc.imageviewer.backend.db.jooq.tables.pojos.Collection object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.onebyte_llc.imageviewer.backend.db.jooq.tables.pojos.Collection> fetchRangeOfId(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(Collection.COLLECTION.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<com.onebyte_llc.imageviewer.backend.db.jooq.tables.pojos.Collection> fetchById(Integer... values) {
        return fetch(Collection.COLLECTION.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public com.onebyte_llc.imageviewer.backend.db.jooq.tables.pojos.Collection fetchOneById(Integer value) {
        return fetchOne(Collection.COLLECTION.ID, value);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public Optional<com.onebyte_llc.imageviewer.backend.db.jooq.tables.pojos.Collection> fetchOptionalById(Integer value) {
        return fetchOptional(Collection.COLLECTION.ID, value);
    }

    /**
     * Fetch records that have <code>name BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.onebyte_llc.imageviewer.backend.db.jooq.tables.pojos.Collection> fetchRangeOfName(String lowerInclusive, String upperInclusive) {
        return fetchRange(Collection.COLLECTION.NAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>name IN (values)</code>
     */
    public List<com.onebyte_llc.imageviewer.backend.db.jooq.tables.pojos.Collection> fetchByName(String... values) {
        return fetch(Collection.COLLECTION.NAME, values);
    }
}
