package com.ifeng.recom.mixrecall.common.model.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * Created by liligeng on 2019/9/23.
 */
@Setter
@Getter
public class EvItem {

    private long t;

    private List<EvObj> ev;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class EvObj{

        private String i; //文章docId

        private String y; //文章召回原因

        private boolean c; //文章是否点击

        /** categories */
        private List<String> categories;

        /** lda_topic*/
        private List<String> ldatopics;

        /** subcates */
        private List<String> subcates;

        /** cotagSet */
        private Set<String> cotags;

        /** 文章曝光时间 */
        private long t;

    }
}
