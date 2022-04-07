package org.basis.enhance.event.config;

import org.springframework.context.annotation.ComponentScan;

/**
 * 扫描其他bean
 *
 * @author Mr_wenpan@163.com 2022/04/02 12:51
 */
@ComponentScan(basePackages = {"org.basis.enhance.event.consumer"})
public class ScanOtherClass {

}
