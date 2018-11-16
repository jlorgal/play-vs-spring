package models;

import com.mongodb.ConnectionString;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.reactivestreams.client.*;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.mongodb.client.model.Filters.eq;
import static models.SubscriberHelpers.subscribeAndAwait;

public class MongoPostRepository implements PostRepository {
    // Open the client
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;

    MongoPostRepository() {
        // or provide custom MongoClientSettings
        ClusterSettings clusterSettings = ClusterSettings.builder().hosts(java.util.Arrays.asList(new ServerAddress("192.168.99.100"))).build();

        com.mongodb.connection.ConnectionPoolSettings.Builder poolBuilder = com.mongodb.connection.ConnectionPoolSettings.builder();
        poolBuilder.maxSize(100);
        poolBuilder.minSize(100);
        ConnectionPoolSettings poolSettings = poolBuilder.build();

        MongoClientSettings.Builder mongoSettingsBuilder = MongoClientSettings.builder();
        mongoSettingsBuilder.connectionPoolSettings(poolSettings);

        MongoClientSettings mongoClientSettings = mongoSettingsBuilder.clusterSettings(clusterSettings).build();
        MongoClient mongoClient = MongoClients.create(mongoClientSettings);

        mongoDatabase = mongoClient.getDatabase("play-java-reactive");
        //mongoClient =  MongoClients.create(new ConnectionString("mongodb://192.168.99.100:27017"));

        try {
            subscribeAndAwait(mongoDatabase.createCollection("posts"));
        } catch (Throwable throwable) {
        }

        mongoCollection = mongoDatabase.getCollection("posts");
    }

    public CompletionStage<Post> create(Post post) {
        ObjectId oid = new ObjectId();
        post.setId(oid.toString());
        Document document = new Document();
        document.append("_id", post.getId());
        document.append("title", post.getTitle());
        document.append("body", post.getBody());

        SubscriberHelpers.OperationSubscriber<Success> subscriber = new SubscriberHelpers.OperationSubscriber<Success>();
        mongoCollection.insertOne(document).subscribe(subscriber);
        try {
            subscriber.await();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return get(post.getId());
    }

    public CompletionStage<Post> get(String id) {
        final CompletableFuture<Post> future = new CompletableFuture<>();

        mongoCollection.find(eq("_id", id)).first().subscribe(new SubscriberHelpers.FutureSubscriber(future));
        return future;
    }
}
