package icu.yujing.common.security.entity;

import icu.yujing.user.entity.po.UserPo;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author: Cyqurt
 * @date: 2022/4/15
 * @version: 1.0
 * @description:
 */
@Data
public class UserDetailsEntity implements UserDetails, Serializable {
    private UserPo user;
    private List<SimpleGrantedAuthority> authorities;

    public UserDetailsEntity() {
    }

    public UserDetailsEntity(UserPo user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getPhone();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
