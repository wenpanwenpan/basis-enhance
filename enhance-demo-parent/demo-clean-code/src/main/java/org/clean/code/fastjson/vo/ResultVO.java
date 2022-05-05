package org.clean.code.fastjson.vo;

import lombok.Data;

/**
 * 返回体
 *
 * @author Mr_wenpan@163.com 2022/05/04 16:46
 */
@Data
public class ResultVO<T> {
    private boolean success = true;
    private T body;

    public static <T> ResultVO<T> buildResult(T t) {
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setBody(t);
        return resultVO;
    }
}
