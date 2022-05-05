package org.clean.code.jackson.dto;

import lombok.Data;

/**
 * 返回体
 *
 * @author Mr_wenpan@163.com 2022/05/04 16:46
 */
@Data
public class ResultDTO<T> {
    private boolean success = true;
    private T body;

    public static <T> ResultDTO<T> buildResult(T t) {
        ResultDTO<T> resultVO = new ResultDTO<>();
        resultVO.setBody(t);
        return resultVO;
    }
}
