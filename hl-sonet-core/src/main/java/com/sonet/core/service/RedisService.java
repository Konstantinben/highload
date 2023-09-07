package com.sonet.core.service;

import com.sonet.core.model.entity.Post;
import com.sonet.core.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    @Value("${app.redis.cache.bulk-update-size}")
    private int redisBulkUpdateSize;

    @Value("${app.redis.cache.feed-size}")
    private int feedSize;

    private final RedisTemplate<String, Post> postRedisTemplate;

    public long addPostToUsersFeeds(List<User> users, Post post) {
        long totalCounter = 0;
        var batches = new ArrayList<>(ListUtils.partition(users, redisBulkUpdateSize));
        for(var batch : batches) {
            for(var userEntry : batch) {
                try {
                    totalCounter += addAddPostToUserFeed(userEntry, post);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return totalCounter;
    }

    public long addAddPostToUserFeed(User user, Post post) {
        List<Object> txResults = null;
        try {
            txResults = postRedisTemplate.execute(new SessionCallback<>() {
                public List<Object> execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.opsForList().leftPush(user.getUuid().toString(), post);
                    operations.opsForList().trim(user.getUuid().toString(), 0, feedSize);
                    // This will contain the results of all operations in the transaction
                    return operations.exec();
                }
            });
        } catch (Exception e) {
            log.error("Cannot update feed for user " + user.getId(), e);
        }
        return txResults != null ? (long)txResults.get(0) : 0;
    }

    public List<Post> getUserPosts(User user) {
        List<Post> feeds = postRedisTemplate.opsForList().range(user.getUuid().toString(), 0, -1);
        if (CollectionUtils.isEmpty(feeds)) {
            return Collections.EMPTY_LIST;
        }
        return feeds;
    }
}
