
package com.workoss.boot.util.concurrent;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步执行运行时
 *
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */

public class AsyncRuntime {

    private static final Logger log = LoggerFactory.getLogger(AsyncRuntime.class);


    /**
     * callback业务线程池（callback+async）
     */
    private static volatile ThreadPoolExecutor asyncThreadPool;

    /**
     * 得到callback用的线程池 默认开始创建
     *
     * @return callback用的线程池
     */
    public static ThreadPoolExecutor getAsyncThreadPool() {
        return getAsyncThreadPool(true);
    }

    /**
     * 得到callback用的线程池
     *
     * @param build 没有时是否构建
     * @return callback用的线程池
     */
    public static ThreadPoolExecutor getAsyncThreadPool(boolean build) {
        if (asyncThreadPool == null && build) {
            synchronized (AsyncRuntime.class) {
                if (asyncThreadPool == null && build) {
                    // 一些系统参数，可以从配置或者注册中心获取。
                    int coresize = 10;
                    int maxsize = 200;
                    int queuesize = 256;
                    int keepAliveTime = 60000;

                    BlockingQueue<Runnable> queue = ThreadPoolUtils.buildQueue(queuesize);
                    NamedThreadFactory threadFactory = new NamedThreadFactory("EVENT-CB", true);

                    RejectedExecutionHandler handler = new RejectedExecutionHandler() {
                        private int i = 1;

                        @Override
                        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                            if (i++ % 7 == 0) {
                                i = 1;
                                if (log.isWarnEnabled()) {
                                    log.warn("Task:{} has been reject because of threadPool exhausted!" +
                                                    " pool:{}, active:{}, queue:{}, taskcnt: {}", r,
                                            executor.getPoolSize(),
                                            executor.getActiveCount(),
                                            executor.getQueue().size(),
                                            executor.getTaskCount());
                                }
                            }
                            throw new RejectedExecutionException("Callback handler thread pool has bean exhausted");
                        }
                    };
                    asyncThreadPool = ThreadPoolUtils.newCachedThreadPool(
                            coresize, maxsize, keepAliveTime, queue, threadFactory, handler);
                }
            }
        }
        return asyncThreadPool;
    }


    static class NamedThreadFactory implements ThreadFactory {
        /**
         * 系统全局线程池计数器
         */
        private static final AtomicInteger POOL_COUNT = new AtomicInteger();

        /**
         * 当前线程池计数器
         */
        final AtomicInteger threadCount = new AtomicInteger(1);
        /**
         * 线程组
         */
        private final ThreadGroup group;
        /**
         * 线程名前缀
         */
        private final String namePrefix;
        /**
         * 是否守护线程，true的话随主线程退出而退出，false的话则要主动退出
         */
        private final boolean isDaemon;
        /**
         * 线程名第一前缀
         */
        private final String firstPrefix = "BOOT-";

        /**
         * 构造函数，默认非守护线程
         *
         * @param secondPrefix 第二前缀，前面会自动加上第一前缀，后面会自动加上-T-
         */
        public NamedThreadFactory(String secondPrefix) {
            this(secondPrefix, false);
        }

        /**
         * 构造函数
         *
         * @param secondPrefix 第二前缀，前面会自动加上第一前缀，后面会自动加上-T-
         * @param daemon       是否守护线程，true的话随主线程退出而退出，false的话则要主动退出
         */
        public NamedThreadFactory(String secondPrefix, boolean daemon) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = firstPrefix + secondPrefix + "-" + POOL_COUNT.getAndIncrement() + "-T";
            isDaemon = daemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadCount.getAndIncrement(), 0);
            t.setDaemon(isDaemon);
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
