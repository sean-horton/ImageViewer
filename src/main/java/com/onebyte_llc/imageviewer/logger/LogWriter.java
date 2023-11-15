package com.onebyte_llc.imageviewer.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

class LogWriter {

    private Thread thread;
    private volatile boolean stop = false;

    private BlockingQueue<LogItem> q = new LinkedBlockingQueue<>();
    private CountDownLatch stopLatch = new CountDownLatch(1);
    private List<LogAppender> mLogAppenders = new ArrayList<>();

    public LogWriter() {
        thread = new Thread(() -> {
            while (!stop) {
                try {
                    LogItem bundle = q.take();
                    String msg = bundle.toString();
                    for (LogAppender output : mLogAppenders) {
                        output.write(msg);
                    }
                } catch (InterruptedException e) {
                    break; // normal shutdown
                } catch (Exception e) {
                    System.err.println("ERROR!!!! logger failed to write " + e.getMessage());
                }
            }
            stopLatch.countDown();
        });
        thread.start();
    }

    public void addLogOutput(LogAppender logAppender) {
        if (mLogAppenders.contains(logAppender)) {
            return;
        }
        mLogAppenders.add(logAppender);
    }

    public void stop() {
        stop = true;
        thread.interrupt();
        try {
            stopLatch.await();
        } catch (Exception e) {
            // nothing to do or log
        }
    }

    public void offer(LogItem bundle) {
        if (stop) {
            // don't accept new logs
            return;
        }
        q.offer(bundle);
    }

}
