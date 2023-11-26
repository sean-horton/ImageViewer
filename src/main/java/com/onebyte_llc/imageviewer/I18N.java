/*
 *     ImageViewer - free image viewing gui
 *     Copyright (C) 2023  Sean Horton
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

package com.onebyte_llc.imageviewer;

import com.onebyte_llc.imageviewer.logger.Logger;
import javafx.beans.binding.StringBinding;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {

    private static final Logger LOG = Logger.getInstance(I18N.class);
    private static final List<WeakReference<StringBinding>> BINDINGS = new LinkedList<>();
    private static ResourceBundle BUNDLE = ResourceBundle.getBundle("i18n.en");

    public static void setLocale(Locale locale) {
        try {
            BUNDLE = ResourceBundle.getBundle("i18n." + locale.getLanguage());
        } catch (Exception e) {
            LOG.warn("No localization found for: {0}. Using previous.", locale.getLanguage());
        }

        Iterator<WeakReference<StringBinding>> iter = BINDINGS.iterator();
        while (iter.hasNext()) {
            WeakReference<StringBinding> next = iter.next();
            if (next.get() == null) {
                iter.remove();
            } else {
                next.get().invalidate();
            }
        }
    }

    public static StringBinding get(String key, Object... args) {
        StringBinding sb = new StringBinding() {
            @Override
            protected String computeValue() {
                try {
                    return BUNDLE.getString(key).formatted(args);
                } catch (Exception e) {
                    return "NO_TRANSLATION";
                }
            }
        };
        BINDINGS.add(new WeakReference<>(sb));
        return sb;
    }

}
