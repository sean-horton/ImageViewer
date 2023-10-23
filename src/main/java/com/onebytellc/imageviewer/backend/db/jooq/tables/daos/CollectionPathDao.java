/*
 * This file is generated by jOOQ.
 */
package com.onebytellc.imageviewer.backend.db.jooq.tables.daos;


import com.onebytellc.imageviewer.backend.db.jooq.tables.CollectionPath;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.CollectionPathRecord;

import java.util.List;
import java.util.Optional;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CollectionPathDao extends DAOImpl<CollectionPathRecord, com.onebytellc.imageviewer.backend.db.jooq.tables.pojos.CollectionPath, Integer> {

    /**
     * Create a new CollectionPathDao without any configuration
     */
    public CollectionPathDao() {
        super(CollectionPath.COLLECTION_PATH, com.onebytellc.imageviewer.backend.db.jooq.tables.pojos.CollectionPath.class);
    }

    /**
     * Create a new CollectionPathDao with an attached configuration
     */
    public CollectionPathDao(Configuration configuration) {
        super(CollectionPath.COLLECTION_PATH, com.onebytellc.imageviewer.backend.db.jooq.tables.pojos.CollectionPath.class, configuration);
    }

    @Override
    public Integer getId(com.onebytellc.imageviewer.backend.db.jooq.tables.pojos.CollectionPath object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.onebytellc.imageviewer.backend.db.jooq.tables.pojos.CollectionPath> fetchRangeOfId(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(CollectionPath.COLLECTION_PATH.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<com.onebytellc.imageviewer.backend.db.jooq.tables.pojos.CollectionPath> fetchById(Integer... values) {
        return fetch(CollectionPath.COLLECTION_PATH.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public com.onebytellc.imageviewer.backend.db.jooq.tables.pojos.CollectionPath fetchOneById(Integer value) {
        return fetchOne(CollectionPath.COLLECTION_PATH.ID, value);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public Optional<com.onebytellc.imageviewer.backend.db.jooq.tables.pojos.CollectionPath> fetchOptionalById(Integer value) {
        return fetchOptional(CollectionPath.COLLECTION_PATH.ID, value);
    }

    /**
     * Fetch records that have <code>collection_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.onebytellc.imageviewer.backend.db.jooq.tables.pojos.CollectionPath> fetchRangeOfCollectionId(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(CollectionPath.COLLECTION_PATH.COLLECTION_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>collection_id IN (values)</code>
     */
    public List<com.onebytellc.imageviewer.backend.db.jooq.tables.pojos.CollectionPath> fetchByCollectionId(Integer... values) {
        return fetch(CollectionPath.COLLECTION_PATH.COLLECTION_ID, values);
    }

    /**
     * Fetch records that have <code>directory BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.onebytellc.imageviewer.backend.db.jooq.tables.pojos.CollectionPath> fetchRangeOfDirectory(String lowerInclusive, String upperInclusive) {
        return fetchRange(CollectionPath.COLLECTION_PATH.DIRECTORY, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>directory IN (values)</code>
     */
    public List<com.onebytellc.imageviewer.backend.db.jooq.tables.pojos.CollectionPath> fetchByDirectory(String... values) {
        return fetch(CollectionPath.COLLECTION_PATH.DIRECTORY, values);
    }

    /**
     * Fetch records that have <code>depth BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.onebytellc.imageviewer.backend.db.jooq.tables.pojos.CollectionPath> fetchRangeOfDepth(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(CollectionPath.COLLECTION_PATH.DEPTH, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>depth IN (values)</code>
     */
    public List<com.onebytellc.imageviewer.backend.db.jooq.tables.pojos.CollectionPath> fetchByDepth(Integer... values) {
        return fetch(CollectionPath.COLLECTION_PATH.DEPTH, values);
    }
}
