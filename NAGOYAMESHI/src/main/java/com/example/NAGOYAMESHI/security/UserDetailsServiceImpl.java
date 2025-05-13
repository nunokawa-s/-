package com.example.NAGOYAMESHI.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.NAGOYAMESHI.entity.User;
import com.example.NAGOYAMESHI.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("ユーザーが見つかりませんでした。");
        }
     // デバッグログ
        System.out.println("取得したユーザー: " + user.getEmail());
        System.out.println("AdminFlg: " + user.getAdminFlg());
        System.out.println("PaidFlg: " + user.getPaidFlg());

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // 管理者権限の付与
        if (user.getAdminFlg() != null && user.getAdminFlg()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        // 有料会員ロールの付与
        if (user.getPaidFlg() != null && user.getPaidFlg()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_PAID"));
        } else {
            // 有料会員でなければ無料会員ロールを付与 (必要に応じて)
            authorities.add(new SimpleGrantedAuthority("ROLE_FREE"));
        }

        return new UserDetailsImpl(user, authorities);
    }
}