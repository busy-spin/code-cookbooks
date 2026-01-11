package io.github.busy_spin.artio.utils;

import io.aeron.CommonContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.aeron.CommonContext.AERON_DIR_PROP_NAME;

@Slf4j
public final class EnvToProps {

    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final List<String> STANDARD_ENV_PREFIXES = Arrays.asList("AERON", "ARTIO");

    private EnvToProps() {
    }

    public static void initialize() {
        if (initialized.compareAndSet(false, true)) {
            Properties properties = new Properties();
            System.getenv().forEach((key, value) -> {
                for (String prefix : STANDARD_ENV_PREFIXES) {
                    if (key.startsWith(prefix)) {
                        String propKey = key.toLowerCase().replaceAll("_", ".");
                        properties.setProperty(propKey, value);
                        log.info("set env property {}={}", propKey, value);
                    }
                }
            });
            System.getProperties().putAll(properties);

            Integer mdNumber = Integer.getInteger("aeron.media.driver.number");
            System.setProperty(AERON_DIR_PROP_NAME, CommonContext.getAeronDirectoryName() + "-" + mdNumber);
            log.info("Initialized the system properties");
        } else {
            log.info("EnvToProps already initialized");
        }
    }
}
