/*
 *     ImageViewer - free image viewing gui
 *     Copyright (C) 2024  Sean Horton
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; version 2 of the License
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.onebyte_llc.imageviewer.vlc.track;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

// typedef struct libvlc_media_track_t
//{
//    /* Codec fourcc */
//    uint32_t    i_codec;
//    uint32_t    i_original_fourcc;
//    int         i_id;
//    libvlc_track_type_t i_type;
//
//    /* Codec specific */
//    int         i_profile;
//    int         i_level;
//
//    union {
//        libvlc_audio_track_t *audio;
//        libvlc_video_track_t *video;
//        libvlc_subtitle_track_t *subtitle;
//    };
//
//    unsigned int i_bitrate;
//    char *psz_language;
//    char *psz_description;
//
//} libvlc_media_track_t;
@Structure.FieldOrder({"codec", "fourcc", "id", "type", "profile", "level", "info", "bitrate", "language", "desc"})
public class VlcMediaTrack extends Structure {

    //    uint32_t    i_codec;
    //    uint32_t    i_original_fourcc;
    //    int         i_id;
    //    libvlc_track_type_t i_type;
    //
    //    /* Codec specific */
    //    int         i_profile;
    //    int         i_level;
    //
    //    union {
    //        libvlc_audio_track_t *audio;
    //        libvlc_video_track_t *video;
    //        libvlc_subtitle_track_t *subtitle;
    //    };
    //
    //    unsigned int i_bitrate;
    //    char *psz_language;
    //    char *psz_description;


    public int codec;
    public int fourcc;
    public int id;
    public int type;
    public int profile;
    public int level;
    public Pointer info;
    public int bitrate;
    public Pointer language;
    public Pointer desc;


    public VlcMediaTrack(Pointer p) {
        super(p);
        read();
    }

    public VlcMediaTrack() {

    }

    public String getCodec() {
        return fieldToString(codec);
    }

    public String getFourcc() {
        return fieldToString(fourcc);
    }

    private String fieldToString(int val) {
        char[] c = new char[4];
        int x = val;
        for (int i = 0; i < 4; i++) {
            c[i] = (char) (x & 0xFF);
            x >>= 8;
        }
        return new String(c);
    }

}
