package finances.finances.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);       // threads always kept alive
        executor.setMaxPoolSize(10);       // max threads under heavy load
        executor.setQueueCapacity(100);    // requests waiting if all threads busy
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();

        // Wraps the executor so Spring Security context is passed to async threads
        // This is what makes SecurityUtils.getCurrentUser() work inside @Async methods
        return new DelegatingSecurityContextExecutor(executor);
    }
}