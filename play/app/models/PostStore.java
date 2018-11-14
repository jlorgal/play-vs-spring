package models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PostStore {
    private static PostStore instance;
    private Map<Integer, Post> posts = new HashMap<>();

    public static PostStore getInstance() {
        if (instance == null) {
            instance = new PostStore();
        }
        return instance;
    }

    public Post addPost(Post post) {
        int id = posts.size();
        post.setId(Integer.toString(id));
        posts.put(id, post);
        return post;
    }

    public Post getPost(int id) {
        return posts.get(id);
    }
}

