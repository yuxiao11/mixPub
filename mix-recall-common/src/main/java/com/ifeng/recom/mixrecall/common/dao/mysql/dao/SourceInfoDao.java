package com.ifeng.recom.mixrecall.common.dao.mysql.dao;

import com.ifeng.recom.mixrecall.common.config.ApplicationConfig;
import com.ifeng.recom.mixrecall.common.constant.ApolloConstant;
import com.ifeng.recom.mixrecall.common.dao.mysql.mapper.SourceInfoMapper;
import com.ifeng.recom.mixrecall.common.model.LowTagInfoItem;
import com.ifeng.recom.mixrecall.common.model.SourceInfoItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lilg1 on 2018/3/30.
 */
@Repository
public class SourceInfoDao {

    @Autowired
    private SourceInfoMapper sourceInfoMapper;

    public List<String> getOrganizationMedia() {
        return sourceInfoMapper.selectOrganizationSource();
    }

    public List<SourceInfoItem> getSourceInfo(){
        return sourceInfoMapper.selectSourceInfo();
    }

    public List<LowTagInfoItem> getLowTagInfo(){
        return sourceInfoMapper.selectLowTagInfo();
    }

    public List<String> getSansuSimIds() {
        if (ApolloConstant.Switch_on.equals(ApplicationConfig.getProperty(ApolloConstant.WxbContentSecuritySwitch))) {
            return sourceInfoMapper.selectSansuSourceAll();
        } else {
            return sourceInfoMapper.selectSansuSource();
        }
    }
}
