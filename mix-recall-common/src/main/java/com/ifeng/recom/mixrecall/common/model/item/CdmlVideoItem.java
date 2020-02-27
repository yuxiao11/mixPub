package com.ifeng.recom.mixrecall.common.model.item;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by liligeng on 2019/5/22.
 */

@Getter
@Setter
public class CdmlVideoItem {

    private String simId;

    private Double score;

    private String guid;

    public CdmlVideoItem(String simId, String guid, Double score){
        this.simId = simId;
        this.guid = guid;
        this.score = score;
    }


    public static class  CdmlVideoItemComparator implements Comparator<CdmlVideoItem> {

        @Override
        public int compare(CdmlVideoItem o1, CdmlVideoItem o2) {
            return o2.getScore().compareTo(o1.getScore());
        }
    }

    public static void main(String[] args) {
        CdmlVideoItem cdmlVideoItem = new CdmlVideoItem("","",0.2);
        CdmlVideoItem cdmlVideoItem1 = new CdmlVideoItem("","",0.1);
        CdmlVideoItem cdmlVideoItem2 = new CdmlVideoItem("","",0.3);
        List<CdmlVideoItem> list = new ArrayList<>();
        list.add(cdmlVideoItem);
        list.add(cdmlVideoItem1);
        list.add(cdmlVideoItem2);
        CdmlVideoItemComparator comparator = new CdmlVideoItemComparator();
        list.sort(comparator);
        for(CdmlVideoItem item : list){
            System.out.println(item.getScore());
        }

    }
}
