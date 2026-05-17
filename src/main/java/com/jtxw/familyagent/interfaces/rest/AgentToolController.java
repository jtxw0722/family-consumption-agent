package com.jtxw.familyagent.interfaces.rest;

import com.jtxw.familyagent.application.ImportApplicationService;
import com.jtxw.familyagent.application.PriceAnalysisApplicationService;
import com.jtxw.familyagent.application.ReportApplicationService;
import com.jtxw.familyagent.application.ReviewApplicationService;
import com.jtxw.familyagent.domain.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @Author: jtxw
 * @Date: 2026/05/13/10:28
 * @Description: REST Tool API 控制器，暴露导入、比价、报告和复核查询接口。
 */
@RestController
@RequestMapping("/api/tools")
public class AgentToolController {
    private final ImportApplicationService importApplicationService;
    private final PriceAnalysisApplicationService priceAnalysisApplicationService;
    private final ReportApplicationService reportApplicationService;
    private final ReviewApplicationService reviewApplicationService;

    public AgentToolController(ImportApplicationService importApplicationService,
                               PriceAnalysisApplicationService priceAnalysisApplicationService,
                               ReportApplicationService reportApplicationService,
                               ReviewApplicationService reviewApplicationService) {
        this.importApplicationService = importApplicationService;
        this.priceAnalysisApplicationService = priceAnalysisApplicationService;
        this.reportApplicationService = reportApplicationService;
        this.reviewApplicationService = reviewApplicationService;
    }

    /**
     * 导入本地订单文件。
     *
     * <p>该接口只读取用户提供的本地文件路径，不会访问电商平台、不会读取浏览器 Cookie，
     * 也不会上传订单数据。导入过程会写入本地 SQLite，并可能生成待复核记录。</p>
     *
     * @param request 文件导入请求
     * @return 导入结果，包括导入记录数和待复核记录数
     */
    @PostMapping("/import-file")
    public ImportResult importFile(@Valid @RequestBody ImportFileRequest request) {
        return importApplicationService.importCsv(Path.of(request.filePath()));
    }

    /**
     * 比较当前商品价格与历史价格，返回价格判断结果。
     *
     * @param request 当前价格比较请求
     * @return 价格判断结果，包括当前单位价格、历史统计值和判断说明
     */
    @PostMapping("/compare-price")
    public PriceDecisionResult comparePrice(@Valid @RequestBody ComparePriceRequest request) {
        return priceAnalysisApplicationService.comparePrice(request.productName(), request.price(), request.quantity(), request.unit());
    }

    /**
     * 生成指定月份的本地 Markdown 消费报告。
     *
     * <p>报告文件会写入本地 reports 目录，统计口径由应用服务和仓储层统一控制。</p>
     *
     * @param request 月度报告请求
     * @return 报告生成结果，包括统计记录数、总金额和报告路径
     */
    @PostMapping("/generate-report")
    public MonthlyReportResult generateReport(@Valid @RequestBody GenerateReportRequest request) {
        return reportApplicationService.generateMonthlyReport(request.month());
    }

    /**
     * 查询当前待人工复核的异常记录。
     *
     * @return 待复核记录列表
     */
    @GetMapping("/review-items")
    public List<ReviewItem> listReviewItems() {
        return reviewApplicationService.listPending();
    }

    /**
     * 应用人工复核结果。
     *
     * <p>该接口会更新复核项状态，并同步更新关联消费记录的统计决策。</p>
     *
     * @param id      复核项 ID
     * @param request 复核动作请求
     * @return 复核应用结果
     */
    @PostMapping("/review-items/{id}/apply")
    public ReviewApplyResult applyReview(@PathVariable long id, @Valid @RequestBody ReviewApplyRequest request) {
        return reviewApplicationService.apply(id, request.action(), request.note());
    }

    public static class ImportFileRequest {
        /**
         * 本地订单文件路径
         */
        @NotBlank
        private String filePath;

        public ImportFileRequest() {
        }

        public String filePath() {
            return filePath;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }

    public static class ComparePriceRequest {
        /**
         * 原始商品名称
         */
        @NotBlank
        private String productName;
        /**
         * 当前总价
         */
        @Positive
        private double price;
        /**
         * 当前商品数量
         */
        @Positive
        private double quantity;
        /**
         * 数量单位
         */
        @NotBlank
        private String unit;

        public ComparePriceRequest() {
        }

        public String productName() {
            return productName;
        }

        public double price() {
            return price;
        }

        public double quantity() {
            return quantity;
        }

        public String unit() {
            return unit;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }

    public static class GenerateReportRequest {
        /**
         * 报告月份，格式为 yyyy-MM
         */
        @NotBlank
        private String month;

        public GenerateReportRequest() {
        }

        public String month() {
            return month;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }
    }

    public static class ReviewApplyRequest {
        /**
         * 复核动作，取值 include 或 exclude
         */
        @NotBlank
        private String action;
        /**
         * 复核备注
         */
        private String note;

        public ReviewApplyRequest() {
        }

        public String action() {
            return action;
        }

        public String note() {
            return note;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }

    /**
     * 将业务参数错误转换为 400 响应，避免工具调用方收到不明确的 500 错误。
     *
     * @param exception 参数或状态异常
     * @return 错误信息
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public Map<String, String> handleBadRequest(RuntimeException exception) {
        return Map.of("error", exception.getMessage());
    }
}
