package com.volasoftware.tinder.controllers;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAsyncExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        LOGGER.error("Exception message - " + throwable.getMessage());
        LOGGER.error("Method name - " + method.getName());
        for (Object param : obj) {
            LOGGER.error("Parameter value - " + param);
        }
    }
}
