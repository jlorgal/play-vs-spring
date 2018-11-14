// Copyright (c) Telefonica I+D. All rights reserved.

package es.tid.playvsspring.spring.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import es.tid.playvsspring.spring.model.Post;

@Repository
public interface PostsRepository extends ReactiveMongoRepository<Post, String> {
}
