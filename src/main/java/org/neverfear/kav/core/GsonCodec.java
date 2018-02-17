package org.neverfear.kav.core;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonCodec<T> {
    private static class Message {
        private final int type;
        private final String payload;

        public Message(int type, String payload) {
            super();
            this.type = type;
            this.payload = payload;
        }

    }

    private static final Gson GSON = new GsonBuilder().create();
    private final Class<T> type;

    public GsonCodec(Class<T> type) {
        super();
        this.type = type;
    }

    public byte[] encode(T object) {
        String payload = GSON.toJson(object);
        return GSON.toJson(new Message(42, payload)).getBytes();
    }

    public T decode(byte[] data) {
        Message message = GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(data)), Message.class);
        if (message.type != 42) {
            throw new IllegalArgumentException();
        }
        return GSON.fromJson(message.payload, type);
    }
}
