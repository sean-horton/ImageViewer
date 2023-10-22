/*
 * This file is generated by jOOQ.
 */
package com.onebytellc.imageviewer.backend.db.jooq.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer pathId;
    private LocalDateTime imOriginalDate;
    private LocalDateTime fsModifyTime;

    public Image() {}

    public Image(Image value) {
        this.id = value.id;
        this.pathId = value.pathId;
        this.imOriginalDate = value.imOriginalDate;
        this.fsModifyTime = value.fsModifyTime;
    }

    public Image(
        Integer id,
        Integer pathId,
        LocalDateTime imOriginalDate,
        LocalDateTime fsModifyTime
    ) {
        this.id = id;
        this.pathId = pathId;
        this.imOriginalDate = imOriginalDate;
        this.fsModifyTime = fsModifyTime;
    }

    /**
     * Getter for <code>image.id</code>.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Setter for <code>image.id</code>.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Getter for <code>image.path_id</code>.
     */
    public Integer getPathId() {
        return this.pathId;
    }

    /**
     * Setter for <code>image.path_id</code>.
     */
    public void setPathId(Integer pathId) {
        this.pathId = pathId;
    }

    /**
     * Getter for <code>image.im_original_date</code>.
     */
    public LocalDateTime getImOriginalDate() {
        return this.imOriginalDate;
    }

    /**
     * Setter for <code>image.im_original_date</code>.
     */
    public void setImOriginalDate(LocalDateTime imOriginalDate) {
        this.imOriginalDate = imOriginalDate;
    }

    /**
     * Getter for <code>image.fs_modify_time</code>.
     */
    public LocalDateTime getFsModifyTime() {
        return this.fsModifyTime;
    }

    /**
     * Setter for <code>image.fs_modify_time</code>.
     */
    public void setFsModifyTime(LocalDateTime fsModifyTime) {
        this.fsModifyTime = fsModifyTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Image other = (Image) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.pathId == null) {
            if (other.pathId != null)
                return false;
        }
        else if (!this.pathId.equals(other.pathId))
            return false;
        if (this.imOriginalDate == null) {
            if (other.imOriginalDate != null)
                return false;
        }
        else if (!this.imOriginalDate.equals(other.imOriginalDate))
            return false;
        if (this.fsModifyTime == null) {
            if (other.fsModifyTime != null)
                return false;
        }
        else if (!this.fsModifyTime.equals(other.fsModifyTime))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.pathId == null) ? 0 : this.pathId.hashCode());
        result = prime * result + ((this.imOriginalDate == null) ? 0 : this.imOriginalDate.hashCode());
        result = prime * result + ((this.fsModifyTime == null) ? 0 : this.fsModifyTime.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Image (");

        sb.append(id);
        sb.append(", ").append(pathId);
        sb.append(", ").append(imOriginalDate);
        sb.append(", ").append(fsModifyTime);

        sb.append(")");
        return sb.toString();
    }
}
