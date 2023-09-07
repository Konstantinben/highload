package com.sonet.core.service;

import com.sonet.core.model.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessagePublisher implements MessagePublisher<Post> {

    private final RedisTemplate<String, Post> postRedisTemplate;
    private final ChannelTopic postsTopic;


    public void publish(Post post) {
        postRedisTemplate.convertAndSend(postsTopic.getTopic(), post);
    }
}
