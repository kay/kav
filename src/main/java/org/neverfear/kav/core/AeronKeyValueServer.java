package org.neverfear.kav.core;

import static java.lang.System.out;

import java.io.Closeable;

import org.neverfear.kav.event.KeyUpdatedEvent;
import org.neverfear.kav.query.FetchKeyValueRequest;
import org.neverfear.kav.query.FetchKeyValueResponse;

public class AeronKeyValueServer implements Closeable {
    private final KeyValueStore store;

    private final AeronPublisherService responsePublisherService;
    private final MessagePublisher<FetchKeyValueResponse> responsePublisher;
    private final AeronSubscriberService requestSubscriber;
    private final AeronSubscriberService eventSubscriber;

    public AeronKeyValueServer(KeyValueStore store, AeronService aeronService) {
        this.store = store;

        this.responsePublisherService = aeronService.publisher(
                Configuration.CLIENT_CHANNEL,
                Configuration.QUERY_RESPONSE_STREAM_ID);

        this.responsePublisher = new MessagePublisher<>(FetchKeyValueResponse.class, this.responsePublisherService);

        this.requestSubscriber = aeronService.subscriber(
                Configuration.SERVER_CHANNEL,
                Configuration.QUERY_REQUEST_STREAM_ID,
                new MessageListener<>(FetchKeyValueRequest.class, this::onRequest));

        this.eventSubscriber = aeronService.subscriber(
                Configuration.SERVER_CHANNEL,
                Configuration.EVENT_STREAM_ID,
                new MessageListener<>(KeyUpdatedEvent.class, this::onEvent));
    }

    private void onRequest(FetchKeyValueRequest request) {
        final String key = request.getKey();
        final String value = this.store.load(key);
        final FetchKeyValueResponse response = new FetchKeyValueResponse(key, value);
        try {
            this.responsePublisher.publish(response);
        } catch (final PublishException e) {
            out.println(e.getMessage());
        }
    }

    private void onEvent(KeyUpdatedEvent event) {
        final String key = event.getKey();
        final String value = event.getValue();
        this.store.store(key, value);
    }

    @Override
    public void close() {
        try (AeronSubscriberService c1 = this.requestSubscriber;
                AeronSubscriberService c2 = this.eventSubscriber;
                AeronPublisherService c3 = this.responsePublisherService;) {
            /*
             * close all using try-with-resources so that we get all the
             * suppressed exceptions if needed
             */
        }
    }

}
