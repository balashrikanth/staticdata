package com.stonex.corp.payments.staticdata.controller;

import com.stonex.corp.payments.staticdata.dto.PageResponse;
import com.stonex.corp.payments.staticdata.model.StaticData;
import com.stonex.corp.payments.staticdata.utils.FilterBuilderService;
import com.stonex.corp.payments.staticdata.utils.FilterCondition;
import com.stonex.corp.payments.staticdata.utils.GenericFilterCriteriaBuilder;
import com.stonex.corp.payments.staticdata.utils.StaticDataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Filter;

@RestController
@RequestMapping("/staticdata/search")
public class SearchAndFilterController {


    //private final StaticDataService staticDataService;
    private final FilterBuilderService filterBuilderService;

    public SearchAndFilterController(FilterBuilderService filterBuilderService) {
        this.filterBuilderService = filterBuilderService;
    }

    /**
     * @param page      page number
     * @param size      size count
     * @param filterOr  string filter or conditions
     * @param filterAnd string filter and conditions
     * @param orders    string orders
     * @return PageResponse<Employee>
     */
    @GetMapping(value = "/page")
    public ResponseEntity<PageResponse<StaticData>> getSearchCriteriaPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "filterOr", required = false) String filterOr,
            @RequestParam(value = "filterAnd", required = false) String filterAnd,
            @RequestParam(value = "orders", required = false) String orders) {

        PageResponse<StaticData> response = new PageResponse<>();

        Pageable pageable = filterBuilderService.getPageable(size, page, orders);
        GenericFilterCriteriaBuilder filterCriteriaBuilder = new GenericFilterCriteriaBuilder();


        List<FilterCondition> andConditions = filterBuilderService.createFilterCondition(filterAnd);
        List<FilterCondition> orConditions = filterBuilderService.createFilterCondition(filterOr);

        Query query = filterCriteriaBuilder.addCondition(andConditions, orConditions);
        //Page<StaticData> pg = .getPage(query, pageable);
        //response.setPageStats(pg, pg.getContent());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
