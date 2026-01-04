package io.github.busy_spin.artio.utils;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class FileUtil {

    private enum Location {

        /**
         * Load from the filesystem.
         */
        FILESYSTEM,

        /*
         * Load from the thread's context class loader
         */
        CONTEXT_RESOURCE,

        /**
         * Load a class resource
         */
        CLASS_RESOURCE,

        /**
         * Load a resource from the classpath
         */
        CLASSLOADER_RESOURCE,

        /**
         * Load a resource identified by an URI
         */
        URL
    }


    private static InputStream open(Class<?> clazz, String name) {
        return open(clazz, name, Location.FILESYSTEM,
                Location.CONTEXT_RESOURCE, Location.CLASS_RESOURCE,
                Location.CLASSLOADER_RESOURCE, Location.URL);
    }

    public static List<String> contentAsLines(String name) {
        return content(name).lines().toList();
    }

    public static String content(String name) {
        try (InputStream stream = open(FileUtil.class, name)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream open(Class<?> clazz, String name, Location... locations) {
        InputStream in = null;
        for (Location location : locations) {
            switch (location) {
                case FILESYSTEM:
                    try {
                        in = new FileInputStream(name);
                    } catch (FileNotFoundException e) {
                        // ignore
                    }
                    break;
                case CONTEXT_RESOURCE:
                    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                    if (contextClassLoader != null) {
                        in = contextClassLoader.getResourceAsStream(name);
                    }
                    break;
                case CLASS_RESOURCE:
                    if (clazz != null) {
                        in = clazz.getResourceAsStream(name);
                    }
                    break;
                case CLASSLOADER_RESOURCE:
                    if (clazz != null) {
                        in = clazz.getClassLoader().getResourceAsStream(name);
                    }
                    break;
                case URL:
                    try {
                        URL url = new URL(name);
                        URLConnection urlConnection = url.openConnection();
                        if (urlConnection instanceof HttpURLConnection) {
                            HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
                            httpURLConnection.setRequestProperty("User-Agent", "Java-QuickFIXJ-FileUtil");
                            httpURLConnection.connect();
                            in = httpURLConnection.getInputStream();
                        } else {
                            if (urlConnection != null) {
                                in = urlConnection.getInputStream();
                            }
                        }
                    } catch (IOException e) {
                        // ignore
                    }
                    break;
            }
            if (in != null) {
                break;
            }
        }

        return in;
    }
}
