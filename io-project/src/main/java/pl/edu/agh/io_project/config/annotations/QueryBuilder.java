package pl.edu.agh.io_project.config.annotations;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryBuilder {
    int defaultPage() default 0;
    int defaultSize() default 10;
    String defaultSort() default "id";
    String defaultOrder() default "asc";
}