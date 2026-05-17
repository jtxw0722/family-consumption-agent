package com.jtxw.familyagent.infrastructure.persistence;

import com.jtxw.familyagent.common.ClockUtils;
import com.jtxw.familyagent.domain.model.PurchaseRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: jtxw
 * @Date: 2026/05/11/15:34
 * @Description: 消费记录仓储，负责订单明细的写入和价格统计查询。
 */
@Repository
public class PurchaseRecordRepository {
    private final JdbcTemplate jdbcTemplate;

    public PurchaseRecordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 保存标准化后的消费记录。
     *
     * @param record 消费记录
     * @return 新增记录 ID
     */
    public long save(PurchaseRecord record) {
        jdbcTemplate.update("""
                        INSERT INTO purchase_records(
                            batch_id, order_time, platform, owner, product_name, normalized_name, sku,
                            category, sub_category, quantity, unit, total_amount, unit_price, currency,
                            decision, is_duplicate, dedupe_status, source_file, created_at
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                record.batchId(), record.orderTime(), record.platform(), record.owner(), record.productName(),
                record.normalizedName(), record.sku(), record.category(), record.subCategory(), record.quantity(),
                record.unit(), record.totalAmount(), record.unitPrice(), record.currency(), record.decision(),
                record.duplicate() ? 1 : 0, record.dedupeStatus(), record.sourceFile(), ClockUtils.nowText());
        return jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
    }

    /**
     * 查询指定商品的历史有效单价样本。
     *
     * <p>默认只返回正式统计口径内的记录：
     * decision = include，is_duplicate = 0，dedupe_status = unique。</p>
     *
     * @param normalizedName 归一化商品名称
     * @return 历史单位价格列表
     */
    public List<Double> listUnitPrices(String normalizedName) {
        return jdbcTemplate.queryForList("""
                SELECT unit_price FROM purchase_records
                WHERE normalized_name = ?
                  AND decision = 'include'
                  AND is_duplicate = 0
                  AND dedupe_status = 'unique'
                  AND unit_price IS NOT NULL
                  AND total_amount > 0
                """, Double.class, normalizedName);
    }

    /**
     * 更新消费记录的统计决策。
     *
     * @param id       消费记录 ID
     * @param decision 统计决策，通常为 include 或 exclude
     * @return 更新记录数
     */
    public int updateDecision(long id, String decision) {
        return jdbcTemplate.update("UPDATE purchase_records SET decision = ? WHERE id = ?", decision, id);
    }

    /**
     * 查询指定月份内纳入正式统计的消费记录。
     *
     * <p>默认只返回 decision = include、is_duplicate = 0、dedupe_status = unique，
     * 且实付金额大于 0 的记录。</p>
     *
     * @param month 月份，格式为 yyyy-MM
     * @return 月度有效消费记录列表
     */
    public List<PurchaseRecord> listIncludedByMonth(String month) {
        return jdbcTemplate.query("""
                SELECT * FROM purchase_records
                WHERE substr(order_time, 1, 7) = ?
                  AND decision = 'include'
                  AND is_duplicate = 0
                  AND dedupe_status = 'unique'
                  AND total_amount > 0
                ORDER BY order_time
                """, rowMapper(), month);
    }

    private RowMapper<PurchaseRecord> rowMapper() {
        return (rs, rowNum) -> new PurchaseRecord(
                rs.getLong("id"), rs.getLong("batch_id"), rs.getString("order_time"),
                rs.getString("platform"), rs.getString("owner"), rs.getString("product_name"),
                rs.getString("normalized_name"), rs.getString("sku"), rs.getString("category"),
                rs.getString("sub_category"), rs.getDouble("quantity"), rs.getString("unit"),
                rs.getDouble("total_amount"), rs.getDouble("unit_price"), rs.getString("currency"),
                rs.getString("decision"), rs.getInt("is_duplicate") == 1, rs.getString("dedupe_status"),
                rs.getString("source_file"), rs.getString("created_at")
        );
    }
}
