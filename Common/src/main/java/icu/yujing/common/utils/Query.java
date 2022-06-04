package icu.yujing.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
public class Query {

    /**
     *
     * @param orderField
     * @param orderType 0:降序 1:升序
     * @param allowedOrderFields
     * @param wrapper
     */
    public static void order(String orderField, Integer orderType, List<String> allowedOrderFields, QueryWrapper wrapper) {
        for (String allowedOrderField : allowedOrderFields) {
            if (allowedOrderField.equalsIgnoreCase(orderField)) {
                if (orderType == 0) {
                    wrapper.orderByDesc(allowedOrderField);
                } else {
                    wrapper.orderByAsc(allowedOrderField);
                }
                break;
            }
        }
    }

    public static boolean orderIsValid(String orderField, Integer orderType, List<String> allowedOrderFields, Map<String, List<Integer>> allowedOrderTypes) {
        for (String allowedOrderField : allowedOrderFields) {
            if (allowedOrderField.equals(orderField)) {
                List<Integer> orderTypes = allowedOrderTypes.get(allowedOrderField);
                for (Integer allowedOrderType : orderTypes) {
                    if (Objects.equals(allowedOrderType, orderType)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }
}
