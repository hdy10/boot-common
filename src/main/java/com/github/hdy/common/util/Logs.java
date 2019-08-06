package com.github.hdy.common.util;


import lombok.extern.slf4j.Slf4j;

/**
 * Created by hdy on 2017/10/17.
 */
@Slf4j
public class Logs {

    public static void debug(Object msg) {
        log.debug("\u001b[33m" + msg + "\u001b[0m");
    }

    public static void info(Object msg) {
        log.info("\u001b[35m" + msg + "\u001b[0m");
    }


    public static void warn(Object msg) {
        log.warn("\u001b[34m" + msg + "\u001b[0m");
    }

    public static void error(Object msg) {
        log.error("\u001b[31m" + msg + "\u001b[0m");
    }

    public static void printHr(Object msg) {
        System.out.println("\u001b[33m" + msg + "\u001b[0m");
    }

}
