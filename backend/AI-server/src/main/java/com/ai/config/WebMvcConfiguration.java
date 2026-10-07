package com.ai.config;

import com.ai.interceptor.JwtTokenUserInterceptor;
import com.ai.properties.FileProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author ：褚婧雯
 * @date ：2025/3/25 15:46
 * @description ：配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private FileProperties fileProperties;
    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册拦截器...");
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**","/text/**","/ppt/**","/file/**","/practice/**","/analyze/**","/ai/**","/knowledge/**")
                .excludePathPatterns("/user/login","/user/captcha","/user/register");
    }

    /**
     * 跨域配置
     * @param registry
     */
    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE"); // 显式允许这些HTTP方法
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源位置
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:"+fileProperties.getUploadPath());
    }
}
