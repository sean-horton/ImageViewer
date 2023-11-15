package com.onebyte_llc.imageviewer.logger;

public interface LogAppender {

    void write(String msg);

    static LogAppender stdout() {
        return System.out::println;
    }

}
