package org.neverfear.kav.core;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import org.agrona.concurrent.BackoffIdleStrategy;
import org.agrona.concurrent.IdleStrategy;

import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;

public class AeronSubscriberService implements Closeable {
    private final FragmentHandler fragmentHandler;
    private final Thread thread;
    private final Subscription subscription;

    private volatile boolean shouldRun;

    public AeronSubscriberService(Aeron aeron, String channel, int streamId, FragmentHandler fragmentHandler) {
        this.fragmentHandler = fragmentHandler;

        this.subscription = aeron.addSubscription(channel, streamId);

        this.shouldRun = true;
        this.thread = new Thread(this::run, "subscriber-" + streamId);
        
        this.thread.start();
    }

    private void run() {
        final IdleStrategy idleStrategy = new BackoffIdleStrategy(
                100,
                10,
                TimeUnit.MICROSECONDS.toNanos(1),
                TimeUnit.MICROSECONDS.toNanos(100));

        while (this.shouldRun) {
            final int fragmentsRead = this.subscription.poll(this.fragmentHandler, 10);
            idleStrategy.idle(fragmentsRead);
        }
    }

    @Override
    public void close() {
        if (this.subscription.isClosed()) {
            throw new IllegalStateException("Already closed");
        }

        this.shouldRun = false;
        this.thread.interrupt();

        this.subscription.close();
    }

}
