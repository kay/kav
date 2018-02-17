package org.neverfear.kav.main;

import java.util.concurrent.TimeUnit;

import org.neverfear.kav.core.AeronKeyValueServer;
import org.neverfear.kav.core.AeronService;
import org.neverfear.kav.core.HashKeyValueStore;
import org.neverfear.kav.core.KeyValueStore;

import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import io.aeron.samples.SamplesUtil;

public class AeronKeyValueServerMain {
    private static final boolean EMBEDDED_MEDIA_DRIVER = true;

    public static void main(String[] args) throws Exception {

        KeyValueStore store = new HashKeyValueStore();

        try (final MediaDriver driver = EMBEDDED_MEDIA_DRIVER ? MediaDriver.launchEmbedded() : null;) {
            Aeron.Context context = createContext(driver);

            try (AeronService aeronService = new AeronService(context)) {
                try (AeronKeyValueServer server = new AeronKeyValueServer(store, aeronService)) {
                    TimeUnit.DAYS.sleep(7);
                }
            }
        }

    }

    private static Aeron.Context createContext(final MediaDriver driver) {
        Aeron.Context context = new Aeron.Context();
        if (EMBEDDED_MEDIA_DRIVER) {
            context.aeronDirectoryName(driver.aeronDirectoryName());
        }
        context.availableImageHandler(SamplesUtil::printAvailableImage);
        context.unavailableImageHandler(SamplesUtil::printUnavailableImage);
        return context;
    }
}
