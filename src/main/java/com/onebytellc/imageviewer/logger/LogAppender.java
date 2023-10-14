package com.onebytellc.imageviewer.logger;

public interface LogAppender {

    void write(String msg);

    static LogAppender stdout() {
        return System.out::println;
    }

}
