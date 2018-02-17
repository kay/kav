package org.neverfear.kav.core;

final class Configuration {
    static final int EVENT_STREAM_ID = 1;
    static final int QUERY_REQUEST_STREAM_ID = 2;
    static final int QUERY_RESPONSE_STREAM_ID = 3;

    static final String SERVER_CHANNEL = "aeron:udp?endpoint=localhost:40123";
    static final String CLIENT_CHANNEL = "aeron:udp?endpoint=localhost:40124";

    private Configuration() {
    }
}
