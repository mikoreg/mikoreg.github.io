package io.example.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.example.api.GreetingService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ServiceLoader;
import java.util.concurrent.CountDownLatch;

public final class WebApplication {
    private WebApplication() {
    }

    public static void main(String[] args) throws IOException {
        GreetingService greetingService = ServiceLoader.load(GreetingService.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Brak implementacji GreetingService"));

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", exchange -> write(exchange, page(greetingService.greeting())));
        server.start();

        System.out.println("Server started on http://localhost:8080");

        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            server.stop(0);
        }
    }

    private static String page(String message) {
        return """
                <!doctype html>
                <html lang=\"pl\">
                <head>
                  <meta charset=\"utf-8\"/>
                  <title>JPMS Skeleton</title>
                </head>
                <body>
                  <h1>Dowód techniczny: JPMS + aplikacja webowa</h1>
                  <p>%s</p>
                </body>
                </html>
                """.formatted(message);
    }

    private static void write(HttpExchange exchange, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
        }
    }
}
