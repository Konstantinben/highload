package com.sonet.core.repository;

import com.sonet.core.model.entity.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository  extends CrudRepository<Post, Long> {
}
