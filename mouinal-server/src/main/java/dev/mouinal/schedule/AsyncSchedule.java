package dev.mouinal.schedule;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class AsyncSchedule {
    private final AsyncExecutor asyncExecutor;

    public AsyncSchedule() {
        this.asyncExecutor = new AsyncExecutor();
    }

    public void runNow(Runnable runnable) {
        asyncExecutor.execute(runnable);
    }

    public void run(Runnable runnable) {
        asyncExecutor.submit(runnable);
    }

    public void close() {
        if (asyncExecutor.isTerminated()) {
            asyncExecutor.shutdownNow();
        }
    }

    private static class AsyncExecutor implements ExecutorService {
        private final ThreadPoolExecutor executor;

        private AsyncExecutor() {
            this.executor = new ThreadPoolExecutor(
                    Math.max(2, Runtime.getRuntime().availableProcessors() / 2),
                    Integer.MAX_VALUE,
                    30L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>()
            );
        }

        @Override
        public void shutdown() {
            executor.shutdown();
        }

        @NotNull
        @Override
        public List<Runnable> shutdownNow() {
            return executor.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return executor.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return executor.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
            return executor.awaitTermination(timeout, unit);
        }

        @NotNull
        @Override
        public <T> Future<T> submit(@NotNull Callable<T> task) {
            return executor.submit(task);
        }

        @NotNull
        @Override
        public <T> Future<T> submit(@NotNull Runnable task, T result) {
            return executor.submit(task, result);
        }

        @NotNull
        @Override
        public Future<?> submit(@NotNull Runnable task) {
            return executor.submit(task);
        }

        @NotNull
        @Override
        public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return executor.invokeAll(tasks);
        }

        @NotNull
        @Override
        public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
            return executor.invokeAll(tasks, timeout, unit);
        }

        @NotNull
        @Override
        public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            return executor.invokeAny(tasks);
        }

        @Override
        public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return executor.invokeAny(tasks, timeout, unit);
        }

        @Override
        public void close() {
            executor.close();
        }

        @Override
        public void execute(@NotNull Runnable command) {
            executor.execute(command);
        }
    }
}
