package icu.yujing.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yujing.common.constant.ExceptionContent;
import icu.yujing.common.constant.UserModuleConstant;
import icu.yujing.common.exception.MyTopException;
import icu.yujing.common.security.entity.UserDetailsEntity;
import icu.yujing.common.utils.JwtUtils;
import icu.yujing.common.utils.R;
import icu.yujing.common.utils.YujingUtils;
import icu.yujing.user.config.UserModuleConfig;
import icu.yujing.user.dao.UserDao;
import icu.yujing.user.entity.po.UserPo;
import icu.yujing.user.entity.to.VerificationCodeTo;
import icu.yujing.user.entity.vo.UserLoginVo;
import icu.yujing.user.feign.ThirdPartyFeignService;
import icu.yujing.user.service.UserApiService;
import icu.yujing.user.service.UserLoginApiService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Service
public class UserLoginApiServiceImpl extends ServiceImpl<UserDao, UserPo> implements UserLoginApiService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserApiService userApiService;

    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;

    private String createJwtAndSaveUserIntoRedis(UserPo user) {
        // 将userId 进行JWT加密并返回JWT
        String jwt = JwtUtils.createJwt(UserModuleConstant.JWT_USER_KEY,
                user.getId().toString(),
                UserModuleConstant.JWT_USER_SIGNATURE,
                new Date(System.currentTimeMillis() + UserModuleConstant.JWT_USER_EXPIRATION));
        // 将用户数据保存到Redis中
        user.setPassword(null);
        UserDetailsEntity userDetailsEntity = new UserDetailsEntity(user);
        List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("user"));
        userDetailsEntity.setAuthorities(authorities);
        redisTemplate.opsForValue()
                .set(UserModuleConstant.USER_KEY_PREFIX_IN_REDIS + user.getId(),
                        JSON.toJSONString(userDetailsEntity),
                        UserModuleConstant.JWT_USER_EXPIRATION, TimeUnit.MILLISECONDS);
        return jwt;
    }

    @Override
    public String userLoginByPassword(UserLoginVo userLoginVo) {
        UserPo user = userApiService.getOne(new QueryWrapper<UserPo>().eq("phone", userLoginVo.getPhone()));
        if (user == null) {
            throw new MyTopException(ExceptionContent.ACCOUNT_IS_NOT_EXISTED.getCode(),
                    ExceptionContent.ACCOUNT_IS_NOT_EXISTED.getMessage());
        }
        // 密码登录
        boolean result = UserModuleConfig.USER_PASSWORD_ENCODER.matches(userLoginVo.getPassword(), user.getPassword());
        if (result) {
            return createJwtAndSaveUserIntoRedis(user);
        } else {
            throw new MyTopException(ExceptionContent.WRONG_ACCOUNT_OR_PASSWORD.getCode(),
                    ExceptionContent.WRONG_ACCOUNT_OR_PASSWORD.getMessage());
        }

    }

    @Override
    public String userLoginByCode(UserLoginVo userLoginVo) {
        // 验证码登录
        UserPo user = userApiService.getOne(new QueryWrapper<UserPo>().eq("phone", userLoginVo.getPhone()));
        DefaultRedisScript<Long> script = new DefaultRedisScript<>("if redis.call('GET',KEYS[1]) == ARGV[1] then return redis.call('DEL',KEYS[1]) else return 2 end ", Long.class);
        Long result = redisTemplate.execute(script,
                Arrays.asList(UserModuleConstant.USER_VERIFICATION_CODE_PREFIX + userLoginVo.getPhone()),
                userLoginVo.getVerificationCode());
        // result-> 0:验证码相同,但删除KEY的时候失败了 1:验证通过 2:验证码不相同
        if (result == 1L) {
            return createJwtAndSaveUserIntoRedis(user);
        } else if (result == 2L) {
            throw new MyTopException(ExceptionContent.WRONG_VERIFICATION_CODE.getCode(),
                    ExceptionContent.WRONG_VERIFICATION_CODE.getMessage());
        } else {
            throw new MyTopException(ExceptionContent.REDIS_KEY_CANT_BE_DELETED.getCode(),
                    ExceptionContent.REDIS_KEY_CANT_BE_DELETED.getMessage());
        }
    }


    @Override
    public R sendVerificationCode(String phone) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>("if redis.call('SETNX',KEYS[1],ARGV[1]) == 1 then return redis.call('EXPIRE',KEYS[1],ARGV[2]) else return 2 end", Long.class);
        String verificationCode = ((int) (Math.random() * 100000)) + "";
        List<String> keys = Arrays.asList(UserModuleConstant.USER_VERIFICATION_CODE_PREFIX + phone);
        Long result = redisTemplate.execute(script, keys, verificationCode, "60");
        System.out.println("result: " + result);


        if (result == 1L) {
            //TODO 调用远程服务
            VerificationCodeTo verificationCodeTo = new VerificationCodeTo();
            verificationCodeTo.setPhone(phone);
            verificationCodeTo.setVerificationCode(verificationCode);
            R resultR = thirdPartyFeignService.sendTheVerificationCode(verificationCodeTo);
            if (resultR.getCode() == 200) {
                return R.ok();
            } else {
                return R.error(88888, "发送验证码失败");
            }
        } else if (result == 0) {
            return R.error(ExceptionContent.REDIS_KEY_CANT_BE_EXPIRED.getCode(), ExceptionContent.REDIS_KEY_CANT_BE_EXPIRED.getMessage());
        } else {
            return R.error(ExceptionContent.REDIS_KEY_IS_EXISTED.getCode(), ExceptionContent.REDIS_KEY_IS_EXISTED.getMessage());
        }
    }

    @Override
    public String uploadAvatar(MultipartFile avatar) throws IOException {
        String path = YujingUtils.getPath(UserModuleConstant.USER_AVATAR_OSS_PREFIX, avatar.getOriginalFilename());
        R result = thirdPartyFeignService.uploadFile(avatar, path);
        if (result.getCode() == 200) {
            Object avatarUrl = result.getData();
            if (!StringUtils.isEmpty(avatarUrl)) {
                return (String) avatarUrl;
            }
        }
        return null;
    }

    @Override
    public void logout(HttpServletRequest request) {
        String jwt = request.getHeader(UserModuleConstant.JWT_TOKEN_HTTP_HEADER_KEY);
        Claims claims = JwtUtils.parseJwt(UserModuleConstant.JWT_USER_SIGNATURE, jwt);
        String userId = claims.get(UserModuleConstant.JWT_USER_KEY).toString();
        redisTemplate.delete(UserModuleConstant.USER_KEY_PREFIX_IN_REDIS + userId);
    }

    @Override
    public UserPo userRegister(UserLoginVo userLoginVo) {
        if (!userLoginVo.getPassword().equals(userLoginVo.getRepassword())) {
            throw new MyTopException(ExceptionContent.DIFFERENT_PASSWORD.getCode(),
                    ExceptionContent.DIFFERENT_PASSWORD.getMessage());
        }


        DefaultRedisScript<Long> script = new DefaultRedisScript<>("if redis.call('GET',KEYS[1]) == ARGV[1] then return redis.call('DEL',KEYS[1]) else return 2 end", Long.class);
        Long result = redisTemplate.execute(script,
                Arrays.asList(UserModuleConstant.USER_VERIFICATION_CODE_PREFIX + userLoginVo.getPhone()),
                userLoginVo.getVerificationCode());
        // result-> 0:验证码相同,但删除KEY的时候失败了 1:验证通过 2:验证码不相同
        if (result == 1L) {
            // 如果以前注册过,直接通过
            UserPo user = userApiService.getOne(new QueryWrapper<UserPo>().eq("phone", userLoginVo.getPhone()));
            if (user != null) {
                return user;
            }
            // 注册账号并登录
            UserPo userPo = new UserPo();
            userPo.setAvatar(userLoginVo.getAvatar());
            userPo.setNickname(userLoginVo.getNickname());
            userPo.setPhone(userLoginVo.getPhone());
            userPo.setPassword(UserModuleConfig.USER_PASSWORD_ENCODER.encode(userLoginVo.getPassword()));
            userApiService.save(userPo);
            // 可以直接将userPo填充0值后返回,不需要查询数据库
            user = userApiService.getOne(new QueryWrapper<UserPo>().eq("phone", userLoginVo.getPhone()));
            return user;
        } else if (result == 2L) {
            throw new MyTopException(ExceptionContent.WRONG_VERIFICATION_CODE.getCode(),
                    ExceptionContent.WRONG_VERIFICATION_CODE.getMessage());
        } else {
            throw new MyTopException(ExceptionContent.REDIS_KEY_CANT_BE_DELETED.getCode(),
                    ExceptionContent.REDIS_KEY_CANT_BE_DELETED.getMessage());
        }
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserPo user = getOne(new QueryWrapper<UserPo>().eq("phone", username));
//        UserDetailsEntity userDetails = new UserDetailsEntity(user);
//        return userDetails;
//    }
}
