package com.jtxw.familyagent.domain.model;

/**
 * @Author: jtxw
 * @Date: 2026/05/11/00:44
 * @Description: 月度报告生成结果对象，返回统计汇总和报告路径。
 */
public class MonthlyReportResult {
    /**
     * 报告月份，格式为 yyyy-MM
     */
    private final String month;
    /**
     * 纳入本次月度统计的消费记录数
     */
    private final int recordCount;
    /**
     * 本月纳入统计记录的总支出金额
     */
    private final double totalAmount;
    /**
     * 当前仍待人工复核的记录数
     */
    private final int pendingReviewCount;
    /**
     * 生成的 Markdown 报告文件路径
     */
    private final String reportPath;

    public MonthlyReportResult(String month, int recordCount, double totalAmount, int pendingReviewCount, String reportPath) {
        this.month = month;
        this.recordCount = recordCount;
        this.totalAmount = totalAmount;
        this.pendingReviewCount = pendingReviewCount;
        this.reportPath = reportPath;
    }

    public String month() {
        return month;
    }

    public int recordCount() {
        return recordCount;
    }

    public double totalAmount() {
        return totalAmount;
    }

    public int pendingReviewCount() {
        return pendingReviewCount;
    }

    public String reportPath() {
        return reportPath;
    }

    public String getMonth() {
        return month;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getPendingReviewCount() {
        return pendingReviewCount;
    }

    public String getReportPath() {
        return reportPath;
    }
}
