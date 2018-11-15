package util;

import com.mongodb.MongoClient;
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

        MongoClient mongoClient = new MongoClient(
                ConfigFactory.load().getString("mongodb.host"),
                ConfigFactory.load().getInt("mongodb.port"));

        datastore = morphia.createDatastore(
                mongoClient, ConfigFactory.load().getString("mongodb.database"));
    }

}
