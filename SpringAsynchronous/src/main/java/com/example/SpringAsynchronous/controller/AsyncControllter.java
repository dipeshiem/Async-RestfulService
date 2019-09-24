package com.example.SpringAsynchronous.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class AsyncControllter {
	
	@Autowired
	 Executor taskExecutor;

	Logger log = Logger.getLogger("AsyncControllter");
	
	@RequestMapping(path = "/sync", method = RequestMethod.GET)
	public String getValueSync() {

	  log.info("Request received");

	  return doSomething();

	}
	
	@RequestMapping(path = "/asyncDeferred", method = RequestMethod.GET)
	public DeferredResult<String> getValueAsyncUsingDeferredResult() {

	    log.info("Request received");

	    DeferredResult<String> deferredResult = new DeferredResult<>();

	    ForkJoinPool.commonPool()
	            .submit(() -> deferredResult.setResult(doSomething()));

	    log.info("Servlet thread released");

	    return deferredResult;

	}
	
	@RequestMapping(path = "/asyncCompletable", method = RequestMethod.GET)
	public CompletableFuture<String> getValueAsyncUsingCompletableFuture() {

	  log.info("Request received");

	  CompletableFuture<String> completableFuture =
	      CompletableFuture.supplyAsync(this::doSomething);

	  log.info("Servlet thread released");

	  return completableFuture;

	}
	
	/**
	 * without lambda
	 * @return
	 */
	@RequestMapping(path = "/asyncCompletableComposed", method = RequestMethod.GET)
	public CompletableFuture<String> getValueAsyncUsingCompletableFutureComposed() {
		 log.info("Request received");
	    CompletableFuture<String> completableFuture = CompletableFuture
	            .supplyAsync(this::doSomething)
	            .thenApplyAsync(new Function<String, String>() {
	            	public String apply(String t) {
	            		return doSomString1();
	            		
	            	}
	            	
				});
	    
	    log.info("Servlet thread released");
	    return completableFuture;

	}

	
	/**
	 * with lambda
	 * @return
	 */
	
	@RequestMapping(path = "/asyncCompletableWithLambda", method = RequestMethod.GET)
	public CompletableFuture<String> getValueAsyncUsingCompletableFutureComposedLambda() {
		 log.info("Request received");
		 CompletableFuture<String> completableFuture = CompletableFuture
		            .supplyAsync(this::doSomething).thenApplyAsync((input)->doSomString1() );
	    
	    log.info("Servlet thread released");
	    return completableFuture;

	}
	
	@RequestMapping(path = "/asyncCompletableWithLambdaExecuter", method = RequestMethod.GET)
	public CompletableFuture<String> getValueAsyncUsingCompletableFutureComposedLambdaseperateExecuter() {
		 log.info("Request received");
		 CompletableFuture<String> completableFuture = CompletableFuture
		            .supplyAsync(this::doSomething).thenApplyAsync((input)->doSomString1(),taskExecutor);
	    
	    log.info("Servlet thread released");
	    return completableFuture;

	}

	
	
	
	private String doSomething() {
	    log.info("Start processing request");
	    try {
	        Thread.sleep(50000);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	    log.info("Completed processing request");
	    return "RESULT";
	}
	
	private String doSomString1() {
	    log.info("Start processing request reverse");
	    try {
	        Thread.sleep(50000);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	    log.info("Completed processing request reverse");
	    return "TLUSER";
	}

}
