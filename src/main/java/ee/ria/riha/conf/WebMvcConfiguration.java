package ee.ria.riha.conf;

import ee.ria.riha.storage.util.FilterableArgumentResolver;
import ee.ria.riha.storage.util.PageableArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(new PageableArgumentResolver());
        argumentResolvers.add(new FilterableArgumentResolver());
    }

}