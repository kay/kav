package org.neverfear.kav.main;

import static java.lang.System.out;

import org.neverfear.kav.core.AeronKeyValueClient;
import org.neverfear.kav.core.AeronService;

import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import io.aeron.samples.SamplesUtil;

public class AeronKeyValueClientMain {
    private static final boolean EMBEDDED_MEDIA_DRIVER = true;

    public static void main(String[] args) throws Exception {
        String[] keys = { "A", "B" };
        String[] values = { "Alice", "Bob", "Eva" };

        try (final MediaDriver driver = EMBEDDED_MEDIA_DRIVER ? MediaDriver.launchEmbedded() : null;) {
            Aeron.Context context = createContext(driver);

            try (AeronService aeronService = new AeronService(context)) {
                try (AeronKeyValueClient client = new AeronKeyValueClient(aeronService)) {
                    for (int i = 0; i <= Integer.MAX_VALUE; i++) {
                        String key = keys[i % keys.length];
                        String newValue = values[i % values.length];
                        try {
                            String oldValue = client.load(key);
                            out.println("Update key " + key + " from " + oldValue + " to " + newValue);
                            client.store(key, newValue);
                        } catch (Exception e) {
                            out.println(e.getMessage());
                        }
                    }
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
