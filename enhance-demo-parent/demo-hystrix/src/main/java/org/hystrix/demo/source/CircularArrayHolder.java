package org.hystrix.demo.source;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * 环形数组管理器，通过该holder来方便的操作环形数组
 * 实现原理参考：{@link HystrixRollingNumber.BucketCircularArray}
 *
 * @author wenpanfeng 2022/07/30 11:12
 */
public class CircularArrayHolder implements Iterable<Bucket> {

    /**
     * 持有一个环形数组的引用，以便于可以通过该引用方便的访问环形数组
     */
    private final AtomicReference<CircularArray> circularArray;
    /**
     * 固定值，一旦创建就不会改变, 预留一个空间，作为后续向环形数组增减和删除的支持，
     * 长度始终为：桶的数量 + 1，比如：如果环形数组里有10个桶，那么该值就是11
     * <p>
     */
    private final int dataLength;
    /**
     * 桶的数量
     */
    private final int numBuckets;

    /**
     * 构造函数
     */
    public CircularArrayHolder(int size) {
        // + 1 as extra room for the add/remove;
        AtomicReferenceArray<Bucket> buckets = new AtomicReferenceArray<>(size + 1);
        // state持有该环形数组的引用
        circularArray = new AtomicReference<>(new CircularArray(buckets, 0, 0));
        dataLength = buckets.length();
        numBuckets = size;
    }

    /**
     * 清除环形数组里的所有元素（线程安全）
     */
    public void clear() {
        while (true) {
            // 获取到环形数组的引用
            CircularArray currentCircularArray = circularArray.get();
            // 调用环形数组的clear方法，此时会返回环形数组新的引用
            CircularArray newCircularArray = currentCircularArray.clear();
            // 使用新的引用替换旧的引用
            if (circularArray.compareAndSet(currentCircularArray, newCircularArray)) {
                // 如果cas替换成功则退出，不然则进行下一次尝试
                return;
            }
        }
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
     * 往环形数组尾部添加一个bucket（非线程安全）
     */
    public void addLast(Bucket bucket) {
        // 获取到环形数组的引用
        CircularArray currentCircularArray = circularArray.get();
        // 将元素添加到环形数组里，添加成功后会返回一个新的环形数组的引用
        CircularArray newCircularArray = currentCircularArray.addBucket(bucket);
        // 将circularArray重新指向最新的环形数组
        circularArray.compareAndSet(currentCircularArray, newCircularArray);
    }

    /**
     * 获取环形数组最后一个元素
     */
    public Bucket getLast() {
        return peekLast();
    }

    /**
     * 获取环形数组里的元素个数
     */
    public int size() {
        // 大小也可以每次计算为: return (tail + data.length() - head) % data.length();
        return circularArray.get().size;
    }

    /**
     * 获取环形数组最后一个元素
     */
    public Bucket peekLast() {
        return circularArray.get().tail();
    }

    /**
     * 获取环形数组中所有的元素
     */
    private Bucket[] getArray() {
        return circularArray.get().getArray();
    }

    /**
     * 私有内部类，不允许外部直接访问（环形数组实现类，适用于写多读少的场景）
     */
    private class CircularArray {

        /**
         * 环形数组，数组里是一个个的桶，桶内需要放什么数据可自己决定
         */
        protected final AtomicReferenceArray<Bucket> data;
        /**
         * 环形数组的大小（也就是环形数组中现有元素的个数）
         */
        protected final int size;
        /**
         * 数组头节点下标索引
         */
        protected final int tail;
        /**
         * 数组尾节点下标索引
         */
        protected final int head;

        /**
         * 构造方法
         */
        public CircularArray(AtomicReferenceArray<Bucket> data, int head, int tail) {
            this.data = data;
            this.head = head;
            this.tail = tail;
            // 计算size
            if (head == 0 && tail == 0) {
                size = 0;
            } else {
                size = (tail + dataLength - head) % dataLength;
            }
        }

        /**
         * 获取环形数组尾部元素
         */
        public Bucket tail() {
            // 桶内还没有元素时，返回null
            if (size == 0) {
                return null;
            } else {
                // we want to get the last item, so size()-1
                return data.get(convert(size - 1));
            }
        }

        /**
         * convert() 方法采用逻辑索引（好像 head 始终为 0）并计算 elementData 内的索引
         */
        private int convert(int index) {
            return (index + head) % dataLength;
        }

        /**
         * 获取环形数组中所有的元素
         */
        protected Bucket[] getArray() {
            List<Bucket> array = new ArrayList<>();
            // 依次获取环形数组内部所有元素并加入到新的list
            for (int i = 0; i < size; i++) {
                array.add(data.get(convert(i)));
            }
            return array.toArray(new Bucket[0]);
        }

        /**
         * 增加一个元素到环形数组尾部
         */
        private CircularArray incrementTail() {
            // 如果已经到达环形数组最大长度，则头尾指针一起移动
            if (size == numBuckets) {
                return new CircularArray(data, (head + 1) % dataLength, (tail + 1) % dataLength);
            } else {
                // 如果还没有到达环形数组最大容量，则increment only tail
                return new CircularArray(data, head, (tail + 1) % dataLength);
            }
        }

        /**
         * 清除环形数组，其实也就是新建一个CircularArray然后将头尾指针都指向0位置
         *
         * @return CircularArray
         * @author wenpanfeng 2022/7/28 21:31
         */
        public CircularArray clear() {
            return new CircularArray(new AtomicReferenceArray<>(dataLength), 0, 0);
        }

        /**
         * 添加一个桶到环形数组里
         */
        public CircularArray addBucket(Bucket bucket) {
            // 设置尾结点位置的值为bucket
            data.set(tail, bucket);
            // 尾部移动一个
            return incrementTail();
        }
    }

}