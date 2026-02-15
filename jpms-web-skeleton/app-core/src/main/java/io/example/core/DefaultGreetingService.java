package io.example.core;

import io.example.api.GreetingService;

public class DefaultGreetingService implements GreetingService {
    @Override
    public String greeting() {
        return "JPMS web app działa: moduły API/CORE/WEB są odseparowane.";
    }
}
