package com.onebytellc.imageviewer.logger;

import java.text.MessageFormat;

class LogItem {

    private String name;
    private String format;
    private Object[] values;
    private Exception e;

    public LogItem(String name, String format, Object... values) {
        this.name = name;
        this.format = format;
        this.values = values;
    }

    @Override
    public String toString() {
        return name + " - " + MessageFormat.format(format, values);
    }
}
