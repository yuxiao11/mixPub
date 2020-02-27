package com.ifeng.recom.mixrecall.controller;

import com.ifeng.recom.mixrecall.biz.impl.AssembleBizImpl;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.LogFileName;
import com.ifeng.recom.mixrecall.common.dao.hbase.UserCFClick;
import com.ifeng.recom.mixrecall.common.model.UserCF;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.LoggerUtils;
import com.ifeng.recom.mixrecall.common.util.JsonUtil;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.core.cache.UserProfileCache;
import com.ifeng.recom.mixrecall.core.util.DebugInfoUtils;
import com.ifeng.recom.mixrecall.support.RequestSupport;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
//添加负反馈相关包


/**
 * Created by jibin on 2017/11/17.
 * 召回异步调用入口，对任务耗时敏感度低
 * 业务：
 * storm头条增量
 *
 */
@RestController
@RequestMapping("/mixrecom")
public class RecomController {
    private static final Logger logger = LoggerFactory.getLogger(RecomController.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);
    private static final Logger accessLogger = LoggerUtils.Logger(LogFileName.ACCESS);


    private final AssembleBizImpl assembleBiz;


    @Autowired
    public RecomController(AssembleBizImpl assembleBiz, RequestSupport requestSupport) {
        this.assembleBiz = assembleBiz;
    }


    /**
     * 健康检查接口
     *
     * @return ok
     */
    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "ok";
    }

    @GetMapping("/")
    public String index() {
        return "test ok,now=" + new java.util.Date();
    }

    /**
     * 获取当前机器ip
     *
     * @return ip
     */
    @RequestMapping(value = {"/ip"}, method = RequestMethod.GET)
    @ResponseBody
    public String getIp(@RequestParam(value = "uid", required = false) String uid,
                        @RequestParam(value = "debugType", required = false, defaultValue = "ip") String debugType,
                        @RequestParam(value = "tableName", required = false, defaultValue = "recom_usercf_dssm") String tableName) {
        try {
            String result = "";
            if (GyConstant.debugType_userModel.equals(debugType)) {
                UserModel userModel = UserProfileCache.getUserModel(uid);
                result = JsonUtil.object2jsonWithoutException(userModel);
            } else if (debugType.startsWith(GyConstant.debugType_userCf)) {
                UserCF userCF = null;
                switch (tableName) {
                    case "recom_usercf":
                        userCF = UserCFClick.getNeighborClick(uid);
                        break;
                    case "recom_usercf_als":
                        userCF = UserCFClick.getNeighborClick_als(uid);
                        break;
                    case "recom_usercf_cache":
                        userCF = UserCFClick.getNeighborClickAlsFromRedis(uid);
                        break;
                    default:
                        userCF = UserCFClick.getNeighborClick_dssm(uid);
                        break;
                }
                if (GyConstant.debugType_userCf_detail.equals(debugType)) {
                    Map<Integer, List<String>> neighborClick = userCF.getNeighborClick();
                    if (MapUtils.isNotEmpty(neighborClick)) {
                        for (Map.Entry<Integer, List<String>> m : neighborClick.entrySet()) {
                            List<String> simIdList = m.getValue();
                            Integer key = m.getKey();
                            Set set = new HashSet(simIdList);
                            List<String> newResult = DebugInfoUtils.getBatchDocForSimId(set);
                            neighborClick.put(key, newResult);
                        }
                    }
                    userCF.setNeighborClick(neighborClick);
                }
                result = JsonUtil.object2jsonWithoutException(userCF);
            } else {
                InetAddress addr = InetAddress.getLocalHost();
                String hostname = addr.getHostName();
                result = "ip:" + addr.getHostAddress() + "  hostname:" + hostname;
            }
            return result;
        } catch (UnknownHostException e) {
            logger.error("request ip", e);
        }
        return "error";
    }


    /**
     * mix推荐对外暴露的主接口
     *
     * @param requestInfo 客户端请求的json串，方便扩展
     * @return String
     */
    @RequestMapping(value = {"/list"}, method = RequestMethod.POST)
    @ResponseBody
    public String list(String requestInfo) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("total");

        String uid = null;
        String result = "";
        String flowType = null;

        try {
            MixRequestInfo mixRequestInfo = GsonUtil.json2Object(requestInfo, MixRequestInfo.class);
            uid = mixRequestInfo.getUid();
            flowType = mixRequestInfo.getFlowType();

            accessLogger.info("uid:{} type:{} request:{}", uid, flowType, requestInfo);

            if (StringUtils.isBlank(flowType)) {
                logger.warn("uid:{} flowType is null", uid);
                return result;
            }

            //各种组装逻辑策略统一返回string结果，方便后面兼容不同的数据结构
            result = assembleBiz.doRecom(mixRequestInfo);

        } catch (Exception e) {
            logger.error("uid:{},controller {}", uid, e);
            e.printStackTrace();
        } finally {
            timer.addEndTime("total");
            timeLogger.info("mixRecom {} uid:{},flowType:{}", timer.getStaticsInfo(), uid, flowType);
            TimerEntityUtil.remove();
        }

        return result;
    }
}