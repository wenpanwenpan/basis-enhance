package org.hystrix.demo.source;

/**
 * @author wenpanfeng 2022/07/29 11:57
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("=============================测试CircularArrayHolder=============================");
        CircularArrayHolder holder = new CircularArrayHolder(10);

        for (int i = 0; i < 20; i++) {
            holder.addLast(new Bucket(String.valueOf(i)));
        }

        for (Bucket next : holder) {
            System.out.println(next.getName());
        }

        holder.clear();
        System.out.println("==================================================");

        for (Bucket next : holder) {
            System.out.println(next.getName());
        }

        System.out.println("=============================测试SimpleCircularArray=============================");

        SimpleCircularArray simpleCircularArray = new SimpleCircularArray(4);

        for (int i = 0; i < 10; i++) {
            simpleCircularArray.addLast(new Bucket(String.valueOf(i)));
        }

        for (Bucket bucket : simpleCircularArray) {
            System.out.println(bucket.getName());
        }

        simpleCircularArray.clear();
        System.out.println("======================================");

        for (int i = 0; i < 5; i++) {
            simpleCircularArray.addLast(new Bucket(String.valueOf(i)));
        }

        for (Bucket bucket : simpleCircularArray) {
            System.out.println(bucket.getName());
        }

        simpleCircularArray.clear();
        System.out.println("======================================");

        for (int i = 0; i < 3; i++) {
            simpleCircularArray.addLast(new Bucket(String.valueOf(i)));
        }

        for (Bucket bucket : simpleCircularArray) {
            System.out.println(bucket.getName());
        }

        simpleCircularArray.clear();
        System.out.println("======================================");

        for (int i = 0; i < 500; i++) {
            simpleCircularArray.addLast(new Bucket(String.valueOf(i)));
        }

        for (Bucket bucket : simpleCircularArray) {
            System.out.println(bucket.getName());
        }
    }
}