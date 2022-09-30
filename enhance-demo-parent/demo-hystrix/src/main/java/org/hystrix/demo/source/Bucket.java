package org.hystrix.demo.source;

/**
 * 桶
 *
 * @author wenpanfeng 2022/07/30 11:17
 */
public class Bucket {
    /**
     * 桶名称
     */
    String name;
    
    public Bucket() {
    }

    public Bucket(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}