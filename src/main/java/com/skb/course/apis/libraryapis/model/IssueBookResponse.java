package com.skb.course.apis.libraryapis.model;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Map;

public class IssueBookResponse {

    private Map<Integer, IssueBookStatus> issueBookStatusMap;

    public IssueBookResponse() {
    }

    public IssueBookResponse(Map<Integer, IssueBookStatus> issueBookStatusMap) {
        this.issueBookStatusMap = issueBookStatusMap;
    }

    public Map<Integer, IssueBookStatus> getIssueBookStatusMap() {
        return issueBookStatusMap;
    }

    public void setIssueBookStatusMap(Map<Integer, IssueBookStatus> issueBookStatusMap) {
        this.issueBookStatusMap = issueBookStatusMap;
    }
}

