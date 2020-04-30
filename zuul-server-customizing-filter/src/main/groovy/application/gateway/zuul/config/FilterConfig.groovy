package application.gateway.zuul.config

import application.gateway.zuul.filter.PostLogFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterConfig {
    @Bean
    public PostLogFilter postLogFilter() {
        new PostLogFilter()
    }
}
