package icu.yujing.user.security.service.impl;

import icu.yujing.user.service.UserLoginApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author: Cyqurt
 * @date: 2022/4/15
 * @version: 1.0
 * @description:
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserLoginApiService userLoginApiService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//        userLoginApiService.userLoginByPassword()
        return null;
    }
}
