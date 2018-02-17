package org.neverfear.kav.core;

import static java.lang.System.out;

public class MessagePublisher<T> {
    private final GsonCodec<T> codec;
    private final AeronPublisherService publisherService;

    public MessagePublisher(Class<T> type, AeronPublisherService publisherService) {
        this.codec = new GsonCodec<>(type);
        this.publisherService = publisherService;
    }

    public void publish(T message) throws PublishException {
        byte[] data = codec.encode(message);
        publisherService.publish(data);
        out.println("Published: " + message);
    }
}
