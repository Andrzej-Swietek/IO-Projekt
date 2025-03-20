package pl.edu.agh.io_project.config.annotations;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class QueryBuilderResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(QueryBuilder.class) != null
                && parameter.getParameterType().equals(QueryBuilderParams.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        QueryBuilder annotation = parameter.getParameterAnnotation(QueryBuilder.class);
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        int page = parseOrDefault(request.getParameter("page"), annotation.defaultPage());
        int size = parseOrDefault(request.getParameter("size"), annotation.defaultSize());
        String sort = request.getParameter("sort") != null ? request.getParameter("sort") : annotation.defaultSort();
        String order = request.getParameter("order") != null ? request.getParameter("order") : annotation.defaultOrder();

        return new QueryBuilderParams(page, size, sort, order);
    }

    private int parseOrDefault(String value, int defaultValue) {
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
