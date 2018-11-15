package util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.typesafe.config.ConfigFactory;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class MongoConfig {

    private static Datastore datastore;

    public static Datastore datastore() {
        if (datastore == null) {
            initDatastore();
        }
        return datastore;
    }

    public static void initDatastore() {

        final Morphia morphia = new Morphia();

        // Tell Morphia where to find our models
        morphia.mapPackage("models");

        MongoClientOptions options = MongoClientOptions.builder()
            .connectionsPerHost(ConfigFactory.load().getInt("mongodb.connectionsPerHost"))
            .minConnectionsPerHost(ConfigFactory.load().getInt("mongodb.minConnectionsPerHost"))
            .build();

        MongoClient mongoClient = new MongoClient(
            new ServerAddress(
                ConfigFactory.load().getString("mongodb.host"),
                ConfigFactory.load().getInt("mongodb.port")
            ),
            options
        );

        datastore = morphia.createDatastore(
                mongoClient, "play-java");
    }

}
