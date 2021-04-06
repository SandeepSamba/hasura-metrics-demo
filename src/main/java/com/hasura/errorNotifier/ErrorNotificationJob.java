package com.hasura.errorNotifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasura.dto.OperationLog;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ErrorNotificationJob {

    private final ErrorFetchingService errorFetchingService;
    private final SlackNotifier slackNotifier;
    private final ObjectMapper mapper;

    public static final int POLL_DURATION = 10000;

    @SneakyThrows
    @Scheduled(fixedDelay = POLL_DURATION)
    public void notifyDownstream() {
        List<OperationLog> errorLogs = errorFetchingService.fetchErrors();

        if (!CollectionUtils.isEmpty(errorLogs)) {
            for (OperationLog errorLog : errorLogs) {
                String message = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorLog);
                slackNotifier.notifyClient(message);
            }
        }
    }
}
