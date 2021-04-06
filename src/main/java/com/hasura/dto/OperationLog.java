package com.hasura.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OperationLog {

    private Error error;
    private String error_code;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")
    private ZonedDateTime time;

    private String request_id;
    private String user_role;
    private Map<String, Object> user_vars;
    private String client_name;
    private String operation_type;
    private String operation_id;
    private Query query;

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class Error {
        private String code;
        private String path;
        private String error;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class Query {
        private String query;
        private String mutation;
        private String subscription;
    }
}
