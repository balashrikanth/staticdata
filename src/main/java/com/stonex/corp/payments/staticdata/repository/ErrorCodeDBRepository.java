package com.stonex.corp.payments.staticdata.repository;

import com.stonex.corp.payments.staticdata.entity.ErrorCodeDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorCodeDBRepository extends MongoRepository<ErrorCodeDB,String> {
}
