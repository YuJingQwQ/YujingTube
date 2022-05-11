package icu.yujing.user.security.filters;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import icu.yujing.common.constant.ExceptionContent;
import icu.yujing.common.constant.UserModuleConstant;
import icu.yujing.common.security.entity.UserDetailsEntity;
import icu.yujing.common.utils.JwtUtils;
import icu.yujing.common.utils.R;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: Cyqurt
 * @date: 2022/4/15
 * @version: 1.0
 * @description:
 */
public class CheckUserLoginStatusByJwtFilter extends OncePerRequestFilter {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public static final ThreadLocal<UserDetailsEntity> currentThreadUser = new ThreadLocal();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 解析Jwt
        String jwt = request.getHeader(UserModuleConstant.JWT_TOKEN_HTTP_HEADER_KEY);
        // 判断请求头是否携带jwt
        if (StringUtils.isEmpty(jwt)) {
            // 放行
            currentThreadUser.set(new UserDetailsEntity());
            filterChain.doFilter(request, response);
            currentThreadUser.remove();
            return;
        }
        Claims claims;
        try {
            claims = JwtUtils.parseJwt(UserModuleConstant.JWT_USER_SIGNATURE, jwt);
        } catch (Exception e) {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Type", "application/json");
            PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(R.error(ExceptionContent.DIY_EXCEPTION.getCode(), "非法Token")));
            writer.close();
            return;
        }
        String userId = claims.get(UserModuleConstant.JWT_USER_KEY).toString();

        // 从Redis中查询此用户
        String userDetailsJson = redisTemplate.opsForValue().get(UserModuleConstant.USER_KEY_PREFIX_IN_REDIS + userId);
        if (StringUtils.isEmpty(userDetailsJson)) {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Type", "application/json");
            PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(R.error(ExceptionContent.LOGIN_HAS_EXPIRED.getCode(), ExceptionContent.LOGIN_HAS_EXPIRED.getMessage())));
            writer.close();
            return;
        }
        UserDetailsEntity userDetailsEntity = JSON.parseObject(userDetailsJson, new TypeReference<UserDetailsEntity>() {
        });
        currentThreadUser.set(userDetailsEntity);
        //存入SecurityContextHolder
        //TODO 获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetailsEntity.getUser(), null, userDetailsEntity.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        filterChain.doFilter(request, response);
        // 清空当前线程的数据
        currentThreadUser.remove();
    }
}
