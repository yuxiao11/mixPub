package com.ifeng.recom.mixrecall.common.dao.mysql.mapper;


import com.ifeng.recom.mixrecall.common.model.LowTagInfoItem;
import com.ifeng.recom.mixrecall.common.model.SourceInfoItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by lilg1 on 2018/3/30.
 */
@Service
public class SourceInfoMapper {

    @Autowired
    @Qualifier("weMdiaJdbcTemplate")
    protected JdbcTemplate jdbcTemplate1;

    @Autowired
    @Qualifier("sansuJdbcTemplate")
    protected JdbcTemplate jdbcTemplate2;


   public List<String> selectOrganizationSource(){

       List<String> resultStr = jdbcTemplate1.query("select manuscriptName from evalLevel_used where mediaType = 2", new RowMapper<String>(){
           public String mapRow(ResultSet rs, int rowNum)
                   throws SQLException {
               return rs.getString(1);
           }
       });
     return   resultStr;
   }

    public List<SourceInfoItem> selectSourceInfo(){
            return   jdbcTemplate1.query("select manuscriptName,comEvalLevel from evalLevel_used",new BeanPropertyRowMapper<>(SourceInfoItem.class));
    }

    public List<LowTagInfoItem> selectLowTagInfo(){
        return   jdbcTemplate1.query("select recomId,auditTags,insertTime from NewsAccessTag",new BeanPropertyRowMapper<>(LowTagInfoItem.class));
    }

    public List<String> selectSansuSource(){

        List<String> resultStr = jdbcTemplate2.query("SELECT simid from GarbageNewsAssess WHERE isRecover=0 ", new RowMapper<String>(){
            public String mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                return rs.getString(1);
            }
        });
        return   resultStr;
    }

    public List<String> selectSansuSourceAll(){

        List<String> resultStr = jdbcTemplate2.query("SELECT simid from GarbageNewsAssess", new RowMapper<String>(){
            public String mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                return rs.getString(1);
            }
        });
        return   resultStr;
    }

}
