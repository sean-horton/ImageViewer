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
