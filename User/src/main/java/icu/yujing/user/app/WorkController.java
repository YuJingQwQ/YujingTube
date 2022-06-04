package icu.yujing.user.app;

import icu.yujing.common.utils.R;
import icu.yujing.user.entity.po.UserPo;
import icu.yujing.user.service.UserApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 鱼颈
 * @date 2022/5/10 10:08
 */
@RestController
public class WorkController {
    @Autowired
    private UserApiService userApiService;

    @RequestMapping("/api/user/list/all")
    public R work() {
        // 使用MybatisPlus提供的API
        List<UserPo> list = userApiService.list();
        return R.ok().putData(list);
    }
}
