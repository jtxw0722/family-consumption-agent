package com.jtxw.familyagent.infrastructure.persistence;

import com.jtxw.familyagent.common.ClockUtils;
import com.jtxw.familyagent.domain.model.ReviewItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @Author: jtxw
 * @Date: 2026/05/11/15:58
 * @Description: 复核事项仓储，负责创建和查询待人工确认的异常记录。
 */
@Repository
public class ReviewItemRepository {
    private final JdbcTemplate jdbcTemplate;

    public ReviewItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 创建待人工复核项。
     *
     * @param recordId      关联消费记录 ID
     * @param reasonCode    复核原因编码
     * @param reasonMessage 复核原因说明
     */
    public void create(Long recordId, String reasonCode, String reasonMessage) {
        jdbcTemplate.update("INSERT INTO review_items(record_id, reason_code, reason_message, status, created_at) VALUES (?, ?, ?, ?, ?)",
                recordId, reasonCode, reasonMessage, "pending", ClockUtils.nowText());
    }

    /**
     * 查询所有待处理的复核项。
     *
     * @return pending 状态的复核项列表
     */
    public List<ReviewItem> listPending() {
        return jdbcTemplate.query("SELECT * FROM review_items WHERE status='pending' ORDER BY id", rowMapper());
    }

    /**
     * 根据 ID 查询复核项。
     *
     * @param id 复核项 ID
     * @return 复核项，不存在时为空
     */
    public Optional<ReviewItem> findById(long id) {
        List<ReviewItem> items = jdbcTemplate.query("SELECT * FROM review_items WHERE id = ?", rowMapper(), id);
        return items.stream().findFirst();
    }

    /**
     * 将待复核项标记为已处理。
     *
     * <p>该方法只更新 pending 状态的复核项，用于避免重复应用人工复核结果。</p>
     *
     * @param id             复核项 ID
     * @param reviewDecision 人工复核动作
     * @param reviewNote     人工复核备注
     * @return 更新记录数
     */
    public int resolve(long id, String reviewDecision, String reviewNote) {
        return jdbcTemplate.update("""
                UPDATE review_items
                SET status = ?, review_decision = ?, review_note = ?, resolved_at = ?
                WHERE id = ? AND status = 'pending'
                """, "resolved", reviewDecision, reviewNote, ClockUtils.nowText(), id);
    }

    /**
     * 统计当前待处理复核项数量。
     *
     * @return pending 状态复核项数量
     */
    public int countPending() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM review_items WHERE status='pending'", Integer.class);
        return count == null ? 0 : count;
    }

    private RowMapper<ReviewItem> rowMapper() {
        return (rs, rowNum) -> new ReviewItem(
                rs.getLong("id"), rs.getLong("record_id"), rs.getString("reason_code"),
                rs.getString("reason_message"), rs.getString("status"), rs.getString("review_decision"),
                rs.getString("review_note"), rs.getString("created_at"),
                rs.getString("resolved_at")
        );
    }
}
