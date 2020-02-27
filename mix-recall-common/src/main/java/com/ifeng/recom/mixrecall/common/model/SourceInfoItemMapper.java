package com.ifeng.recom.mixrecall.common.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SourceInfoItemMapper  implements RowMapper<SourceInfoItem> {

    @Override
    public SourceInfoItem mapRow(ResultSet rs, int num) throws SQLException {
        //从结果集里把数据得到
        String manuscriptName=rs.getString("manuscriptName");
        String evalLevel=rs.getString("comEvalLevel");
        //把数据封装到对象里
        SourceInfoItem sourceInfoItem=new SourceInfoItem();
        sourceInfoItem.setManuscriptName(manuscriptName);
        sourceInfoItem.setComEvalLevel(evalLevel);
        return sourceInfoItem;
    }
}
