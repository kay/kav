package org.neverfear.kav.core;

import java.io.Closeable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.neverfear.kav.event.KeyUpdatedEvent;
import org.neverfear.kav.query.FetchKeyValueRequest;
import org.neverfear.kav.query.FetchKeyValueResponse;

public final class AeronKeyValueClient implements KeyValueStore, Closeable {
    private final AeronPublisherService requestPublisherService;
    private final AeronPublisherService eventPublisherService;
    private final AeronSubscriberService responseSubscriber;
    private final MessagePublisher<FetchKeyValueRequest> requestPublisher;
    private final MessagePublisher<KeyUpdatedEvent> eventPublisher;

    public AeronKeyValueClient(AeronService aeronService) {
        // TODO: We don't want for us to be subscribed to the response channel before sending a request
        responseSubscriber = aeronService.subscriber(
                Configuration.CLIENT_CHANNEL,
                Configuration.QUERY_RESPONSE_STREAM_ID,
                new MessageListener<>(
                        FetchKeyValueResponse.class,
                        this::onResponse));
        
        
        requestPublisherService = aeronService.publisher(
                Configuration.SERVER_CHANNEL,
                Configuration.QUERY_REQUEST_STREAM_ID);

        requestPublisher = new MessagePublisher<>(FetchKeyValueRequest.class, requestPublisherService);

        eventPublisherService = aeronService.publisher(
                Configuration.SERVER_CHANNEL,
                Configuration.EVENT_STREAM_ID);

        eventPublisher = new MessagePublisher<>(KeyUpdatedEvent.class, eventPublisherService);
    }

    // TODO: Some requests receive no responses and must also ensure when
    // called from multiple threads that we receive them in the same order
    // we sent
    private final BlockingQueue<FetchKeyValueResponse> responseQueue = new LinkedBlockingQueue<>();

    private void onResponse(FetchKeyValueResponse response) {
        responseQueue.offer(response);
    }

    @Override
    public void close() {
        try (AeronSubscriberService c1 = this.responseSubscriber;
                AeronPublisherService c2 = this.requestPublisherService;
                AeronPublisherService c3 = this.eventPublisherService;) {
            /*
             * close all using try-with-resources so that we get all the
             * suppressed exceptions if needed
             */
        }
    }

    @Override
    public String load(String key) {
        // TODO: This request/response blocking protocol impl is rubbish
        try {
            requestPublisher.publish(new FetchKeyValueRequest(key));
            FetchKeyValueResponse response = responseQueue.take();
            return response.getValue();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new AssertionError(e);
        } catch (PublishException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void store(String key, String value) {
        try {
            eventPublisher.publish(new KeyUpdatedEvent(key, value));
        } catch (PublishException e) {
            throw new IllegalStateException(e);
        }
    }
}