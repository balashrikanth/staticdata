package com.stonex.corp.payments.staticdata.controller;

import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.dal.StaticDataDAL;
import com.stonex.corp.payments.staticdata.dto.AppReturnObject;

import com.stonex.corp.payments.staticdata.model.GenericValue;
import com.stonex.corp.payments.staticdata.model.PageInfo;
import com.stonex.corp.payments.staticdata.model.QueryCriteriaKey;
import com.stonex.corp.payments.staticdata.model.StaticQueryCriteria;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/searchandfilter")
public class SearchAndFilterController {

    private static final Logger logger = LogManager.getLogger(SearchAndFilterController.class);


    @Autowired
    private final StaticDataDAL staticDataDAL;

    public SearchAndFilterController(StaticDataDAL staticDataDAL) {
        this.staticDataDAL = staticDataDAL;
    }

    @GetMapping("/page")
    public String getSearchCriteriaPage(@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId,
            @RequestParam (required=false) Map<String,String> reqparams) {
        AppReturnObject appReturnObject = new AppReturnObject();

        //Defaults
        int page = 0;
        String orders = "ASC";//Other is DESC
        int size = 20;
        String orderby ="";


        StaticQueryCriteria staticQueryCriteria = new StaticQueryCriteria();
        staticQueryCriteria.setQueryName("page");
        QueryCriteriaKey queryCriteriaKey = new QueryCriteriaKey();
        if (reqparams !=null){
            for (Map.Entry<String, String> entry : reqparams.entrySet()){
                String key = entry.getKey().trim().toUpperCase();
                switch (key){
                    case "PAGE":
                        try {
                            page = Integer.valueOf(reqparams.get(key));
                        } catch (Exception e){
                            //If bad value default used
                        }
                        break;
                    case "ORDERS":
                        orders = reqparams.get(key);
                        break;
                    case "ORDERBY":
                        orderby = reqparams.get(key);
                        break;
                    case "SIZE":
                        try {
                            size = Integer.valueOf(reqparams.get(key));
                        }catch (Exception e){
                            //If bad value default used
                        }
                        break;
                    default:
                        queryCriteriaKey.setCriteriaKey(key);
                        queryCriteriaKey.setGenericValue(new GenericValue(reqparams.get(key)));
                }
            }
        }
        PageInfo pageInfo = new PageInfo(size,page);
        staticQueryCriteria.addCSICriteriaKey(queryCriteriaKey);
        staticQueryCriteria.setLogicalOperator("AND");
        staticQueryCriteria.setFetchAll(true);
        Query query = staticQueryCriteria.getQuery();
        /*if (orderby!=null ){
            if (orders.equalsIgnoreCase("DESC")){
                query.with(new Sort(Sort.Direction.DESC,orderby));
            } else {
                query.with(new Sort(Sort.Direction.ASC,orderby));
            }
        }

         */
        List<Object> objectList = this.staticDataDAL.getPage(true,functionId,staticQueryCriteria.getQuery(), pageInfo);
        appReturnObject.PerformReturnArrayObject(objectList);
        return appReturnObject.setReturnJSON();
    }

    @GetMapping("/all")
    public String getAllSearchCriteria(@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId,
            @RequestParam (required=false) Map<String,String> reqparams) {
        logger.debug("GET API All");
        AppReturnObject appReturnObject = new AppReturnObject();

        StaticQueryCriteria staticQueryCriteria = new StaticQueryCriteria();
        staticQueryCriteria.setQueryName("all");
        QueryCriteriaKey queryCriteriaKey = new QueryCriteriaKey();
        if (reqparams !=null){
            for (Map.Entry<String, String> entry : reqparams.entrySet()){
                String key = entry.getKey().trim().toUpperCase();
                queryCriteriaKey.setCriteriaKey(key);
                queryCriteriaKey.setGenericValue(new GenericValue(reqparams.get(key)));
            }
        }
        staticQueryCriteria.addCSICriteriaKey(queryCriteriaKey);
        staticQueryCriteria.setLogicalOperator("AND");
        staticQueryCriteria.setFetchAll(true);
        List<Object> objectList = this.staticDataDAL.getAll(true,functionId,staticQueryCriteria.getQuery());
        appReturnObject.PerformReturnArrayObject(objectList);
        return appReturnObject.setReturnJSON();
    }

}
