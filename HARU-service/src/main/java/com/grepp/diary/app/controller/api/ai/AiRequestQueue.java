package com.grepp.diary.app.controller.api.ai;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AiRequestQueue {

    private final BlockingQueue<AiRequestTask> requestQueue = new LinkedBlockingQueue<>();

    public void addRequest(AiRequestTask task) {
        requestQueue.offer(task);
    }

    @Scheduled(fixedDelay = 2000) // 30RPM => 2초 간격으로 1개 처리
    public void processQueue() {
        AiRequestTask task = requestQueue.poll();
        if (task != null) {
            try {
                String result = task.getCallable().call();
                task.getFuture().complete(result);
            } catch (Exception e) {
                task.getFuture().completeExceptionally(e);
            }
        }
    }

    @Getter
    public static class AiRequestTask {
        private final Callable<String> callable;
        private final CompletableFuture<String> future;

        public AiRequestTask(Callable<String> callable, CompletableFuture<String> future) {
            this.callable = callable;
            this.future = future;
        }
    }
}
