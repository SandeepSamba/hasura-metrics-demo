package com.hasura.errorNotifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasura.dto.GraphqlRequest;
import com.hasura.dto.OperationLog;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hasura.errorNotifier.ErrorNotificationJob.POLL_DURATION;

@Service
@RequiredArgsConstructor
public class ErrorFetchingService {

    @Value("${hasura.metrics.url}")
    private String hasuraMetricsUrl;

    @Value("${hasura.metrics.admin-secret}")
    private String hasuraMetricsAdminSecret;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private ZonedDateTime to;
    private ZonedDateTime from;

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX";

    @SneakyThrows
    public List<OperationLog> fetchErrors() {
        GraphqlRequest getErrorsRequest = new GraphqlRequest();
        getErrorsRequest.setOperationName("GetOperationLogs");
        getErrorsRequest.setQuery("query GetOperationLogs($gte: timestamptz!, $lte: timestamptz!) {\n" +
                "  operation_logs(where: {_and: [{is_error: {_eq: true}}, {time: {_gte: $gte}}, {time: {_lte: $lte}}]}) {\n" +
                "    error\n" +
                "    error_code\n" +
                "    time\n" +
                "    request_id\n" +
                "    user_role\n" +
                "    user_vars\n" +
                "    client_name\n" +
                "    operation_type\n" +
                "    operation_id\n" +
                "    query\n" +
                "  }\n" +
                "}\n");

        to = ZonedDateTime.now();
        if (from == null) {
            from = to.minusSeconds(POLL_DURATION / 1000);
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("gte", from.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        variables.put("lte", to.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        getErrorsRequest.setVariables(variables);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-hasura-admin-secret", hasuraMetricsAdminSecret);
        HttpEntity<GraphqlRequest> getErrorsEntity = new HttpEntity<>(getErrorsRequest, httpHeaders);
        String errors = restTemplate.postForObject(hasuraMetricsUrl, getErrorsEntity, String.class);
        List<OperationLog> errorLogs = Arrays.asList(mapper.treeToValue(
                mapper.readTree(errors).get("data").get("operation_logs"), OperationLog[].class));

        from = to;
        return errorLogs;
    }
}