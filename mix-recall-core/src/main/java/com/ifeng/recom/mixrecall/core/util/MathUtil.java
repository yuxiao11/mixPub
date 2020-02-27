package com.ifeng.recom.mixrecall.core.util;

import com.google.common.collect.Maps;

import java.util.*;
import java.util.zip.CRC32;


/**
 * Created by jibin on 2017/7/19.
 */
public class MathUtil {

    /**
     * 实验分流的分母
     */
    private static final int rateMax=10000;

    /**
     * 生成0到max的随机数， 不包括max的 开区间
     *
     * @param max
     * @return
     */
    public static int getNum(int max) {
        return (int) (Math.random() * max);
    }


    public static void main(String[] args) {
        System.out.println(Math.random());
        System.out.println((int)(Math.round(Math.ceil(0.22)*2.50)));
        System.out.println(Math.ceil(0.22)*2.50);
        Map<String,Integer> vv = Maps.newHashMap();
        System.out.println(vv.containsKey("x"));

        List<Integer> num = new ArrayList<>();
        num.add(1);
        System.out.println(num.stream().mapToInt(Integer::intValue).sum());
        String sb = "[]";
        System.out.println(sb.startsWith("["));
        System.out.println(sb.startsWith("\\["));
        Map<String,String> s5 = new HashMap<>();
        System.out.println(s5.containsKey("s"));
        List<Map<String,String>> a2 = new ArrayList<>();
        List<String> a3 = new ArrayList<>();
        System.out.println(a3.size());

        a3.add("a");
        System.out.println("-------------------------------");
        System.out.println(a3.subList(0,0));
        System.out.println(a3.size());
        System.out.println(a2.getClass());
        System.out.println(a3.getClass());
        System.out.println(sb.getClass().getSimpleName());
        System.out.println(s5.getClass().getSimpleName());
        System.out.println(sb.split("\\^")[0]);
        System.out.println(sb.split("\\^"));
        String s = "娱乐-剧本 电视娱乐-剧本 娱乐-演员 电视娱乐-演员 娱乐-寒冬";
        String[] s2 = s.split(" ");
        System.out.println(s2);
        System.out.println(s2.length);
        for(String s3: s2){
            System.out.println(s3.split("-")[1]);
        }
        Map<String,String> test = new HashMap<>();
        System.out.println(test.containsKey("xx"));
        System.out.println("  s                 ");
        System.out.println(test.get("asd") == null);


//        long abtestFlag = getNumByUid( mixRequestInfo.getUid(),"CateFilterTest"); // 此处按照
//        if(abtestFlag < 2000){
//            abtestMap.put("CateFilterTest","Filter_test_20");
//
//        }else if(abtestFlag < 4000) {
//            abtestMap.put("CateFilterTest","Filter_base_20");
//
//        }else if(abtestFlag < 6000) {
//            abtestMap.put("GraphCotagTest","Graph_Cotag_test_20");
//
//        }else if(abtestFlag < 8000) {
//            abtestMap.put("GraphCotagTest","Graph_Cotag_base_20");
//
//        }
        int r1 = 0;
        int r2 = 0;
        for(int i = 0; i<10000000;i++){
            String test1 = "a" + i;
            long testtag = getNumByUid(test1,"CateFilterTest");
            if(testtag < 2000){
                r1 +=1;
            }else if(testtag < 4000) {
                r2 +=1;
            }
        }

        System.out.println(r1);
        System.out.println(r2);

    }


    /**
     * 使用uid进行abtest分组，分母为100，同一个uid 会打到同一个分组中
     * @param uid
     * @return
     */
    @Deprecated
    public static long getNumByUid(String uid){
        CRC32 crc32 = new CRC32();
        crc32.update((uid).getBytes());
        long result = crc32.getValue() % rateMax;
        return result;
    }


    /**
     * 使用uid进行abtest分组，分母为100，同一个uid 会打到同一个分组中
     * @param uid
     * @return
     */
    public static long getNumByUid(String uid,String group){
        CRC32 crc32 = new CRC32();
        crc32.update((uid+group).getBytes());
        long result = crc32.getValue() % rateMax;
        return result;
    }


    /**
     * 使用uid进行abtest分组，分母为100，同一个uid 会打到同一个分组中
     *
     * @param uid
     * @return
     */
    @Deprecated
    public static long getNumByUidUser(String uid, String group) {
        CRC32 crc32 = new CRC32();
        crc32.update((uid + group).getBytes());
        long result = crc32.getValue() % 2;
        return result;
    }




    /**
     * 判断是实验路流量
     *
     * @param uid
     * @param rate_MaxNum
     * @return
     */
    @Deprecated
    public static boolean isTestFlowByUid(String uid, int rate_MaxNum) {
        return (getNumByUid(uid) < rate_MaxNum);
    }
}
