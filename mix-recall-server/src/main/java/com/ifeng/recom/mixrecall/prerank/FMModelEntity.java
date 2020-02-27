package com.ifeng.recom.mixrecall.prerank;

import com.ifeng.recom.mixrecall.prerank.constant.CTRConstant;
import gnu.trove.map.hash.*;

import java.util.List;

/**
 * model实体，采用Trove库作为底层map的实现，减少内存占用，降低存取时间
 * 存储FM模型中除了用户ID特征之外的模型参数
 */
public class FMModelEntity implements Model {

    double  intercept = 0.0d;
    THashMap<String, double[]> weights;
    double defaultValue = 0.0d;

    public double getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public double calculate(double[] userVec, double[] itemVec, List<FeatureItem> featureValues) {
        return 0;
    }

    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
    }
    public void setIntercept(double intercept) {
        this.intercept = intercept;
    }
    public double getIntercept() {
        return this.intercept;
    }
    public void setWeight(THashMap<String, double[]> weights) {
        this.weights = weights;
    }
    public THashMap<String, double[]> getWeights() {
        return this.weights;
    }

    public FMModelEntity() {}

    public FMModelEntity(double intercept, THashMap<String, double[]> weights) {
        this.intercept = intercept;
        this.weights = weights;
        // 默认值设置低一些
        this.defaultValue = 0.031453;
    }

    public int size() {
        return weights.size() + 1;
    }

    /**
     * FM模型计算点击率
     * @param featureValues
     * @return
     */
    public double calculate(double [] userVec, List<FeatureItem> featureValues) {
        if (featureValues != null) {
            // 线性项
            int featureSize = featureValues.size();
            double[][] tempArr = new double[featureSize][];
            double sum_w = 0.0;
            for (int i = 0; i < featureSize; i++) {
                double [] vector = getFeatureVector(userVec, featureValues.get(i));
                tempArr[i] = vector;
                if (vector == null) {
                    continue;
                }
                sum_w += vector[0];
            }

            // 交叉项
            double sumVif_sq = 0.0d;
            double sum_vifSq = 0.0d;
            for (int f = 1; f <= CTRConstant.FM_K; f++) {
                double sumVif = 0.0d;
                for (int i = 0; i < featureSize; i++) {
                    double [] vector = tempArr[i];
                    if (vector == null) {
                        continue;
                    }
                    sumVif += vector[f];
                    sum_vifSq += vector[f] * vector[f];
                }
                sumVif_sq += (sumVif * sumVif);
            }
            // 求和并截断
            double value = this.intercept + sum_w + 0.5 * (sumVif_sq - sum_vifSq);
            if (value > CTRConstant.FM_SUM_MAX) {
                value = CTRConstant.FM_SUM_MAX;
            } else if (value < CTRConstant.FM_SUM_MIN) {
                value = CTRConstant.FM_SUM_MIN;
            }

            return probabilityFunction(value);
        } else {
            return this.defaultValue;
        }
    }

    @Override
    public double calculate(double[] userVec, List<FeatureItem> featureItems, String modelVersion) {
        return 0;
    }

    @Override
    public double calculateAndLog(double[] userVec, double[] itemVec, List<FeatureItem> featureValues, AdNatureEntity adNature) {
        return 0;
    }

    private double [] getFeatureVector(double [] userVec, FeatureItem feature) {
        double [] vector;
        if (feature.getFeatureId() == CTRConstant.FM_USERID_FEATURE_ID) {
            vector = userVec;       // 用户ID特征，存储在另一个redis中
        } else {
            vector = weights.get(feature.getFeatureString());
        }
        return vector;
    }


    /**
     * 点击率预估公式
     *
     * @param value
     * @return
     */
    private static double probabilityFunction(double value) {
        return 1.0 / (1.0 + Math.exp(-value));
    }

    /**
     * 计算点击率并打日志
     * @param featureValues
     * @param adNature
     * @return
     */
    public double calculateAndLog(double [] userVec, List<FeatureItem> featureValues, AdNatureEntity adNature) {
        return calculate(userVec, featureValues);
    }

    public static void main(String[] args) {
        double[] [] vecArr = new double[10][];
        vecArr[0] = new double[5];
        vecArr[1] = new double[5];
        vecArr[2] = null;
        vecArr[3] = new double[6];
        vecArr[9] = null;
        System.out.println(vecArr[2] == null);
    }

}
