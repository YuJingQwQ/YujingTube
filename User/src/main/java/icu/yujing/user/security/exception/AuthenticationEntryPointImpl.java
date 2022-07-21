package icu.yujing.user.security.exception;

import com.alibaba.fastjson.JSON;
import icu.yujing.common.constant.ExceptionContent;
import icu.yujing.common.utils.R;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author 鱼颈
 * @date 2022/7/21 14:35
 * @version: 1.0
 * @description: 由于登录功能没有用到AuthenticationManager, 所以此类不会执行
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(R.error(ExceptionContent.WRONG_ACCOUNT_OR_PASSWORD.getCode(), ExceptionContent.WRONG_ACCOUNT_OR_PASSWORD.getMessage())));
        writer.close();
    }
}
