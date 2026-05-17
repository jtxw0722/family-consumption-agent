package com.jtxw.familyagent.domain.model;

/**
 * @Author: jtxw
 * @Date: 2026/05/11/00:36
 * @Description: 价格判断结果对象，承载当前单价、历史统计和决策说明。
 */
public class PriceDecisionResult {
    /**
     * 原始商品名称
     */
    private final String productName;
    /**
     * 归一化后的商品名称
     */
    private final String normalizedName;
    /**
     * 本次输入价格折算后的单位价格
     */
    private final double currentUnitPrice;
    /**
     * 单位价格使用的计量单位
     */
    private final String unit;
    /**
     * 历史最低单位价格；样本不足时为空
     */
    private final Double historicalMin;
    /**
     * 历史单位价格中位数；样本不足时为空
     */
    private final Double historicalMedian;
    /**
     * 历史单位价格平均值；样本不足时为空
     */
    private final Double historicalAverage;
    /**
     * 用于本次价格判断的历史样本数量
     */
    private final int sampleSize;
    /**
     * 机器可读的价格判断编码
     */
    private final String decision;
    /**
     * 面向用户展示的价格判断文案
     */
    private final String decisionText;
    /**
     * 价格判断原因说明
     */
    private final String reason;

    public PriceDecisionResult(String productName,
                               String normalizedName,
                               double currentUnitPrice,
                               String unit,
                               Double historicalMin,
                               Double historicalMedian,
                               Double historicalAverage,
                               int sampleSize,
                               String decision,
                               String decisionText,
                               String reason) {
        this.productName = productName;
        this.normalizedName = normalizedName;
        this.currentUnitPrice = currentUnitPrice;
        this.unit = unit;
        this.historicalMin = historicalMin;
        this.historicalMedian = historicalMedian;
        this.historicalAverage = historicalAverage;
        this.sampleSize = sampleSize;
        this.decision = decision;
        this.decisionText = decisionText;
        this.reason = reason;
    }

    public String productName() {
        return productName;
    }

    public String normalizedName() {
        return normalizedName;
    }

    public double currentUnitPrice() {
        return currentUnitPrice;
    }

    public String unit() {
        return unit;
    }

    public Double historicalMin() {
        return historicalMin;
    }

    public Double historicalMedian() {
        return historicalMedian;
    }

    public Double historicalAverage() {
        return historicalAverage;
    }

    public int sampleSize() {
        return sampleSize;
    }

    public String decision() {
        return decision;
    }

    public String decisionText() {
        return decisionText;
    }

    public String reason() {
        return reason;
    }

    public String getProductName() {
        return productName;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public double getCurrentUnitPrice() {
        return currentUnitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public Double getHistoricalMin() {
        return historicalMin;
    }

    public Double getHistoricalMedian() {
        return historicalMedian;
    }

    public Double getHistoricalAverage() {
        return historicalAverage;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public String getDecision() {
        return decision;
    }

    public String getDecisionText() {
        return decisionText;
    }

    public String getReason() {
        return reason;
    }
}
