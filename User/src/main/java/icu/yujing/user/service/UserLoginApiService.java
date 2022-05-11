package icu.yujing.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import icu.yujing.common.utils.R;
import icu.yujing.user.entity.po.UserPo;
import icu.yujing.user.entity.vo.UserLoginVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
public interface UserLoginApiService extends IService<UserPo> {
    String userLoginByPassword(UserLoginVo userLoginVo);

    String userLoginByCode(UserLoginVo userLoginVo);

    UserPo userRegister(UserLoginVo userLoginVo);

    R sendVerificationCode(String phone);

    String uploadAvatar(MultipartFile avatar) throws IOException;


    void logout(HttpServletRequest request);
}
