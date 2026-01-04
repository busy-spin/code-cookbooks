package io.github.busy_spin.artio.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Slf4j
public final class EnvToProps {

    private static final List<String> STANDARD_ENV_PREFIXES = Arrays.asList("AERON", "ARTIO");

    private EnvToProps() {
    }

    public static void loadPropsFromEnv() {
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
    }

}
