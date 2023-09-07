package com.sonet.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonet.core.model.entity.Post;
import com.sonet.core.repository.UserReadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostsRedisMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    private final RedisService redisService;

    private final UserReadRepository userReadRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Message received: " + message.toString());
        try {
            Post post = objectMapper.readValue(message.getBody(), Post.class);
            var userList = userReadRepository.findFriendsById(post.getUserId());
            redisService.addPostToUsersFeeds(userList, post);
        } catch (Exception e) {
            log.error("Cannot process post " + message, e);
        }
    }
}
