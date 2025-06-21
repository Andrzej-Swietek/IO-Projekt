package pl.edu.agh.io_project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.edu.agh.io_project.config.annotations.QueryBuilderResolver;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final QueryBuilderResolver queryBuilderResolver;

    public WebConfig(QueryBuilderResolver queryBuilderResolver) {
        this.queryBuilderResolver = queryBuilderResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(queryBuilderResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}