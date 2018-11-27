package com.hammer.lei.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by leifeifei
 */
public class ThreadExceptionHandler implements Thread.UncaughtExceptionHandler {
  private Logger LOG = LoggerFactory.getLogger(ThreadExceptionHandler.class);

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    LOG.info("execute task error in thread, error msg is " + e + ", thread name is " + t.getName());
  }
}
