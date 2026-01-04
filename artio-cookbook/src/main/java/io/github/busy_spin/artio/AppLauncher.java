package io.github.busy_spin.artio;

public interface AppLauncher extends AutoCloseable {

    String id();

    void launch();

}
