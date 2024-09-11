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

package com.onebyte_llc.imageviewer.vlc;

import com.onebyte_llc.imageviewer.vlc.video.VlcVideoDisplayCb;
import com.onebyte_llc.imageviewer.vlc.video.VlcVideoLockCb;
import com.onebyte_llc.imageviewer.vlc.video.VlcVideoUnlockCb;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

/**
 * 1. Create VLC
 * 2. Create Media (with some path)
 * 3. Create MediaPlayer using the Media objcct
 */
public interface Vlc extends Library {

    //////////////
    // VLC
    /////////////

    /**
     * libvlc_instance_t* libvlc_new(int argc, const char *const *argv)
     */
    VlcInstance libvlc_new(int argc, String[] argv);


    //////////////
    // MEDIA
    /////////////

    /**
     * libvlc_media_t* libvlc_media_new_path (const char *path)
     * <br>
     * https://videolan.videolan.me/vlc/group__libvlc__media.html#ga2fc5ea9f8cf0e0d598c357ebb09aab52
     */
    VlcMedia libvlc_media_new_path(VlcInstance instance, String path);

    /**
     * libvlc_media_t* 	libvlc_media_retain(libvlc_media_t *p_md)
     * <br>
     * https://videolan.videolan.me/vlc/group__libvlc__media.html#ga6f359cf52fa1beeb8c9f90921b058af4
     */
    VlcMedia libvlc_media_release(VlcMedia media);

    /**
     * https://videolan.videolan.me/vlc/group__libvlc__media.html#gae5f3abfa89a678ff33f768d9c48457ff
     * <p>
     * Parse the media asynchronously with options.
     * <p>
     * This fetches (local or network) art, meta data and/or tracks information.
     * <p>
     * To track when this is over you can listen to libvlc_MediaParsedChanged event. However if this functions returns an error, you will not receive any events.
     * <p>
     * It uses a flag to specify parse options (see libvlc_media_parse_flag_t). All these flags can be combined. By default, media is parsed if it's a local file.
     * <br>
     * inst	LibVLC instance that is to parse the media
     * p_md	media descriptor object
     * parse_flag	parse options:
     * timeout	maximum time allowed to preparse the media. If -1, the default "preparse-timeout"
     * option will be used as a timeout. If 0, it will wait indefinitely. If > 0,
     * the timeout will be used (in milliseconds).
     */
    int libvlc_media_parse_with_options(VlcMedia media, int parse_flag, int timeout);

    int libvlc_media_get_parsed_status(VlcMedia media);

    /**
     * Get duration (in ms) of media descriptor object item.
     *
     * @param media
     * @return
     */
    int libvlc_media_get_duration(VlcMedia media);

    /**
     * Get media descriptor's elementary streams description
     * <p>
     * Note, you need to call libvlc_media_parse() or play the media at least once
     * before calling this function.
     * Not doing this will result in an empty array.
     * <p>
     * \version LibVLC 2.1.0 and later.
     * <p>
     * \param p_md media descriptor object
     * \param tracks address to store an allocated array of Elementary Streams
     * descriptions (must be freed with libvlc_media_tracks_release
     * by the caller) [OUT]
     * <p>
     * \return the number of Elementary Streams (zero on error)
     */
    int libvlc_media_tracks_get(VlcMedia media, PointerByReference p1);

    void libvlc_media_tracks_release(Pointer pointer, int i_count);


    //////////////
    // MEDIA PLAYER
    /////////////

    VlcMediaPlayer libvlc_media_player_new(VlcInstance vlcInstance);

    /**
     * libvlc_media_player_t* libvlc_media_player_new_from_media(libvlc_instance_t* inst, libvlc_media_t* p_md)
     */
    VlcMediaPlayer libvlc_media_player_new_from_media(VlcMedia media);

    /**
     * void libvlc_media_player_set_media(libvlc_media_player_t *p_mi, libvlc_media_t *p_md)
     * <br>
     * https://videolan.videolan.me/vlc/group__libvlc__media__player.html#gadeb7ac440f41dbb2aa1a7811904099b1
     */
    void libvlc_media_player_set_media(VlcMediaPlayer mediaPlayer, VlcMedia media);

    /**
     * int 	libvlc_media_player_play (libvlc_media_player_t *p_mi)
     * <br>
     * https://videolan.videolan.me/vlc/group__libvlc__media__player.html#gac5da60e52d11c81c7a6a61f470aa1646
     * <br>
     * 0 if playback started (and was already started), or -1 on error.
     */
    int libvlc_media_player_play(VlcMediaPlayer mediaPlayer);

    /**
     * play/resume if zero, pause if non-zero
     *
     * @param mediaPlayer
     * @param do_pause
     */
    void libvlc_media_player_set_pause(VlcMediaPlayer mediaPlayer, int do_pause);

    void libvlc_media_player_stop(VlcMediaPlayer mediaPlayer);

    void libvlc_media_player_release(VlcMediaPlayer player);

    /**
     * @param mediaPlayer
     * @param f_pos       between 0.0 and 1.0
     * @param b_fast
     * @return
     */
    int libvlc_media_player_set_position(VlcMediaPlayer mediaPlayer, float f_pos, boolean b_fast);

    /**
     * @param mediaPlayer
     * @return between 0.0 and 1.0
     */
    float libvlc_media_player_get_position(VlcMediaPlayer mediaPlayer);

    /**
     * https://videolan.videolan.me/vlc/group__libvlc__media__player.html#ga612605f2e5c638d9f4ed59021d714bf0
     * Set callbacks and private data to render decoded video to a custom area in memory.
     * <br>
     * mp	the media player
     * lock	callback to lock video memory (must not be NULL)
     * unlock	callback to unlock video memory (or NULL if not needed)
     * display	callback to display video (or NULL if not needed)
     * opaque	private pointer for the three callbacks (as first parameter)
     */
    void libvlc_video_set_callbacks(VlcMediaPlayer mediaPlayer, VlcVideoLockCb lock,
                                    VlcVideoUnlockCb unlock, VlcVideoDisplayCb display, Pointer p);

    /**
     * https://videolan.videolan.me/vlc/group__libvlc__media__player.html#ga6518394e05d458731c11c86edb23f4cc
     * Set decoded video chroma and dimensions.
     * <br>
     * mp	the media player
     * chroma	a four-characters string identifying the chroma (e.g. "RV32" or "YUYV")
     * width	pixel width
     * height	pixel height
     * pitch	line pitch (in bytes)
     */
    void libvlc_video_set_format(VlcMediaPlayer player, String chroma, int width, int height, int pitch);


    ////////////
    // LOGS
    ////////////
    void libvlc_clearerr();

    String libvlc_errmsg();

}
