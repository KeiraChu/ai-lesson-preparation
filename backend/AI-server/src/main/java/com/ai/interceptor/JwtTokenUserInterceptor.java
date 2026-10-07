package com.ai.interceptor;

import com.ai.constant.JwtClaimsConstant;
import com.ai.context.UserContext;
import com.ai.properties.JwtProperties;
import com.ai.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * @author ：褚婧雯
 * @date ：2025/3/25 13:55
 * @description : jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        判断当前拦截到的是Controller的方法还是其他资源
        if(!(handler instanceof HandlerMethod)){
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
        // 从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());

        //校验令牌
        try{
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            String username = claims.get(JwtClaimsConstant.USERNAME).toString();
            Object userId = claims.get(JwtClaimsConstant.USER_ID);
            if (userId == null) {
                throw new IllegalArgumentException("令牌缺少用户身份");
            }
            UserContext.setUserId(Long.valueOf(userId.toString()));
            return true;
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":0,\"msg\":\"登录状态无效或已过期\",\"data\":null}");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
