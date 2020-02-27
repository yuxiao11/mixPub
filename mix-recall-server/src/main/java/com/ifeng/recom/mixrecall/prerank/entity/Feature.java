package com.ifeng.recom.mixrecall.prerank.entity;
import com.ifeng.recom.mixrecall.prerank.constant.Constant;
import com.ifeng.recom.mixrecall.prerank.executor.Operator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

/**
 * 特征类：定义特征抽取的方式和操作算子，对应特征配置文件每行数据
 * Created by zhaozp on 2017/5/11.
 */
public class Feature implements Serializable {
    private static final long serialVersionUID = 2430881983334036432L;

    protected static final Log LOG = LogFactory.getLog(Feature.class);

    protected Long featureId ;//特征ID（数字字符串）

    protected String featureName;//特征名称

    protected int type;//特征类型：0(指标),1（连续），2（离散）,3(GBDT),4(评分)，5（LDA）,6(fm);

    protected String attrDimension; // 特征作用维度，相当于特征采用的数据维度字段，及作用方式;

    protected Operator operator;//特征提取的计算算子的名称

    protected boolean status;//特征是否使用: 1(使用),0(不使用)

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAttrDimension() {
        return attrDimension;
    }

    public void setAttrDimension(String attrDimension) {
        this.attrDimension = attrDimension;
    }

    public Feature(){

    }

    public Feature(Long featureId, String featureName, int type,
                   Operator operator, String attrDimension,boolean status) {
        super();
        this.featureId = featureId;
        this.featureName = featureName;
        this.type = type;
        this.operator = operator;
        this.attrDimension=attrDimension;
        this.status = status;
    }

    public Feature(Long featureId, String featureName, int type,
                   Operator operator, boolean status) {
        this(featureId,featureName,type,operator,"-",status);
    }

    public Feature(Long featureId, String featureName,
                   Operator operator,String attrDimension, boolean status) {
        this(featureId,featureName,0,operator,attrDimension,status);
    }

    public Feature(Long featureId, String featureName,
                   Operator operator, boolean status) {
        this(featureId,featureName,0,operator,"-",status);
    }

    /**
     * 解析特征配置文档
     * @param line
     * @return
     */
    public static Feature parse(String line){
        String[] array = line.split("\\s+");
        if (array.length < 6) {
            LOG.warn("请注意本行小于6列. line: " + line);
            return null;
        }
        try {
            StringBuffer sb = new StringBuffer();

            if (!array[3].contains(".")) {
                sb.append(Constant.GENERAL_FEATURE_PACKAGE).append(".");
            }
            sb.append(array[3]);
            Class<?> cl = Class.forName(sb.toString());
            Operator operator = (Operator)cl.newInstance();
            long featureId = Long.parseLong(array[0]);
            if (featureId >= 0) {
                Feature item = null;
                if(array.length == 6){
                    item = new Feature(featureId,array[1],Integer.parseInt(array[2]),operator,array[4],(array[5].equals("1"))?true:false);
                }else{
                    item = new Feature(featureId,array[1],0,operator,"-",(array[5].equals("1"))?true:false);
                }
                return item;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        LOG.info("特征配置转换为实体错误. line: " + line);
        return null;
    }


}
