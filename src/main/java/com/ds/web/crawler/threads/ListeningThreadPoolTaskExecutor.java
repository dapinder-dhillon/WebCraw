package com.ds.web.crawler.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * The Class ListeningThreadPoolTaskExecutor. A traditional Future represents
 * the result of an asynchronous computation: a computation that may or may not
 * have finished producing a result yet. A Future can be a handle to an
 * in-progress computation, a promise from a service to supply us with a result.
 * 
 * A ListenableFuture allows you to register callbacks to be executed once the
 * computation is complete, or if the computation is already complete,
 * immediately.
 */
public class ListeningThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 938110017998181987L;

	@Override
	public Future<?> submit(Runnable task) {
		ListeningExecutorService executor = MoreExecutors.listeningDecorator(getThreadPoolExecutor());

		try {
			return executor.submit(task);
		} catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
		}
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		ListeningExecutorService executor = MoreExecutors.listeningDecorator(getThreadPoolExecutor());

		try {
			return executor.submit(task);
		} catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
		}
	}
}
