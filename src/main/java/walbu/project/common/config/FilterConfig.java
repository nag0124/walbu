package walbu.project.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import walbu.project.common.filter.JwtFilter;
import walbu.project.common.jwt.JwtProvider;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilter(JwtProvider jwtProvider, ObjectMapper objectMapper) {
        FilterRegistrationBean<JwtFilter> filterRegistrationBean = new FilterRegistrationBean<>();

        filterRegistrationBean.setFilter(new JwtFilter(jwtProvider, objectMapper));
        return filterRegistrationBean;
    }

}
