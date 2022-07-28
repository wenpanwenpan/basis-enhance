package org.clean.code.other;

import java.util.concurrent.*;

/**
 * 多线程测试类
 */
public class ThreadTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1.测试callable，配合FutureTask（本质也是runnable）
        // 【new FutureTask<>(new MyRunnable(), result)】可以接受runnable的返回值
        FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable());
        new Thread(futureTask).start();
        //System.out.println(futureTask.get());// 阻塞等待异步执行结束获取结果

        // 2.创建一个固定线程数的线程池，执行异步任务
        ExecutorService pool = Executors.newFixedThreadPool(10);// 使用了线程池，所以当前程序不会结束
        Future<?> future = pool.submit(new MyCallable());
        System.out.println("future: " + future.get());

        // 3.创建一个原生线程池
        new ThreadPoolExecutor(5,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());

        // 4.创建常用的4种线程池
        ExecutorService executor = Executors.newFixedThreadPool(10);// 使用了线程池，所以当前程序不会结束
        Executors.newCachedThreadPool();
        Executors.newScheduledThreadPool(10);
        Executors.newSingleThreadExecutor();

        // 5.测试CompletableFuture
        // 5.1.提交任务异步执行(supplyAsync)
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "测试使用", executor);
        System.out.println(future1.get());
        // 5.2.获取上一步结果并链式异步调用(thenApplyAsync)【 能获取上一步结果 + 有返回值】
        CompletableFuture<String> future2 = future1.thenApplyAsync(s -> s + " 链式调用", executor);// 参数s是上一步的返回值
        System.out.println(future2.get());
        // 5.3.获取上一步执行结果并获取异常信息(whenCompleteAsync)
        CompletableFuture<String> future3 = future2.whenCompleteAsync((result, exception) -> System.out.println("结果是：" + result + "----异常是：" + exception));
        // 5.4.获取上一步异常，如果出现异常返回默认值，不出现异常保持原值(exceptionally)
        CompletableFuture<Integer> future4 = future3.thenApplyAsync((s -> 1 / 0), executor);
        CompletableFuture<Integer> future5 = future4.exceptionally(exception -> {
            System.out.println("出现异常：" + exception);
            return 10;
        });// 出现异常，使用默认返回值
        System.out.println("默认值：" + future5.get());
        // 5.5.方法执行完成后的处理
        CompletableFuture<Integer> future6 = future3.thenApplyAsync((s -> 1 / 0), executor).handle((result, exception) -> {
            if (exception == null) {
                return result;
            }
            System.out.println("handle处理异常：" + exception);
            return 1;
        });
        System.out.println("handle处理返回结果：" + future6.get());

//        // 5.6.1.二者都要完成，组合【不获取前两个任务返回值，且自己无返回值】
//        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务1执行");
//            return 10 / 2;
//        }, executor);
//        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务2执行");
//            return "hello";
//        }, executor);
//        CompletableFuture<Void> future03 = future01.runAfterBothAsync(future02, () -> {
//            System.out.println("任务3执行");
//        }, executor);

//        // 5.6.2.二者都要完成，组合【获取前两个任务返回值，自己无返回值】
//        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务1执行");
//            return 10 / 2;
//        }, executor);
//        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务2执行");
//            return "hello";
//        }, executor);
//        CompletableFuture<Void> future03 = future01.thenAcceptBothAsync(future02,
//                (result1, result2) -> {
//                    System.out.println("任务3执行");
//                    System.out.println("任务1返回值：" + result1);
//                    System.out.println("任务2返回值：" + result2);
//                }, executor);

        // 5.6.3.二者都要完成，组合【获取前两个任务返回值，自己有返回值】
        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1执行");
            return 10 / 2;
        }, executor);
        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2执行");
            return "hello";
        }, executor);
        CompletableFuture<String> future03 = future01.thenCombineAsync(future02,
                (result1, result2) -> {
                    System.out.println("任务3执行");
                    System.out.println("任务1返回值：" + result1);
                    System.out.println("任务2返回值：" + result2);
                    return "任务3返回值";
                }, executor);
        System.out.println(future03.get());


        CompletableFuture<Void> allOf = CompletableFuture.allOf(future01, future02, future03);
        allOf.get();// 阻塞等待所有任务完成

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(future01, future02, future03);
        anyOf.get();// 阻塞等待任一任务完成，返回值是执行成功的任务返回值
    }

    /**
     * 1.测试callable
     */
    public static class MyCallable implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            System.out.println("任务执行");
            return 1;

        }
    }


}
