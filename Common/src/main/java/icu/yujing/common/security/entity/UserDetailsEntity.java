package icu.yujing.common.security.entity;

import icu.yujing.user.entity.po.UserPo;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author: Cyqurt
 * @date: 2022/4/15
 * @version: 1.0
 * @description:
 */
@Data
public class UserDetailsEntity implements UserDetails {
    private UserPo user;
    private List<SimpleGrantedAuthority> authorities;

    public UserDetailsEntity() {
    }

    public UserDetailsEntity(UserPo user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        if (authorities == null) {
//            if (authorityList != null && authorityList.size() > 0) {
//                authorities = authorityList.stream()
//                        .map(SimpleGrantedAuthority::new)
//                        .collect(Collectors.toList());
//            } else {
//                authorities = new ArrayList<>(0);
//            }
//        }
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
