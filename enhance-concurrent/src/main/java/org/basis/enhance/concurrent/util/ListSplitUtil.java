package org.basis.enhance.concurrent.util;

import java.util.ArrayList;
import java.util.List;

/**
 * list集合拆分工具类
 *
 * @author Mr_wenpan@163.com 2020/2/4 1:34 下午
 */
public class ListSplitUtil {

    /**
     * 按指定大小，分隔集合List，将集合按指定Size分为n个部分
     *
     * @param sourceList  待拆分的集合
     * @param perListSize 每个集合的数据量
     * @return 子List集合
     */
    public static <T> List<List<T>> splitList(List<T> sourceList, int perListSize) {
        if (sourceList == null || sourceList.size() == 0 || perListSize < 1) {
            return null;
        }

        List<List<T>> result = new ArrayList<>();
        int size = sourceList.size();
        int count = (size + perListSize - 1) / perListSize;

        for (int i = 0; i < count; i++) {
            List<T> subList = sourceList.subList(i * perListSize, (Math.min((i + 1) * perListSize, size)));
            result.add(subList);
        }
        return result;
    }

    /**
     * 将一个list均分成n个子list, 主要通过偏移量来实现
     *
     * @param source 待拆分的集合
     * @param n      要均分的集合个数
     * @return 子List集合
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<>();
        // (先计算出余数)
        int remaider = source.size() % n;
        // 然后是商
        int number = source.size() / n;
        int offset = 0;
        // 偏移量
        for (int i = 0; i < n; i++) {
            List<T> value;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

}