package com.stonex.corp.payments.staticdata.repository;

import com.stonex.corp.payments.staticdata.entity.StaticDataMetaInfoDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaticDataMetaInfoDBRepository extends MongoRepository<StaticDataMetaInfoDB,String> {
    StaticDataMetaInfoDB findFirstByStaticDataPKAndCollectionName(String statciDataPK, String collectionName);
}
