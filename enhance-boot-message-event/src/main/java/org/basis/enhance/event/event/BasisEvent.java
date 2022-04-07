package org.basis.enhance.event.event;

/**
 * 基础事件
 *
 * @author Mr_wenpan@163.com 2022/04/01 17:34
 */
public class BasisEvent implements java.io.Serializable {

    private static final long serialVersionUID = 5511235349620653480L;

    /**
     * System time when the event happened
     */
    private final long timestamp;

    public BasisEvent() {
        timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
}
