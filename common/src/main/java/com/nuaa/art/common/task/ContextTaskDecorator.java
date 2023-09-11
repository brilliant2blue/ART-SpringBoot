package com.nuaa.art.common.task;

import jakarta.validation.constraints.NotNull;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 重载Task装饰器，用于实现创建异步任务时传递请求上下文
 *
 * @author konsin
 * @date 2023/09/05
 */
public class ContextTaskDecorator implements TaskDecorator {
    @Override
    @NotNull
    public Runnable decorate(@NotNull Runnable runnable) {
        // 获取主线程中的请求信息（我们的用户信息也放在里面）
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return () -> {
            try {
                // 将主线程的请求信息，设置到子线程中
                RequestContextHolder.setRequestAttributes(attributes);
                // 执行子线程
                runnable.run();
            } finally {
                // 线程结束，清空这些信息，否则可能造成内存泄漏
                RequestContextHolder.resetRequestAttributes();
            }
        };
    }
}
