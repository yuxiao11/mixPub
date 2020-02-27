package com.ifeng.recom.mixrecall.prerank;

import java.util.List;

public interface Model {
    double getDefaultValue();

    double calculate(double [] userVec, double[] itemVec, List<FeatureItem> featureValues);
    double calculate(double [] userVec , List<FeatureItem> featureValues);
    double calculate(double [] userVec, List<FeatureItem> featureItems, String modelVersion);

    double calculateAndLog(double [] userVec, double [] itemVec, List<FeatureItem> featureValues, AdNatureEntity adNature);
    double calculateAndLog(double [] userVec, List<FeatureItem> featureValues, AdNatureEntity adNature);

}
