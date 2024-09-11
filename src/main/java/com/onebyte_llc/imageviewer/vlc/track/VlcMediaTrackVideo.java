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

@Structure.FieldOrder({"height", "width", "i_sar_num", "i_sar_den", "i_frame_rate_num", "i_frame_rate_den"})

public class VlcMediaTrackVideo extends Structure {

    public int height;
    public int width;
    public int i_sar_num;
    public int i_sar_den;
    public int i_frame_rate_num;
    public int i_frame_rate_den;

    // TODO I skipped these ...
    //    libvlc_video_orient_t       i_orientation;
    //    libvlc_video_projection_t   i_projection;
    //    libvlc_video_viewpoint_t    pose; /**< Initial view point */

    public VlcMediaTrackVideo(Pointer p) {
        super(p);
        read();
    }

}
