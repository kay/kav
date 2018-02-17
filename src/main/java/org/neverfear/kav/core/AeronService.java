package org.neverfear.kav.core;

import java.io.Closeable;
import java.io.IOException;

import io.aeron.Aeron;
import io.aeron.Aeron.Context;
import io.aeron.logbuffer.FragmentHandler;

public class AeronService implements Closeable {

    private final Aeron aeron;

    public AeronService(Context context) {
        this.aeron = Aeron.connect(context);
    }

    public AeronSubscriberService subscriber(String channel, int streamId, FragmentHandler fragmentHandler) {
        if (this.aeron.isClosed()) {
            throw new IllegalStateException("Closed");
        }
        return new AeronSubscriberService(this.aeron, channel, streamId, fragmentHandler);
    }

    public AeronPublisherService publisher(String channel, int streamId) {
        if (this.aeron.isClosed()) {
            throw new IllegalStateException("Closed");
        }
        return new AeronPublisherService(this.aeron, channel, streamId);
    }

    @Override
    public void close() throws IOException {
        if (this.aeron.isClosed()) {
            throw new IllegalStateException("Closed");
        }
        this.aeron.close();
    }

}
