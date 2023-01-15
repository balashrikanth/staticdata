package com.stonex.corp.payments.staticdata.repository;

import com.stonex.corp.payments.staticdata.entity.EntityValidationRulesDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntityValidationRulesDBRepository extends MongoRepository<EntityValidationRulesDB,String> {
    List<EntityValidationRulesDB>  findAllByFunctionid(String functionId);
}
