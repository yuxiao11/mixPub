package com.ifeng.recom.mixrecall.negative;

import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;

import java.util.List;
import java.util.Map;

public interface IItemProfileService {

    public Map<String, ItemProfile> getItemProfileModel(List<Item> itemList, MixRequestInfo requestInfo);

}
