package org.clean.code.other;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CompletableFuture测试
 *
 * @author wenpanfeng 2022/06/28 16:35
 */
public class CompletableFutureTest {

    public static void main(String[] args) throws Exception {

        test02();
        System.out.println("over....");
    }

    public static void test02() throws ExecutionException, InterruptedException {
        // 无返回值，不能抛出异常，即使执行方法的时候出现了异常，外部也无法感知
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            int i = 1 / 0;
            System.out.println("xxxxx");
        });
        // 可以通过get获取异常
        Void unused = future1.get();
        System.out.println("unused = " + unused);

        // 可以获取任务返回值也可以获取异常
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("====>>>>>>supplyAsync");
            return "10";
        });
        future2.get();
    }

    public void test04() throws Exception {
        // 创建两个任务
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            return "10";
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            return "20";
        });
        // 当future1出现异常时回调该方法
        CompletableFuture<String> exceptionFuture = future1.exceptionally((exception) -> {
            System.out.println("出现异常啦，异常信息：" + exception.getMessage());
            // 出现异常了就返回100
            return "100";
        });
        // 获取异常后的结果
        String exceptionResult = exceptionFuture.get();
        System.out.println("--->exceptionResult ="+ exceptionResult);


    }

    public static void test03() throws Exception{
        // 创建两个任务
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            return "10";
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            return "20";
        });

        // thenRun 不能接受上一步的结果，也不能返回自己的结果
        future1.thenRun(()->{
            System.out.println("--->thenRun，上一步执行完毕后执行thenRun，thenRun不能获取上一步的结果，也不能返回自己的结果。");
        });
        // thenAccept 方法，接受future1的返回值，但不能返回自己的值
        future1.thenAccept(result->{
            System.out.println("--->thenAccept 接受上一步的返回值，但不能返回自己的值。");
        });
        // thenApply方法 接受future1的返回值，并返回自己的值
        future1.thenApply((result)->{
            // 返回自己的结果
            return result + "---thenApply";
        });
        // thenCombine方法，接受future1和future2的返回值，并且返回自己的结果
        future1.thenCombine(future2,(f1,f2)->{
            System.out.println("--->thenCombine 整合future1和future2的结果，并返回自己的结果。");
            return f1 + "===" + f2;
        });
        // handleAsync 可以拿到future1的返回值，并且可以返回自己的返回值，并且可以拿到future1执行中出现的异常信息
        future1.handleAsync((result,exception)->{
            if(exception != null){
                System.out.println("future1执行出异常啦。。。。");
            }
            return "handleAsync";
        });

    }
    public void test01() throws Exception{
        ExecutorService executor = Executors.newFixedThreadPool(10);
        // 5.测试CompletableFuture
        // 5.1.提交任务异步执行(supplyAsync)
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "测试使用", executor);
        System.out.println(future1.get());
        // 5.2.获取上一步结果并链式异步调用(thenApplyAsync)【 能获取上一步结果 + 有返回值】
        CompletableFuture<String> future2 = future1.thenApplyAsync(s -> s + " 链式调用", executor);
        System.out.println(future2.get());
        // 5.3.获取上一步执行结果并获取异常信息(whenCompleteAsync)
        CompletableFuture<String> future3 = future2.whenCompleteAsync((result, exception) ->
                System.out.println("结果是：" + result + "----异常是：" + exception));
        // 5.4.获取上一步异常，如果出现异常返回默认值，不出现异常保持原值(exceptionally)
        CompletableFuture<Integer> future4 = future3.thenApplyAsync((s -> 1 / 0), executor);
        CompletableFuture<Integer> future5 = future4.exceptionally(exception -> {
            // 出现异常，使用默认返回值
            System.out.println("出现异常：" + exception);
            return 10;
        });

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
    }
}