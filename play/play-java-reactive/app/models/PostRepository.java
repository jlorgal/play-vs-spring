package models;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface PostRepository {

    CompletionStage<Post> create(Post postData);

    CompletionStage<Post> get(String id);

}
