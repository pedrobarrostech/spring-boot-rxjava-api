package com.pedroaugust8.rest.services;

import com.pedroaugust8.rest.viewmodels.UserView;
import com.pedroaugust8.share.entities.impl.Role;
import com.pedroaugust8.share.entities.impl.User;
import com.pedroaugust8.share.persistence.RoleRepository;
import com.pedroaugust8.share.persistence.UserRepository;
import com.pedroaugust8.share.persistence.async.RoleAsyncPersistence;
import com.pedroaugust8.share.persistence.async.UserAsyncPersistence;
import com.pedroaugust8.share.throwble.CustomError;
import com.pedroaugust8.share.throwble.ExceptionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s does not exist!", username));
        }
        return new UserRepositoryUserDetails(user);
    }

    public Observable<UserView> me(String username) {
        try {
            return Observable.just(userRepository.findByLogin(username)).map(toUserView);
        } catch (Exception e) {
            return Observable.error(new ExceptionAdapter("Error while getting user from database",
                    CustomError.DATABASE, HttpStatus.INTERNAL_SERVER_ERROR, e));
        }
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }


    private final static class UserRepositoryUserDetails extends User implements UserDetails {

        private static final long serialVersionUID = 1L;

        private UserRepositoryUserDetails(User user) {
            super(user);
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return getRoles();
        }

        @Override
        public String getUsername() {
            return getLogin();
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

    public static rx.functions.Func1<User, UserView> toUserView = user -> {
        UserView userView = new UserView();
        userView.setId(user.getId());
        userView.setName(user.getName());
        userView.setLogin(user.getLogin());
        userView.setRoles(user.getRoles().stream().map(Role::getAuthority).collect(Collectors.toList()));
        return userView;
    };

}
