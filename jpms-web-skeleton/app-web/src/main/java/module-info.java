module io.example.web {
    requires io.example.api;
    requires jdk.httpserver;

    uses io.example.api.GreetingService;
}
