package com.stonex.corp.payments.staticdata.repository;

import com.stonex.corp.payments.staticdata.entity.TemplateDB;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TemplateDBRepository extends MongoRepository<TemplateDB,String> {
    List<TemplateDB> findAllByTemplatetypeAndActive(String templatetype,boolean status);
}
