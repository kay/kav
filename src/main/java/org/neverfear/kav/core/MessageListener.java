package org.neverfear.kav.core;

import static java.lang.System.out;

import org.agrona.DirectBuffer;

import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;

public final class MessageListener<T> implements FragmentHandler {
    interface Listener<T> {
        void onMessage(T message);
    }

    private final GsonCodec<T> codec;
    private final Listener<T> listener;

    public MessageListener(Class<T> type, Listener<T> listener) {
        codec = new GsonCodec<>(type);
        this.listener = listener;
    }

    @Override
    public final void onFragment(DirectBuffer buffer, int offset, int length, Header header) {
        final byte[] data = new byte[length];
        buffer.getBytes(offset, data);

        T message = codec.decode(data);

        out.println("Received: " + message);
        listener.onMessage(message);
    }

}