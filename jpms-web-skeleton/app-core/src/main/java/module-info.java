module io.example.core {
    requires io.example.api;

    provides io.example.api.GreetingService with io.example.core.DefaultGreetingService;
}
