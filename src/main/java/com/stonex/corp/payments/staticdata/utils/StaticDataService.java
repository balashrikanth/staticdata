package com.stonex.corp.payments.staticdata.utils;

import com.stonex.corp.payments.staticdata.model.StaticData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.management.Query;
import java.util.List;

public interface StaticDataService {
    /**
     * @param query custom query
     * @return list of Employee
     */
    List<StaticData> getAll(Query query);
    /**
     * Get all custom paginate data for entity Employee
     *
     * @param query    custom query
     * @param pageable pageable param
     * @return Page of entity Employee
     */
    Page<StaticData> getPage(Query query, Pageable pageable);
}


