package com.jtxw.familyagent.domain.policy;

import org.springframework.stereotype.Component;

/**
 * @Author: jtxw
 * @Date: 2026/05/11/10:38
 * @Description: 商品名称归一化规则，用于降低同类消耗品名称差异。
 */
@Component
public class ProductNormalizer {
    /**
     * 将原始商品名称归一化为稳定的统计名称。
     *
     * <p>当前版本使用确定性关键词规则处理常见家庭消耗品，避免同类商品因规格或标题差异被拆成多个统计对象。</p>
     *
     * @param productName 原始商品名称
     * @return 归一化商品名称
     */
    public String normalize(String productName) {
        if (productName == null || productName.isBlank()) {
            return "未命名商品";
        }
        String name = productName.trim();
        if (name.contains("猫砂")) {
            return "猫砂";
        }
        if (name.contains("猫粮")) {
            return "猫粮";
        }
        if (name.contains("纸巾") || name.contains("抽纸")) {
            return "纸巾";
        }
        if (name.contains("洗衣液")) {
            return "洗衣液";
        }
        return name;
    }
}
