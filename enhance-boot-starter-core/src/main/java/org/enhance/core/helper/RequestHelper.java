package org.enhance.core.helper;

import org.enhance.core.base.vo.ApiParam;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * 发起Http请求
 *
 * @author wenpanfeng 2021-01-01 09:22:45
 */
public interface RequestHelper {

    /**
     * 发起Http请求，支持GET、POST、PUT、DELETE
     *
     * @param url    URL地址
     * @param method {@link HttpMethod} 请求方法，支持GET、POST、PUT、DELETE<br/>
     *
     *               <pre>
     *                                                                GET 参数拼接在地址后面，如：http://examples/{id}/rules?name={name}&enableFlag={enableFlag}<br/>
     *                                                                POST/PUT/DELETE 统一使用"application/json"格式发起请求，接收请求的API使用 "@RequestBody" 接收参数
     *                                                                。也支持在地址后拼接参数，使用@RequestParam接收<br/>
     *
     *                                                                <pre/>
     *                                                         @param apiParam {@link ApiParam} Api参数
     *                                                         @return 请求结果
     */
    @SuppressWarnings("rawtypes")
    ResponseEntity request(String url, HttpMethod method, ApiParam apiParam);

}