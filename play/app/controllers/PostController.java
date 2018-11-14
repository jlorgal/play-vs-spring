package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Post;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.Util;

public class PostController extends Controller {

    private static Post postInstance;

    public static Post getInstance() {
        if (postInstance == null) {
            postInstance = new Post();
        }
        return postInstance;
    }

    public Result create() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest(Util.createResponse("Expecting Json data", false));
        }
        Post post = (Post) Json.fromJson(json, Post.class);
        post.save();
        JsonNode jsonObject = Json.toJson(post);
        return created(Util.createResponse(jsonObject, true));
    }

    public Result retrieve(String id) {
        this.getInstance().setId(id);
        this.getInstance().query();
        if (postInstance.getId().equals("")) {
            return notFound(Util.createResponse("Student with id:" + id + " not found", false));
        }
        JsonNode jsonObjects = Json.toJson(postInstance);
        return ok(Util.createResponse(jsonObjects, true));
    }
}

