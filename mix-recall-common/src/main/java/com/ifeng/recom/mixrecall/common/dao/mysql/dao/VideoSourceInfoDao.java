package com.ifeng.recom.mixrecall.common.dao.mysql.dao;

import com.ifeng.recom.mixrecall.common.dao.mysql.mapper.SourceInfoMapper;
import com.ifeng.recom.mixrecall.common.dao.mysql.mapper.VideoSourceMapper;
import com.ifeng.recom.mixrecall.common.model.SourceInfoItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lilg1 on 2018/3/30.
 */
@Repository
public class VideoSourceInfoDao {

    @Autowired
    private VideoSourceMapper videoSourceMapper;

    public List<SourceInfoItem> getVideoSourceInfo(){
        return videoSourceMapper.selectVideoSourceInfo();
    }


}
