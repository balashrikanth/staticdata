package com.stonex.corp.payments.staticdata.repository;

import com.stonex.corp.payments.staticdata.entity.StaticDataAuditDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaticDataAuditDBRepository extends MongoRepository<StaticDataAuditDB,String> {

}
