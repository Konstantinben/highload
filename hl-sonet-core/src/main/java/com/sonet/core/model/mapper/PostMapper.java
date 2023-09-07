package com.sonet.core.model.mapper;

import com.sonet.core.model.dto.PostDto;
import com.sonet.core.model.entity.Post;
import org.mapstruct.*;

@Mapper(componentModel =  "spring")
public interface PostMapper {

    @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID())")
    Post toPost(PostDto postDto);

    PostDto toPostDto(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdDate", ignore = true)
    Post toPost(@MappingTarget Post post, PostDto postDto);
}
