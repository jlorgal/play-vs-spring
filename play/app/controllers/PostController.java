package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Post;
import models.PostStore;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.Util;

public class PostController extends Controller {

    public Result create() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest(Util.createResponse("Expecting Json data", false));
        }
        Post post = PostStore.getInstance().addPost(Json.fromJson(json, Post.class));
        JsonNode jsonObject = Json.toJson(post);
        return created(Util.createResponse(jsonObject, true));
    }

    public Result retrieve(int id) {
        if (PostStore.getInstance().getPost(id) == null) {
            return notFound(Util.createResponse("Student with id:" + id + " not found", false));
        }
        JsonNode jsonObjects = Json.toJson(PostStore.getInstance().getPost(id));
        return ok(Util.createResponse(jsonObjects, true));
    }
}

