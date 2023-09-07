package com.sonet.core.controller;

import com.sonet.core.model.dto.PostDto;
import com.sonet.core.model.entity.Post;
import com.sonet.core.model.entity.User;
import com.sonet.core.model.mapper.PostMapper;
import com.sonet.core.repository.PostReadRepository;
import com.sonet.core.repository.PostRepository;
import com.sonet.core.repository.UserReadRepository;
import com.sonet.core.security.UserSessionUtil;
import com.sonet.core.service.RedisMessagePublisher;
import com.sonet.core.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "PostController", description = "Контроллер постов")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final UserSessionUtil userSessionUtil;

    private final PostRepository postRepository;

    private final PostReadRepository postReadRepository;
    private final UserReadRepository userReadRepository;

    private final PostMapper postMapper;

    private final RedisService redisService;

    private final RedisMessagePublisher redisMessagePublisher;


    @PutMapping("/create/")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Создать пост")
    public PostDto add(@RequestBody @Valid PostDto postDto) {
        User user = userSessionUtil.getAuthorizedUser();
        Post postToInsert = postMapper.toPost(postDto);
        postToInsert.setUserId(user.getId());
        postToInsert.setUserUuid(user.getUuid());
        postToInsert.setUserFirstName(user.getFirstName());
        postToInsert.setUserLastName(user.getLastName());
        postToInsert.setCreatedDate(new Date());
        Post post = postRepository.save(postToInsert);

        redisMessagePublisher.publish(post);
        //var userList = userReadRepository.findFriendsById(user.getId());
        //redisService.addPostToUsersFeeds(userList, post);

        return postMapper.toPostDto(post);
    }

    @GetMapping("/get/{uuid}")
    @Operation(summary = "Прочитать пост")
    public PostDto getByUuid(@PathVariable("uuid") UUID postUuid) {
        return postReadRepository
                .findByUuid(postUuid)
                .map(postMapper::toPostDto)
                .orElseThrow(() -> new BadCredentialsException("Cannod find post by uuid" + postUuid));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Отредактировать пост")
    public PostDto updateProfile(@RequestBody @Valid PostDto postDto) {
        User user = userSessionUtil.getAuthorizedUser();
        Post post = postReadRepository.findByUuid(postDto.getUuid()).orElseThrow(
                () -> new BadCredentialsException("Cannot find post by uuid" + postDto.getUuid())
        );
        if (!user.getUuid().equals(post.getUserUuid())) {
            throw new BadCredentialsException("Cannot find post " + post.getUuid() + " for user " + user.getUuid());
        }

        Post postToUpdate = postMapper.toPost(post, postDto);
        postToUpdate.setUpdatedDate(new Date());
        Post updatedPost = postRepository.save(post);

        return postMapper.toPostDto(updatedPost);
    }

    @DeleteMapping("/dlete/{uuid}")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Удалить пост")
    public UUID delete(@PathVariable("user_id") UUID postUuid) {
        User user = userSessionUtil.getAuthorizedUser();
        Post post = postReadRepository.findByUuid(postUuid).orElseThrow(
                () -> new BadCredentialsException("Cannot find post by uuid" + postUuid)
        );
        if (!user.getUuid().equals(post.getUserUuid())) {
            throw new BadCredentialsException("Cannot find post " + post.getUuid() + " for user " + user.getUuid());
        }
        postRepository.delete(post);
        return postUuid;
    }

    @GetMapping("/feed")
    @PreAuthorize("hasAuthority('users:read')")
    @Operation(summary = "Лента постов друзей")
    public List<PostDto> getFeed() {
        List<PostDto> cachedPosts = redisService.getUserPosts(userSessionUtil.getAuthorizedUser())
                .stream().map(postMapper::toPostDto)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cachedPosts)) {
            // TODO
        }
        return cachedPosts;
    }
}
