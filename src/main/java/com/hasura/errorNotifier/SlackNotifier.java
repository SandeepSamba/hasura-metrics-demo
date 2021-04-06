package com.hasura.errorNotifier;

import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("SlackNotifier")
@RequiredArgsConstructor
public class SlackNotifier {

    @Value("${slack.channel-id}")
    private String slackChannelId;

    private final MethodsClient methodsClient;

    @SneakyThrows
    public void notifyClient(String message) {
        String slackMessage = "*Error occurred.* \n" +
                "``` " + message + "``` ";
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(slackChannelId)
                .mrkdwn(true)
                .text(slackMessage)
                .build();
        ChatPostMessageResponse response = methodsClient.chatPostMessage(request);
    }
}
