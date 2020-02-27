package com.ifeng.recom.mixrecall.negative;



import com.ifeng.recom.mixrecall.common.model.item.EvItem;

import java.util.*;

/**处理docment的feature工具类
 *
 * Created by yeben on 2018/3/15.
 */
public class FeatureUtil {
    //此处用作记录曝光和点击
    public static Map<Long,Map<String,Integer>> getEvpullnumMap(List<EvItem.EvObj> evList){
        Map<Long,Map<String,Integer>> evPullnumMap = new HashMap<>();
        List<Long> tsList = new ArrayList<>();//时间戳List
        for (EvItem.EvObj exposeInfo : evList){
            boolean isClick = exposeInfo.isC(); //是否点击
            long t = exposeInfo.getT(); //文章曝光时间
            if(!tsList.contains(t)){
                tsList.add(t);
            }
            Map<String,Integer> valueMap = evPullnumMap.get(t);
            //最终数据形式 <timeStamp=[exp:1,click:1],.......>
            if(valueMap==null){
                valueMap = new HashMap<>();
                valueMap.put("exp",1);
                if(isClick){
                    valueMap.put("clk",1);
                }else{
                    valueMap.put("clk",0);
                }
                evPullnumMap.put(t,valueMap);
            }else{
                if(isClick){
                    valueMap.put("clk",valueMap.get("clk")+1);
                }
                valueMap.put("exp",valueMap.get("exp")+1);
            }
        }
        Collections.sort(tsList);//升序排列
        for(Long ts:evPullnumMap.keySet()){
            Map<String,Integer> valueMap = evPullnumMap.get(ts);
            valueMap.put("pullnum",tsList.size() - tsList.indexOf(ts));//记录当前为倒数第几刷
        }
        return evPullnumMap;
    }

//    public static void main(String[] args) {
//        List<ExposeInfo> evList = new ArrayList<>();
//        for(int i=0;i<50;i++){
//            ExposeInfo ev = new ExposeInfo();
//            int x=100+(int)(Math.random()*5);
//            Boolean cl = (Math.random()>0.8);
//            ev.setT(x);
//            ev.setCl(cl);
//            evList.add(ev);
//        }
//        System.out.println(getEvpullnumMap(evList).toString());
//    }


}
