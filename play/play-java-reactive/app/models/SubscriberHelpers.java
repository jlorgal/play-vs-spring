package models;

import com.mongodb.MongoTimeoutException;
import org.bson.Document;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 *  Subscriber helper implementations
 */
public final class SubscriberHelpers {

    public static class FutureSubscriber implements Subscriber<Document> {
        private Post received;
        private final CompletableFuture<Post> future;

        FutureSubscriber(CompletableFuture<Post> future) {
            this.future = future;
            received = new Post();
        }

        public void onSubscribe(final Subscription s) {
            s.request(1);  // <--- Data requested and the insertion will now occur
        }

        public void onNext(final Document success) {
            received.setId(success.get("_id", String.class));
            received.setTitle(success.get("title", String.class));
            received.setBody(success.get("body", String.class));
        }

        public void onError(final Throwable t) {
        }

        public void onComplete() {
            future.complete(received);
        }
    }

    public static <T> void subscribeAndAwait(final Publisher<T> publisher) throws Throwable {
        SubscriberHelpers.ObservableSubscriber<T> subscriber = new SubscriberHelpers.ObservableSubscriber<T>(false);
        publisher.subscribe(subscriber);
        subscriber.await();
    }
    /**
     * A Subscriber that stores the publishers results and provides a latch so can block on completion.
     *
     * @param <T> The publishers result type
     */
    public static class ObservableSubscriber<T> implements Subscriber<T> {
        private final CountDownLatch latch;
        private final List<T> results = new ArrayList<T>();
        private final boolean printResults;

        private volatile int minimumNumberOfResults;
        private volatile int counter;
        private volatile Subscription subscription;
        private volatile Throwable error;

        public ObservableSubscriber() {
            this(true);
        }

        public ObservableSubscriber(final boolean printResults) {
            this.printResults = false;
            this.latch = new CountDownLatch(1);
        }

        @Override
        public void onSubscribe(final Subscription s) {
            subscription = s;
            subscription.request(Integer.MAX_VALUE);
        }

        @Override
        public void onNext(final T t) {
            results.add(t);
            if (printResults) {
                System.out.println(t);
            }
            counter++;
            if (counter >= minimumNumberOfResults) {
                latch.countDown();
            }
        }

        @Override
        public void onError(final Throwable t) {
            error = t;
            System.out.println(t.getMessage());
            onComplete();
        }

        @Override
        public void onComplete() {
            latch.countDown();
        }

        public List<T> getResults() {
            return results;
        }

        public void await() throws Throwable {
            if (!latch.await(10, SECONDS)) {
                throw new MongoTimeoutException("Publisher timed out");
            }
            if (error != null) {
                throw error;
            }
        }

        public void waitForThenCancel(final int minimumNumberOfResults) throws Throwable {
            this.minimumNumberOfResults = minimumNumberOfResults;
            if (minimumNumberOfResults > counter) {
                await();
            }
            subscription.cancel();
        }
    }

    /**
     * A Subscriber that immediately requests Integer.MAX_VALUE onSubscribe
     *
     * @param <T> The publishers result type
     */
    public static class OperationSubscriber<T> extends ObservableSubscriber<T> {

        @Override
        public void onSubscribe(final Subscription s) {
            super.onSubscribe(s);
            s.request(Integer.MAX_VALUE);
        }
    }

    private SubscriberHelpers() {
    }
}