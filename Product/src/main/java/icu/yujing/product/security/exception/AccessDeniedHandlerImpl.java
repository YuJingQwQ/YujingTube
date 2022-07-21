package icu.yujing.product.security.exception;

import com.alibaba.fastjson.JSON;
import icu.yujing.common.constant.ExceptionContent;
import icu.yujing.common.utils.R;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
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
 * @description:
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(R.error(ExceptionContent.NO_ENOUGH_AUTHORITY.getCode(), ExceptionContent.NO_ENOUGH_AUTHORITY.getMessage())));
        writer.close();
    }
}
