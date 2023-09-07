package com.sonet.mapper;

import com.sonet.model.dto.PostDto;
import com.sonet.model.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel =  "spring")
public interface PostMapper {
    PostDto toPostDto(Post post);
}
