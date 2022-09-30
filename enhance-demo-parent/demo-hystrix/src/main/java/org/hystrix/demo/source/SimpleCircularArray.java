package org.hystrix.demo.source;

import java.util.*;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * 简单的环形数组实现，非线程安全，若需保证线程安全只需要在使用时对addLast和clear方法加锁即可
 *
 * @author wenpanfeng 2022/07/30 13:34
 */
public class SimpleCircularArray implements Iterable<Bucket> {

    /**
     * 环形数组，数组里是一个个的桶，桶内需要放什么数据可自己决定
     */
    private final AtomicReferenceArray<Bucket> circularArray;
    /**
     * 头指针
     */
    private int head;
    /**
     * 尾指针
     */
    private int tail;
    /**
     * 当前数组内元素的个数
     */
    private int size;
    /**
     * 环形数组容量
     */
    private final int capacity;

    public SimpleCircularArray(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity can not be less than 1");
        }
        circularArray = new AtomicReferenceArray<>(capacity);
        head = 0;
        tail = 0;
        this.capacity = capacity;
    }

    public SimpleCircularArray(AtomicReferenceArray<Bucket> circularArray) {
        if (Objects.isNull(circularArray)) {
            throw new IllegalArgumentException("circularArray can not be null.");
        }
        if (circularArray.length() < 1) {
            throw new IllegalArgumentException("capacity can not be less than 1");
        }
        this.circularArray = circularArray;
        head = 0;
        tail = 0;
        capacity = circularArray.length();
    }

    public void addLast(Bucket bucket) {
        // 已经到达最后一个
        if (size == capacity) {
            if (head == capacity - 1) {
                head = 0;
            } else {
                head = head + 1;
            }
            if (tail == capacity) {
                circularArray.set(0, bucket);
                tail = 1;
            } else {
                circularArray.set(tail, bucket);
                tail = tail + 1;
            }
        } else {
            // 环形数组中元素个数还未达到capacity，则只移动tail
            circularArray.set(tail, bucket);
            tail = tail + 1;
            size++;
        }
    }

    /**
     * 清除环形数组
     */
    public void clear() {
        size = 0;
        head = 0;
        tail = 0;
    }

    /**
     * 在内部数组的副本上返回一个迭代器，以便迭代器不会因同时添加删除的存储桶而失败。
     */
    @Override
    public Iterator<Bucket> iterator() {
        // 获取环形数组里的所有元素，这里获取到的是环形数组里的元素的副本
        return Collections.unmodifiableList(Arrays.asList(getArray())).iterator();
    }

    /**
     * 获取环形数组中所有元素
     */
    protected Bucket[] getArray() {
        List<Bucket> array = new ArrayList<>();
        // 依次获取环形数组内部所有元素并加入到新的list
        for (int i = 0; i < size; i++) {
            array.add(circularArray.get(convert(i)));
        }
        return array.toArray(new Bucket[0]);
    }

    /**
     * convert() 方法采用逻辑索引（好像 head 始终为 0）并计算 elementData 内的索引
     */
    private int convert(int index) {
        return (index + head) % (capacity);
    }

}