package com.ifeng.recom.mixrecall.threadpool;

/**
 * Created by liligeng on 2019/8/13.
 */
public class ConfigUtils {

    public final static int threadNum = getThreadNum();

    public static int getThreadNum(){
        return Runtime.getRuntime().availableProcessors();
    }

    public static void main(String[] args) {
        System.out.println(threadNum);
    }
}
