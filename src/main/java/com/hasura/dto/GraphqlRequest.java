package com.hasura.dto;

import lombok.Data;

import java.util.Map;

@Data
public class GraphqlRequest {
    private String operationName;
    private String query;
    private Map<String, Object> variables;
}
