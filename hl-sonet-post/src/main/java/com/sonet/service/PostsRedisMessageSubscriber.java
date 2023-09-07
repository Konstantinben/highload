package com.sonet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonet.mapper.PostMapper;
import com.sonet.model.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static com.sonet.configuration.WebSocketConfig.DESTINATION_POSTFIX;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostsRedisMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    private final PostMapper postMapper;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Message received: " + message.toString());
        try {
            var post = objectMapper.readValue(message.getBody(), Post.class);

            // add config.setUserDestinationPrefix("/user");
            simpMessagingTemplate.convertAndSendToUser(
                    post.getUserUuid().toString(),
                    DESTINATION_POSTFIX,
                    postMapper.toPostDto(post));
        } catch (Exception e) {
            log.error("Cannot process post " + message, e);
        }
    }
}
