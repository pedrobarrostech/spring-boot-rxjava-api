package com.pedroaugust8.share.persistence.async;

import com.github.pgasync.Db;
import com.github.pgasync.ResultSet;
import com.github.pgasync.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

@Service
public class UserAsyncPersistence {

    public static final String SELECT_FROM_USER_TABLE = "select * from user_table";

    @Autowired
    private Db postgresAsyncConnectionPool;


    public Observable<Row> findAllUsersInDBAsRows(){
        return postgresAsyncConnectionPool.queryRows(SELECT_FROM_USER_TABLE);
    }

    public Observable<ResultSet> findAllUsersInDBAsResultSet(){
        return postgresAsyncConnectionPool.querySet(SELECT_FROM_USER_TABLE);
    }
}
