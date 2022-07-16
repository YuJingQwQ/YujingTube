package icu.yujing.user.app;

import icu.yujing.common.constant.ExceptionContent;
import icu.yujing.common.constant.UserModuleConstant;
import icu.yujing.common.utils.R;
import icu.yujing.common.utils.YujingUtils;
import icu.yujing.user.entity.vo.UserLoginVo;
import icu.yujing.user.service.UserLoginApiService;
import icu.yujing.user.validation.group.LoginByCodeGroup;
import icu.yujing.user.validation.group.LoginByPasswordGroup;
import icu.yujing.user.validation.group.RegisterValidationGroup;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.IOException;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Validated
@RestController
@RequestMapping("/api/user")
public class UserLoginApiController {

    @Autowired
    private UserLoginApiService userLoginApiService;

    /**
     * 用户发送密码登录请求
     *
     * @param userLoginVo
     * @return
     */
    @PostMapping("/login/by/password")
    public R userLoginByPassword(@RequestBody @Validated(LoginByPasswordGroup.class) UserLoginVo userLoginVo) {
        String jwt = userLoginApiService.userLoginByPassword(userLoginVo);
        return R.ok().putData(jwt);
    }

    /**
     * 用户发送验证码登录请求
     *
     * @param userLoginVo
     * @return
     */
    @PostMapping("/login/by/code")
    public R userLoginByCode(@RequestBody @Validated(LoginByCodeGroup.class) UserLoginVo userLoginVo) {
        String jwt = userLoginApiService.userLoginByCode(userLoginVo);
        return R.ok().putData(jwt);
    }

    /**
     * 用户发送注册请求
     *
     * @param userLoginVo
     * @return
     */
    @PostMapping("/register")
    public R userRegister(@RequestBody @Validated(RegisterValidationGroup.class) UserLoginVo userLoginVo) {
        userLoginApiService.userRegister(userLoginVo);
        return R.ok();
    }

    /**
     * 用户请求发送验证码
     *
     * @param phone
     * @return
     */
    @PostMapping("/verification/code")
    public R sendVerificationCode(@Length(min = 11, max = 11, message = "{user.phone.invalidLength}")
                                  @Pattern(regexp = "^[0-9]*$", message = "{user.phone.invalid}")
                                  @NotBlank(message = "{user.phone.notBlank}")
                                  @RequestParam("phone") String phone) {
        userLoginApiService.sendVerificationCode(phone);
        return R.ok();
    }

    /**
     * 用户发送退出登录请求 前端将localStorage的loginJwt删除掉
     *
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public R userLogout(HttpServletRequest request) {
        userLoginApiService.logout(request);
        return R.ok();
    }

    /**
     * 用户上传头像
     *
     * @param avatar
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/upload/avatar", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public R uploadAvatar(@RequestParam("avatar") MultipartFile avatar) throws IOException {
        // 检验文件大小
        boolean checkFileSize = YujingUtils.checkFileSize(avatar.getSize(), UserModuleConstant.USER_AVATAR_FILE_SIZE_LIMIT, YujingUtils.SizeUnit.MB);
        if (!checkFileSize) {
            return R.error(ExceptionContent.WRONG_FILE_SIZE.getCode(), ExceptionContent.WRONG_FILE_SIZE.getMessage());
        }
        // 检验文件类型
        boolean checkFileType = YujingUtils.checkFileType(avatar.getOriginalFilename(), UserModuleConstant.USER_AVATAR_ALLOWED_FILE_TYPES);
        if (!checkFileType) {
            return R.error(ExceptionContent.WRONG_FILE_TYPE.getCode(), ExceptionContent.WRONG_FILE_TYPE.getMessage());
        }

        // 主业务
        String avatarUrl = userLoginApiService.uploadAvatar(avatar);

        return R.ok().putData(avatarUrl);
    }
}
