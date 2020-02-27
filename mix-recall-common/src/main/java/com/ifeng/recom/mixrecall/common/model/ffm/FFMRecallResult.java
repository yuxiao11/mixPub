package com.ifeng.recom.mixrecall.common.model.ffm;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by liligeng on 2019/6/11.
 */
@Getter
@Setter
public class FFMRecallResult {

    private String tag;

    private List<FFMRecallData> data;
}
