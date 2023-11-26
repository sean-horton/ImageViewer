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

package com.onebyte_llc.imageviewer.logger;

import java.time.Instant;

class LogItem {

    private final long time;
    private final LogLevel level;
    private final String name;
    private final String format;
    private final Object[] values;
    private final Throwable throwable;

    public LogItem(LogLevel level, String name, String format, Throwable throwable, Object... values) {
        this.time = System.currentTimeMillis();
        this.level = level;
        this.name = name;
        this.format = format;
        this.throwable = throwable;
        this.values = values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb
                .append(Instant.ofEpochMilli(time).toString())
                .append(" [").append(level).append("] ")
                .append(name)
                .append(" - ");

        buildFormat(sb, format, values);
        if (throwable != null) {
            buildException(sb, throwable);
        }

        return sb.toString();
    }

    private void buildFormat(StringBuilder sb, String format, Object... values) {
        int i = 0;
        int off = 0;
        while (i < format.length() - 1 && off < values.length) {
            if (format.charAt(i) == '{' && format.charAt(i + 1) == '}') {
                sb.append(values[off++]);
                i++;
            } else {
                sb.append(format.charAt(i));
            }
            i++;
        }

        while (i < format.length()) {
            sb.append(format.charAt(i++));
        }
    }

    private void buildException(StringBuilder sb, Throwable t) {
        sb.append("\n\t").append(t.getLocalizedMessage());
        for (StackTraceElement stack : t.getStackTrace()) {
            sb.append("\n\t\t");
            sb.append(stack.toString());
        }
    }
}
