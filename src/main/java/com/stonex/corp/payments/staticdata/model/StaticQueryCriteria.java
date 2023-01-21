package com.stonex.corp.payments.staticdata.model;

import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StaticQueryCriteria {
    private String queryName;
    private List<QueryCriteriaKey> queryCriteriaKeyList;
    private String logicalOperator;//AND, OR
    private boolean fetchAll;//True get all - False get One

    public StaticQueryCriteria() {
        this.queryCriteriaKeyList = new ArrayList<QueryCriteriaKey>();
        this.logicalOperator = "AND";
        this.fetchAll = true;
    }

    public StaticQueryCriteria(String queryName, List<QueryCriteriaKey> queryCriteriaKeyList) {
        this.queryName = queryName;
        this.queryCriteriaKeyList = queryCriteriaKeyList;
        this.logicalOperator = "AND";
        this.fetchAll = true;
    }

    public StaticQueryCriteria(String queryName, List<QueryCriteriaKey> queryCriteriaKeyList, String logicalOperator, boolean fetchAll) {
        this.queryName = queryName;
        this.queryCriteriaKeyList = queryCriteriaKeyList;
        this.logicalOperator = logicalOperator;
        this.fetchAll = fetchAll;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public List<QueryCriteriaKey> getQueryCriteriaKeyList() {
        return queryCriteriaKeyList;
    }

    public void setQueryCriteriaKeyList(List<QueryCriteriaKey> queryCriteriaKeyList) {
        this.queryCriteriaKeyList = queryCriteriaKeyList;
    }

    public void addCSICriteriaKey(QueryCriteriaKey csiQueryCriteriaKey){
        this.queryCriteriaKeyList.add(csiQueryCriteriaKey);
    }

    public boolean isFetchAll() {
        return fetchAll;
    }

    public void setFetchAll(boolean fetchAll) {
        this.fetchAll = fetchAll;
    }

    public String getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public List<Criteria> buildCriteria(){
        List<Criteria> criteriaList = new ArrayList<Criteria>();
        if (queryCriteriaKeyList !=null){
            for (int i = 0; i< queryCriteriaKeyList.size(); i++){
                QueryCriteriaKey csiQueryCriteriaKey = queryCriteriaKeyList.get(i);
                String operator = "EQ";
                if (csiQueryCriteriaKey.getOperator()!=null){
                    operator = csiQueryCriteriaKey.getOperator();
                }
                csiQueryCriteriaKey.setCriteriaKey(csiQueryCriteriaKey.getCriteriaKey());
                GenericValue genericValue = csiQueryCriteriaKey.getGenericValue();
                if (genericValue!=null && genericValue.getValueDataType()!=null && genericValue.getValue()!=null){
                    switch(genericValue.getValueDataType().trim().toUpperCase()){
                        case "**ALL**":
                            //No criteria to be applied
                            break;
                        case "STRING":
                            switch(operator){
                                case "EQ":
                                    criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(genericValue.getValue()));
                                    break;
                                case "LT":
                                    criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).lt(genericValue.getValue()));
                                    break;
                                case "LE":
                                    criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).lte(genericValue.getValue()));
                                    break;
                                case "GT":
                                    criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).gt(genericValue.getValue()));
                                    break;
                                case "GE":
                                    criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).gte(genericValue.getValue()));
                                    break;
                                case "NE":
                                    criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).ne(genericValue.getValue()));
                                    break;
                                default:
                                    criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(genericValue.getValue()));
                                    break;
                            }
                            break;
                        case "INTEGER":
                            try {
                                int intValue = Integer.parseInt(genericValue.getValue());
                                switch(operator){
                                    case "EQ":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(intValue));
                                        break;
                                    case "LT":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).lt(intValue));
                                        break;
                                    case "LE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).lte(intValue));
                                        break;
                                    case "GT":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).gt(intValue));
                                        break;
                                    case "GE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).gte(intValue));
                                        break;
                                    case "NE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).ne(intValue));
                                        break;
                                    default:
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(intValue));
                                        break;
                                }
                            } catch (Exception e){
                                break;
                            }
                            break;
                        case "DOUBLE":
                            try {
                                double doubleValue = Double.parseDouble(genericValue.getValue());
                                switch(operator){
                                    case "EQ":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(doubleValue));
                                        break;
                                    case "LT":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).lt(doubleValue));
                                        break;
                                    case "LE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).lte(doubleValue));
                                        break;
                                    case "GT":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).gt(doubleValue));
                                        break;
                                    case "GE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).gte(doubleValue));
                                        break;
                                    case "NE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).ne(doubleValue));
                                        break;
                                    default:
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(doubleValue));
                                        break;
                                }
                            } catch (Exception e){
                                break;
                            }
                            break;
                        case "FLOAT":
                            try {
                                float floatValue = Float.parseFloat(genericValue.getValue());
                                switch(operator){
                                    case "EQ":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(floatValue));
                                        break;
                                    case "LT":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).lt(floatValue));
                                        break;
                                    case "LE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).lte(floatValue));
                                        break;
                                    case "GT":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).gt(floatValue));
                                        break;
                                    case "GE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).gte(floatValue));
                                        break;
                                    case "NE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).ne(floatValue));
                                        break;
                                    default:
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(floatValue));
                                        break;
                                }
                            } catch (Exception e){
                                break;
                            }
                            break;
                        case "DATE":
                            //Date is expected in YYYMMDD format
                            try {
                                Date date = new SimpleDateFormat("yyyyMMdd").parse(genericValue.getValue());
                                switch(operator){
                                    case "EQ":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(date));
                                        break;
                                    case "LT":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).lt(date));
                                        break;
                                    case "LE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).lte(date));
                                        break;
                                    case "GT":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).gt(date));
                                        break;
                                    case "GE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).gte(date));
                                        break;
                                    case "NE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).ne(date));
                                        break;
                                    default:
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(date));
                                        break;
                                }
                            } catch (Exception e){
                                break;
                            }
                            break;
                        case "DATETIME":
                            //DateTime is expected in YYYMMDDHHMMSS format
                            try {
                                Date datetime = new SimpleDateFormat("yyyyMMddHHmmss").parse(genericValue.getValue());
                                switch(operator){
                                    case "EQ":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(datetime));
                                        break;
                                    case "LT":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).lt(datetime));
                                        break;
                                    case "LE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).lte(datetime));
                                        break;
                                    case "GT":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).gt(datetime));
                                        break;
                                    case "GE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).gte(datetime));
                                        break;
                                    case "NE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).ne(datetime));
                                        break;
                                    default:
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(datetime));
                                        break;
                                }
                            } catch (Exception e){
                                break;
                            }
                            break;
                        case "BOOLEAN":
                            try {
                                boolean result = false;
                                if (genericValue.getValue()!=null){
                                    if (genericValue.getValue().trim().equalsIgnoreCase("TRUE")){
                                        result = true;
                                    }
                                }
                                switch(operator){
                                    case "EQ":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(result));
                                        break;
                                    case "NE":
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).ne(result));
                                        break;
                                    default:
                                        criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(result));
                                        break;
                                }
                            } catch (Exception e){
                                break;
                            }
                        default:
                            //Default String
                            criteriaList.add(Criteria.where(csiQueryCriteriaKey.getCriteriaKey()).is(genericValue.getValue()));
                            break;
                    }
                }
            }
        }
        return  criteriaList;
    }

    public  Query getQuery() {
        Query query = new Query();
        query.collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()));//case insensitive search.
        System.out.println("EXECUTING QUERY " + this.getQueryName());
        List<QueryCriteriaKey> queryCriteriaKeyList = this.getQueryCriteriaKeyList();
        String logicalOperator = "AND";
        if (this.getLogicalOperator() != null) {
            logicalOperator = this.getLogicalOperator();
        }
        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = buildCriteria(queryCriteriaKeyList);
        switch (logicalOperator.trim().toUpperCase()) {
            case "AND":
                criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
                break;
            case "OR":
                criteria.orOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
                break;
            default:
                criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
                break;
        }
        query.addCriteria(criteria);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return query;
    }

    private List<Criteria> buildCriteria( List<QueryCriteriaKey> queryCriteriaKeyList){
        List<Criteria> criteriaList = new ArrayList<Criteria>();

        if (queryCriteriaKeyList!=null){
            for (int i=0;i<queryCriteriaKeyList.size();i++){
                QueryCriteriaKey queryCriteriaKey = queryCriteriaKeyList.get(i);
                String operator = "EQ";
                if (queryCriteriaKey.getOperator()!=null){
                    operator = queryCriteriaKey.getOperator();
                }
                queryCriteriaKey.setCriteriaKey(queryCriteriaKey.getCriteriaKey());
                GenericValue genericValue = queryCriteriaKey.getGenericValue();
                if (genericValue!=null && genericValue.getValueDataType()!=null && genericValue.getValue()!=null){
                    switch(genericValue.getValueDataType().trim().toUpperCase()){
                        case "**ALL**":
                            //No criteria to be applied
                            break;
                        case "STRING":
                            switch(operator){
                                case "EQ":
                                    criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(genericValue.getValue()));
                                    break;
                                case "LT":
                                    criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).lt(genericValue.getValue()));
                                    break;
                                case "LE":
                                    criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).lte(genericValue.getValue()));
                                    break;
                                case "GT":
                                    criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).gt(genericValue.getValue()));
                                    break;
                                case "GE":
                                    criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).gte(genericValue.getValue()));
                                    break;
                                case "NE":
                                    criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).ne(genericValue.getValue()));
                                    break;
                                default:
                                    criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(genericValue.getValue()));
                                    break;
                            }
                            break;
                        case "INTEGER":
                            try {
                                int intValue = Integer.parseInt(genericValue.getValue());
                                switch(operator){
                                    case "EQ":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(intValue));
                                        break;
                                    case "LT":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).lt(intValue));
                                        break;
                                    case "LE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).lte(intValue));
                                        break;
                                    case "GT":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).gt(intValue));
                                        break;
                                    case "GE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).gte(intValue));
                                        break;
                                    case "NE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).ne(intValue));
                                        break;
                                    default:
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(intValue));
                                        break;
                                }
                            } catch (Exception e){
                                break;
                            }
                            break;
                        case "DOUBLE":
                            try {
                                double doubleValue = Double.parseDouble(genericValue.getValue());
                                switch(operator){
                                    case "EQ":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(doubleValue));
                                        break;
                                    case "LT":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).lt(doubleValue));
                                        break;
                                    case "LE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).lte(doubleValue));
                                        break;
                                    case "GT":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).gt(doubleValue));
                                        break;
                                    case "GE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).gte(doubleValue));
                                        break;
                                    case "NE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).ne(doubleValue));
                                        break;
                                    default:
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(doubleValue));
                                        break;
                                }
                            } catch (Exception e){
                                break;
                            }
                            break;
                        case "FLOAT":
                            try {
                                float floatValue = Float.parseFloat(genericValue.getValue());
                                switch(operator){
                                    case "EQ":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(floatValue));
                                        break;
                                    case "LT":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).lt(floatValue));
                                        break;
                                    case "LE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).lte(floatValue));
                                        break;
                                    case "GT":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).gt(floatValue));
                                        break;
                                    case "GE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).gte(floatValue));
                                        break;
                                    case "NE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).ne(floatValue));
                                        break;
                                    default:
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(floatValue));
                                        break;
                                }
                            } catch (Exception e){
                                break;
                            }
                            break;
                        case "DATE":
                            //Date is expected in YYYMMDD format
                            try {
                                Date date = new SimpleDateFormat("yyyyMMdd").parse(genericValue.getValue());
                                switch(operator){
                                    case "EQ":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(date));
                                        break;
                                    case "LT":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).lt(date));
                                        break;
                                    case "LE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).lte(date));
                                        break;
                                    case "GT":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).gt(date));
                                        break;
                                    case "GE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).gte(date));
                                        break;
                                    case "NE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).ne(date));
                                        break;
                                    default:
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(date));
                                        break;
                                }
                            } catch (Exception e){
                                break;
                            }
                            break;
                        case "DATETIME":
                            //DateTime is expected in YYYMMDDHHMMSS format
                            try {
                                Date datetime = new SimpleDateFormat("yyyyMMddHHmmss").parse(genericValue.getValue());
                                switch(operator){
                                    case "EQ":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(datetime));
                                        break;
                                    case "LT":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).lt(datetime));
                                        break;
                                    case "LE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).lte(datetime));
                                        break;
                                    case "GT":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).gt(datetime));
                                        break;
                                    case "GE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).gte(datetime));
                                        break;
                                    case "NE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).ne(datetime));
                                        break;
                                    default:
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(datetime));
                                        break;
                                }
                            } catch (Exception e){
                                break;
                            }
                            break;
                        case "BOOLEAN":
                            try {
                                boolean result = false;
                                if (genericValue.getValue()!=null){
                                    if (genericValue.getValue().trim().equalsIgnoreCase("TRUE")){
                                        result = true;
                                    }
                                }
                                switch(operator){
                                    case "EQ":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(result));
                                        break;
                                    case "NE":
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).ne(result));
                                        break;
                                    default:
                                        criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(result));
                                        break;
                                }
                            } catch (Exception e){
                                break;
                            }
                        default:
                            //Default String
                            criteriaList.add(Criteria.where(queryCriteriaKey.getCriteriaKey()).is(genericValue.getValue()));
                            break;
                    }
                }
            }
        }
        return criteriaList;
    }

}
