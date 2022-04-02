package org.basis.enhance.datasource.annotation;

import java.util.concurrent.TimeUnit;

/**
 * @author Mr_wenpan@163.com 2022/03/02 15:32
 */
public class Test {

    public static boolean flag = false;

    public static void test2() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 修改flag
            flag = true;
        });
        t1.start();
        // 主线程在这里join等待t1线程执行
        t1.join();
        // join结束后，t1线程对于flag变量的修改对于主线程是可见的，这里会输出true
        System.out.println(flag);
    }

    public static void main(String[] args) throws InterruptedException {
        test2();
        System.out.println("hello world.");
//        int[] arr = new int[]{-1, 1, 2, 3, 4, 4, 4, 4, 5, 6, 17, 18, 19, 19, 110};
        int[] arr = {5, 7, 7, 8, 8, 10};
        int search = search(arr, 8);
        int search2 = search2(arr, 8);
        System.out.println(search);
        System.out.println(search2);

        int[] ints = searchRange(arr, 8);
        System.out.println(ints[0] + "," + ints[1]);
    }

    public String testHello(String name) {
        return name;
    }

    public static int[] searchRange(int[] nums, int target) {
        if (nums == null || nums.length < 1) {
            return new int[]{-1, -1};
        }
        int left = search(nums, target, true);
        int right = search(nums, target, false);

        return new int[]{left, right};
    }

    // 搜索第一个等于target的数字的下标或最后一个等于target的下标
    public static int search(int[] nums, int target, boolean flag) {
        int l = 0;
        int r = nums.length - 1;
        int res = -1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] == target) {
                if (flag) {
                    // 遇到相等时，移动右边界
                    res = mid;
                    r = mid - 1;
                } else {
                    // 遇到相等时，移动右边界
                    res = mid;
                    l = mid + 1;
                }
            } else if (nums[mid] > target) {
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }

        return res;
    }

    public static int search(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return -1;
        }
        int l = 0;
        int r = nums.length - 1;
        int temp = -1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] == target) {
                // 先不返回
                r = mid - 1;
                temp = mid;
            } else if (nums[mid] > target) {
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }

        return temp;
    }

    public static int search2(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return -1;
        }
        int l = 0;
        int r = nums.length - 1;
        int temp = -1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            System.out.println("l = " + l);
            System.out.println("m = " + mid);
            System.out.println("r = " + r);
            if (nums[mid] == target) {
                // 先不返回
                l = mid + 1;
                temp = mid;
            } else if (nums[mid] > target) {
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }

        return temp;
    }

}
