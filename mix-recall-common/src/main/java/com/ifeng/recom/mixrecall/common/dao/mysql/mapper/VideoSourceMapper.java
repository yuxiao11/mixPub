package com.ifeng.recom.mixrecall.common.dao.mysql.mapper;

import com.ifeng.recom.mixrecall.common.model.SourceInfoItem;
import com.ifeng.recom.mixrecall.common.model.SourceInfoItemMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lilg1 on 2018/3/30.
 */
@Service
public class VideoSourceMapper {
    @Autowired
    @Qualifier("weMdiaJdbcTemplate")
    protected JdbcTemplate jdbcTemplate1;

    public List<SourceInfoItem> selectVideoSourceInfo(){
        return   jdbcTemplate1.query("select manuscriptName,comEvalLevel from video_evalLevel_used",new BeanPropertyRowMapper<>(SourceInfoItem.class));
    }
//    //获取视频媒体评级信息
//    @Select("select manuscriptName,evalLevel from video_evalLevel_used ")
//    List<SourceInfoItem> selectVideoSourceInfo();
}
