# Raport: czy dziś można technicznie budować aplikacje webowe z separacją modułów przez Java Module System (JDK 9+)

## Teza
**Tak — obecnie istnieją pełne możliwości techniczne zbudowania aplikacji webowej, która używa JPMS (Java Platform Module System) do separacji modułów.**

## 1) Podstawa normatywna (JVM + JLS + JPMS)

1. **JVM Specification (od Java 9)** wprowadza pojęcie modułu do modelu uruchomieniowego (`module-info.class`, `Module`, `ModulePackages`, `ModuleMainClass`), co oznacza, że modułowość jest egzekwowana przez runtime, a nie tylko konwencję buildową.
2. **JLS (Java Language Specification)** definiuje deklaracje `module`, dyrektywy `requires`, `exports`, `opens`, `uses`, `provides` — czyli językowy i statycznie sprawdzalny kontrakt granic modułów.
3. **JPMS** zapewnia:
   - silną enkapsulację pakietów,
   - jawne zależności,
   - możliwość selektywnego otwierania refleksji (`opens`) dla frameworków.

Wniosek: na poziomie specyfikacji języka i maszyny wirtualnej nie ma bariery — modułowa aplikacja webowa jest w pełni wspieranym scenariuszem.

## 2) Wnioski dla stosu webowego

### Jakarta EE
- Ekosystem opiera się na kontenerach, classloaderach i CDI/reflective APIs.
- **Możliwe strategie**:
  - uruchomienie modularnego kodu domenowego jako JPMS + adapter do kontenera,
  - użycie `opens`/`open module` tam, gdzie kontener wymaga refleksji.
- Ograniczenie nie jest „czy się da”, tylko „jak precyzyjnie skonfigurować refleksję i wdrożenie”.

### Spring Boot / Spring Cloud
- Spring działa intensywnie refleksyjnie (DI, AOP, proxy, konfiguracja).
- **Dziś działa najlepiej podejście hybrydowe**:
  - modułowy rdzeń biznesowy (JPMS),
  - warstwa Spring (często classpath / unnamed module) lub jawnie otwierane moduły przez `opens`.
- To oznacza, że nawet przy dużych frameworkach JPMS jest praktycznie wykorzystywany do separacji core modułów.

### Inne nowoczesne frameworki
- **Micronaut / Quarkus / Helidon**: mniejsza zależność od runtime refleksji (w różnym stopniu), co zwykle ułatwia współpracę z silną enkapsulacją.
- **Frameworki reaktywne i lekkie serwery** (Netty, Undertow, JDK HttpServer): bardzo dobre do „czystego JPMS”, bo wymagają mniej wyjątków `opens`.

## 3) Ograniczenia praktyczne (realne, ale kontrolowalne)

1. Biblioteki bez poprawnych deskryptorów modułów (`module-info`) bywają ładowane jako automatic modules.
2. Frameworki refleksyjne potrzebują jawnych `opens` (czasami szerokich).
3. Przy migracji legacy najczęściej stosuje się etapowość: najpierw moduły domenowe, potem kolejne warstwy.

To są ograniczenia **implementacyjne**, nie blokery architektoniczne.

## 4) Dowód praktyczny — działający szkielet

W repozytorium dodano działający szkielet aplikacji `jpms-web-skeleton`:

- `io.example.api` — kontrakt usługi (`GreetingService`),
- `io.example.core` — implementacja + `provides ... with ...`,
- `io.example.web` — warstwa HTTP na JDK `HttpServer` + `ServiceLoader` (`uses`).

Separacja odpowiedzialności jest wymuszona deklaracjami modułów (`requires/exports/uses/provides`).

## 5) Jak uruchomić

Wymagania:
- JDK 21+
- Maven 3.9+

Kroki:

```bash
cd jpms-web-skeleton
mvn -q clean package
mvn -q -pl app-web exec:java
```

Po uruchomieniu:
- otwórz `http://localhost:8080`
- zobaczysz stronę potwierdzającą działanie modularnej aplikacji webowej.

## 6) Konkluzja

**Teza została potwierdzona** zarówno na poziomie specyfikacji (JLS/JVM/JPMS), jak i empirycznie przez działający przykład. W 2026 r. budowa aplikacji webowej z JPMS jest technicznie możliwa i produkcyjnie sensowna, szczególnie przy strategii:

- JPMS dla granic domeny i modułów wewnętrznych,
- kontrolowane `opens` dla frameworków wymagających refleksji,
- etapowe wdrażanie w istniejących systemach.
