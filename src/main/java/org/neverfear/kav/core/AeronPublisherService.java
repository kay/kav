package org.neverfear.kav.core;

import java.io.Closeable;
import java.nio.ByteBuffer;

import org.agrona.concurrent.UnsafeBuffer;

import io.aeron.Aeron;
import io.aeron.Publication;

public class AeronPublisherService implements Closeable {
    private final UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(256));
    private final Publication publication;

    public AeronPublisherService(Aeron aeron, String channel, int streamId) {
        this.publication = aeron.addPublication(channel, streamId);
    }

    public boolean publish(byte[] messageBytes) throws PublishException {
        if (publication.isClosed()) {
            throw new IllegalStateException("Closed");
        }

        buffer.putBytes(0, messageBytes);
        final long result = publication.offer(buffer, 0, messageBytes.length);
        if (result < 0L) {
            if (result == Publication.BACK_PRESSURED) {
                throw new PublishException("Offer failed due to back pressure");
            } else if (result == Publication.NOT_CONNECTED) {
                throw new PublishException("Offer failed because publisher is not connected to subscriber");
            } else if (result == Publication.ADMIN_ACTION) {
                throw new PublishException("Offer failed because of an administration action in the system");
            } else if (result == Publication.CLOSED) {
                throw new PublishException("Offer failed publication is closed");
            } else if (result == Publication.MAX_POSITION_EXCEEDED) {
                throw new PublishException("Offer failed due to publication reaching max position");
            } else {
                throw new PublishException("Offer failed due to unknown reason");
            }
        }

        return publication.isConnected();
    }

    @Override
    public void close() {
        if (publication.isClosed()) {
            throw new IllegalStateException("Closed");
        }
        publication.close();
    }

}
