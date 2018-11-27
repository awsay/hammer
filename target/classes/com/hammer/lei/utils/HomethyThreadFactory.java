package com.hammer.lei.utils;

import java.util.concurrent.ThreadFactory;

/**
 * Created by leifeifei
 */
public class HomethyThreadFactory implements ThreadFactory {
  @Override
  public Thread newThread(Runnable r) {
    Thread thread = new Thread(r);
    thread.setUncaughtExceptionHandler(new ThreadExceptionHandler());
    return thread;
  }
}
