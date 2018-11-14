package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Post;
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
        Post post = (Post) Json.fromJson(json, Post.class);
        post.save();
        JsonNode jsonObject = Json.toJson(post);
        return created(Util.createResponse(jsonObject, true));
    }

    public Result retrieve(String id) {
        Post post = new Post(id,null,null);
        post.query();
        if (post.getId().equals("")) {
            return notFound(Util.createResponse("Student with id:" + id + " not found", false));
        }
        JsonNode jsonObjects = Json.toJson(post);
        return ok(Util.createResponse(jsonObjects, true));
    }
}
