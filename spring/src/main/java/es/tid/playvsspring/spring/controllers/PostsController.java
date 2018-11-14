/*
 * Copyright (c) Telefonica I+D. All rights reserved.
 */

package es.tid.playvsspring.spring.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.tid.playvsspring.spring.model.Post;
import es.tid.playvsspring.spring.repositories.PostsRepository;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/posts")
public class PostsController {
	
	private PostsRepository postsRepository;
	
	public PostsController(PostsRepository postsRepository) {
		this.postsRepository = postsRepository;
	}
	
	@PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Mono<Post> insertPost(@RequestBody Post post) {
		return postsRepository.save(post);
	}

	@GetMapping(value = "/{id}")
	public Mono<Post> getPost(@PathVariable(value = "id") String id) {
		return postsRepository.findById(id);
	}
}
