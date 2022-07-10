package org.enhance.core.base.intercepter;

import org.springframework.http.client.ClientHttpRequestInterceptor;

/**
 * 实现该接口为 RestTemplate 加入 Header <br/>
 * <pre>
 *      HttpHeaders headers = request.getHeaders();
 *      headers.add(key, value);
 *      execution.execute(request, body)
 * <pre/>
 *
 * @author wenpanfeng
 * @see ClientHttpRequestInterceptor
 */
public interface EventRequestInterceptor extends ClientHttpRequestInterceptor {

}