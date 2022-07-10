package org.enhance.core.base.vo;

import java.util.HashMap;

/**
 * API调用参数封装
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/14 15:35
 */
public class ApiParam extends HashMap<String, Object> {

    private static final long serialVersionUID = 3885646838813320581L;

    public ApiParam() {
    }

    public ApiParam(int initialCapacity) {
        super(initialCapacity);
    }

}