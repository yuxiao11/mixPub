package com.ifeng.recom.mixrecall.common.util;

public class MathTools {

    public static class Accumulator {
        private int i;

        private Accumulator(int i) {
            this.i = i;
        }

        public static Accumulator newAccumulator(int initNum) {
            return new Accumulator(initNum);
        }

        public int add(int num) {
            int o = i;
            i += num;
            return o;
        }

        public int add_1() {
            return i++;
        }

    }

    /**
     * 将num控制在区间范围内
     *
     * @param min
     * @param max
     * @param num
     * @return
     */
    public final static long closedInterval(long min, long max, long num) {
        if (min > max) {
            return Math.min(Math.max(max, num), min);
        }
        return Math.min(Math.max(min, num), max);
    }

    public static void main(String[] args) {
        System.out.println(closedInterval(200,200,150));
    }
}
