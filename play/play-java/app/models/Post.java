package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import static util.MongoConfig.datastore;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(value = "posts", noClassnameStored = true)
public class Post {
    @Id
    private String id;

    private String title;

    private String body;

    public void save() {
        setId(new ObjectId().toString());
        datastore().save(this);
    }

    public void query() {
        Post returned = datastore().get(Post.class, id);
        if (returned == null) {
            this.setId("");
            return;
        }
        this.setBody(returned.getBody());
        this.setTitle(returned.getTitle());
    }
}
