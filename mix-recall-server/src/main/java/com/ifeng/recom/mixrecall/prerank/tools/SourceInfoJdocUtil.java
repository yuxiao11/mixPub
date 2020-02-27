package com.ifeng.recom.mixrecall.prerank.tools;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jibin on 2018/2/9.
 */
public class SourceInfoJdocUtil {
    private static final Logger logger = LoggerFactory.getLogger(SourceInfoJdocUtil.class);

    private static Connection getCon() {
        Connection conn = null;
        String driverClass = "com.mysql.jdbc.Driver";
        String dbUrl = "jdbc:mysql://10.80.134.20:3307/al_basic_data";      // 从库
//        String dbUrl = "jdbc:mysql://10.80.134.20:3306/al_basic_data";    // 主库
        String userName = "al_basic_data";
        String password = "RpJc4ujmnbrgdHGe";
        try {
            Class.forName(driverClass);
            String url = dbUrl;
            logger.info("IfengDataRubikJdocUtil 获取数据库连接：URL-->" + url + "用户名-->" + userName + "密码-->" + password);
            conn = DriverManager.getConnection(url, userName, password);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getCon ERROR:{}", e);
        }
        return conn;
    }


    /**
     * 获取视频的媒体信息
     *
     * @return
     */
    public static List<SourceInfoItem> getVideoSourceInfo() {
        Connection con = null;
        List<SourceInfoItem> sourceInfoItemsList = new ArrayList<>();
        try {
            con = getCon();
            Statement myStmt = con.createStatement();
            String sql = "select manuscriptName,comEvalLevel from video_evalLevel_used ";

            ResultSet rs = myStmt.executeQuery(sql);
            while (rs.next()) {

                String manuscriptName = rs.getString("manuscriptName");
                String evalLevel = rs.getString("comEvalLevel");
                if (StringUtils.isNotBlank(manuscriptName) && StringUtils.isNotBlank(evalLevel)) {
                    SourceInfoItem sourceInfoItem = new SourceInfoItem();
                    sourceInfoItem.setManuscriptName(manuscriptName);
                    sourceInfoItem.setEvalLevel(evalLevel);
                    sourceInfoItemsList.add(sourceInfoItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("getVideoSourceInfo ERROR:{}", e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sourceInfoItemsList;
    }


    /**
     * 获取图文的媒体信息
     *
     * @return
     */
    public static List<SourceInfoItem> getDocSourceInfo() {
        Connection con = null;
        List<SourceInfoItem> sourceInfoItemsList = new ArrayList<>();
        try {
            con = getCon();
            Statement myStmt = con.createStatement();
            String sql = "select manuscriptName,comEvalLevel from evalLevel_used  ";

            ResultSet rs = myStmt.executeQuery(sql);
            while (rs.next()) {

                String manuscriptName = rs.getString("manuscriptName");
                String evalLevel = rs.getString("comEvalLevel");
                if (StringUtils.isNotBlank(manuscriptName) && StringUtils.isNotBlank(evalLevel)) {
                    SourceInfoItem sourceInfoItem = new SourceInfoItem();
                    sourceInfoItem.setManuscriptName(manuscriptName);
                    sourceInfoItem.setEvalLevel(evalLevel);
                    sourceInfoItemsList.add(sourceInfoItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("getVideoSourceInfo ERROR:{}", e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sourceInfoItemsList;
    }

    public static void main(String[] args) {
        List<SourceInfoItem> videoSourceInfo=getVideoSourceInfo();
        List<SourceInfoItem> docSourceInfo=getDocSourceInfo();
        System.out.println(videoSourceInfo.size());
        System.out.println(docSourceInfo.size());
    }

}
