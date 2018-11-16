package controllers;

import com.palominolabs.http.url.UrlBuilder;
import models.Post;
import models.PostRepository;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;

import javax.inject.Inject;
import java.nio.charset.CharacterCodingException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * Handles presentation of Post resources, which map to JSON.
 */
public class PostResourceHandler {

    private final PostRepository repository;
    private final HttpExecutionContext ec;

    @Inject
    public PostResourceHandler(PostRepository repository, HttpExecutionContext ec) {
        this.repository = repository;
        this.ec = ec;
    }

    public CompletionStage<Post> create(Post resource) {
        final Post data = new Post(resource.getId(), resource.getTitle(), resource.getBody());
        return repository.create(data).thenApplyAsync(savedData -> {
            return new Post(savedData.getId(), savedData.getTitle(), savedData.getBody());
        }, ec.current());
    }

    public CompletionStage<Post> lookup(String id) {
        return repository.get(id).thenApplyAsync(savedData -> {
            return new Post(savedData.getId(), savedData.getTitle(), savedData.getBody());
        }, ec.current());
    }
}

