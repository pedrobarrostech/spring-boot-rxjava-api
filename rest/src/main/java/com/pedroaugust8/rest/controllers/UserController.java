package com.pedroaugust8.rest.controllers;

import com.pedroaugust8.REST_API;
import com.pedroaugust8.rest.services.CustomUserDetailsService;
import com.pedroaugust8.rest.services.async.UserAsyncService;
import com.pedroaugust8.rest.viewmodels.UserView;
import com.pedroaugust8.share.entities.impl.User;
import com.pedroaugust8.share.throwble.CustomError;
import com.pedroaugust8.share.throwble.ExceptionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserAsyncService userAsyncService;

    @RequestMapping(REST_API.ME)
    public DeferredResult<ResponseEntity<UserView>> me(@AuthenticationPrincipal User user) {
        DeferredResult<ResponseEntity<UserView>> deferredResult = new DeferredResult<>();

        customUserDetailsService.me(user.getName())
                .onErrorResumeNext(e -> Observable.error(new ExceptionAdapter("Unknown user",
                        CustomError.BAD_USER, HttpStatus.NOT_FOUND)))
                .subscribe(userView -> deferredResult.setResult(ResponseEntity.accepted().body(userView)),
                        deferredResult::setErrorResult);

        return deferredResult;
    }

    @RequestMapping(REST_API.USERS)
    public Iterable<User> getUsers() {
        return customUserDetailsService.findAll();
    }

    @RequestMapping(REST_API.USERS + "rx")
    public DeferredResult<ResponseEntity<List<User>>> getUsersRx() {

        DeferredResult<ResponseEntity<List<User>>> deferredResult = new DeferredResult<>();

        userAsyncService.findAllRx()
                .onErrorResumeNext(e -> Observable.error(new ExceptionAdapter("Error getting users", CustomError.DATABASE, HttpStatus.NOT_FOUND)))
                .toList()
                .subscribe(userView -> deferredResult.setResult(ResponseEntity.accepted().body(userView)), deferredResult::setErrorResult);

        return deferredResult;
    }

}
