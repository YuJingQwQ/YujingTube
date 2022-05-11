package icu.yujing.user.entity.vo;

import icu.yujing.user.validation.group.LoginByCodeGroup;
import icu.yujing.user.validation.group.LoginByPasswordGroup;
import icu.yujing.user.validation.group.RegisterValidationGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Data
public class UserLoginVo {

    @NotBlank(message = "{user.nickname.notBlank}", groups = RegisterValidationGroup.class)
    @Length(min = 2, max = 16, message = "{user.nickname.InvalidLength}", groups = RegisterValidationGroup.class)
    private String nickname;

    @NotBlank(message = "{user.avatar.notBlank}", groups = RegisterValidationGroup.class)
    @Pattern(regexp = "^https://yujing-youtube.oss-cn-guangzhou.aliyuncs.com/user/avatar/[a-zA-z0-9./-]*", message = "{user.avatar.invalid}", groups = RegisterValidationGroup.class)
    private String avatar;

    @Length(min = 11, max = 11, message = "{user.phone.invalidLength}", groups = {LoginByPasswordGroup.class, LoginByCodeGroup.class, RegisterValidationGroup.class})
    @Pattern(regexp = "^[0-9]*$", message = "{user.phone.invalid}", groups = {LoginByPasswordGroup.class, LoginByCodeGroup.class, RegisterValidationGroup.class})
    @NotBlank(message = "{user.phone.notBlank}", groups = {LoginByPasswordGroup.class, LoginByCodeGroup.class, RegisterValidationGroup.class})
    private String phone;

    @NotBlank(message = "{user.password.notBlank}", groups = {LoginByPasswordGroup.class, RegisterValidationGroup.class})
    @Pattern(regexp = "^[0-9a-zA-Z!._]*$", message = "{user.password.invalid}", groups = {LoginByPasswordGroup.class, RegisterValidationGroup.class})
    private String password;
    private String repassword;

    @NotBlank(message = "{user.verificationCode.notBlank}", groups = {LoginByCodeGroup.class, RegisterValidationGroup.class})
    private String verificationCode;
}
