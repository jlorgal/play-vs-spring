/*
 * Copyright (c) Telefonica I+D. All rights reserved.
 */

package es.tid.playvsspring.spring.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "posts")
@Data
public class Post {
	@Id
	private String id;
	
	private String title;
	
	private String body;
}
