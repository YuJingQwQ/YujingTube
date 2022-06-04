package icu.yujing.admin.app.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import icu.yujing.admin.app.user.entity.po.UserPo;

/**
 * @author: Cyqurt
 * @date: 2022/4/13
 * @version: 1.0
 * @description:
 */
public interface UserApiService extends IService<UserPo> {
    void increaseVideoCount(Long userId);

    void subtractVideoCount(Long userId);
}
