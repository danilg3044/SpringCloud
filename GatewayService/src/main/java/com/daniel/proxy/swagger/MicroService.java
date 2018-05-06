package com.daniel.proxy.swagger;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.util.Optional;

public final class MicroService {

    private MicroService() {
    }

    public static Optional<Integer> getPort(URI uri) {
        int port = uri.getPort();
        if (port > 0) {
            return Optional.of(port);
        }
        return Optional.empty();
    }

    public static boolean isServerListening(URI url) {
        int port = getPort(url).orElse("https".equalsIgnoreCase(url.getScheme()) ? 443 : 80);
        return isServerListening(url.getHost(), port);
    }

    private static boolean isServerListening(String host, int port) {
        try (Socket s = new Socket(host, port)){
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}