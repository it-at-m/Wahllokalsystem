---
name: backend-tests
description: Beim Arbeiten in Java-/Spring-Boot-Projekten sollen automatisch passende Tests für neue oder geänderte Klassen (Services, Controller, Konfigurationen, Security) entstehen. Der Skill sorgt dafür, dass diese Tests konsistent zu den bestehenden Mustern und zum Backend-Testkonzept sind und die geforderte Testabdeckung erreichen.
---

## Geltungsbereich

Der Skill ist anzuwenden, wenn:

1. In einem Java-/Spring-Boot-Projekt neue fachliche Klassen entstehen, zum Beispiel:
   - `*Service`
   - `*Controller`
   - `*Configuration` (inkl. Security-Konfigurationen)
2. Bestehende fachliche Logik, Endpunkte oder Security-Regeln erweitert werden und
   - es für diese Klasse noch keine Tests gibt oder
   - bestehende Tests nicht mehr alle relevanten Fälle abdecken.

---

## Allgemeine Regeln

1. **Testframeworks**
   - Verwende **JUnit 5** als Testframework.
   - Verwende **AssertJ** für Assertions.
   - Verwende **Mockito** für das Mocken von Abhängigkeiten.

2. **Testmethoden-Namen**
   - Testmethoden heißen immer:
     ```text
     should_<expectedResult>_when_<stateUnderTest>
     ```
     - Englisch.
     - `<expectedResult>` und `<stateUnderTest>` im camelCase.

3. **Struktur innerhalb einer Testklasse**
   - Nutze `@Nested`-Klassen, um Tests nach fachlichen Methoden / Use Cases zu gruppieren (z.B. `CreateFoo`, `UpdateFoo`, `DeleteFoo`).
   - Innerhalb einer `@Nested`-Klasse:
     - mindestens ein Test für den Erfolgsfall,
     - Tests für Validierungsfehler,
     - Tests für Fehler/Exceptions von externen Abhängigkeiten (Clients, Repositories).

4. **Testabdeckung**
   - Eigene Klassen sollen durch Unittests eine **Codecoverage von 100%** (Methoden und Lines) erreichen.
   - Externe Abhängigkeiten (REST-Clients, Repositories, andere Services) werden gemockt oder über Integrationstests indirekt abgedeckt.

5. **Architekturtests**
   - `ArchUnitTest`-Klassen werden **nicht** im Rahmen von Feature-Implementierungen angepasst.
   - Neue Implementierungen müssen die bestehenden ArchUnit-Regeln einhalten.

---

## Service-Unittests (`*ServiceTest`)

Verwendete Muster: z.B. `WahllokalBenutzerServiceTest`.

### Setup

- Verwende den Mockito-Extension-Mechanismus:
  ```java
  @ExtendWith(MockitoExtension.class)
  class SomeServiceTest {

    @Mock SomeValidator someValidator;
    @Mock SomeClient someClient;

    @InjectMocks SomeService unitUnderTest;
  }
  ```

- Alle fachlichen Kollaborateure (Validatoren, Clients, Repositories) werden als `@Mock` deklariert.
- Der getestete Service wird als `@InjectMocks` eingebunden (`unitUnderTest`).

### Struktur

- Pro öffentliche Service-Methode eine `@Nested`-Klasse:
  ```java
  @Nested
  class CreateFoo { ... }

  @Nested
  class UpdateFoo { ... }
  ```

- Innerhalb jeder `@Nested`-Klasse:
  - `should_..._when_...`-Tests für:
    - erfolgreiche Ausführung,
    - Validierungsfehler (z.B. FachlicheWlsException),
    - technische Fehler (z.B. TechnischeWlsException).

### Assertions und Mockito

- Validierungsfehler:
  ```java
  Assertions.assertThatThrownBy(() -> unitUnderTest.createFoo(input))
      .isSameAs(mockedValidationException);
  ```

- Kein Fehler:
  ```java
  Assertions.assertThatNoException()
      .isThrownBy(() -> unitUnderTest.deleteFoo(id));
  ```

- Komplexe Rückgabeobjekte:
  ```java
  Assertions.assertThat(result)
      .usingRecursiveComparison()
      .isEqualTo(expectedResult);
  ```

- Verifikation von Interaktionen:
  ```java
  Mockito.verify(someClient).callFoo(eq(id));
  ```

---

## Authentifizierungs-Tests (`configuration.SecurityConfigurationTest`)

Verwendete Muster: z.B. `SecurityConfigurationTest` im Admin-Service.

### Setup

- Nutze Spring-Boot-Test mit MockMvc:
  ```java
  @SpringBootTest(
      classes = MicroServiceApplication.class,
      webEnvironment = SpringBootTest.WebEnvironment.MOCK)
  @AutoConfigureMockMvc
  @AutoConfigureObservability
  @ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
  class SecurityConfigurationTest {

    @Autowired MockMvc api;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean SomeBusinessService someBusinessService;
  }
  ```

- Alle Business-Services, die von den Endpoints aufgerufen werden, werden als `@MockitoBean` eingebunden.

### Basis-Endpunkte

- Gesicherte vs. ungesicherte Endpunkte prüfen:
  ```java
  @Test
  void should_returnStatusUnauthorized_when_accessingSecuredResourceRoot() throws Exception {
    api.perform(get("/")).andExpect(status().isUnauthorized());
  }

  @Test
  void should_returnStatusOk_when_accessingUnsecuredResourceActuatorHealth() throws Exception {
    api.perform(get("/actuator/health")).andExpect(status().isOk());
  }
  ```

### Fachliche Endpunkte

- Pro Endpoint eine `@Nested`-Klasse.
- Für jeden Endpoint:
  - `@WithAnonymousUser` → Status `401` (Unauthorized).
  - `@WithMockUser` → Status `200`/`204` und Verifikation der Service-Aufrufe:
    ```java
    @WithMockUser
    @Test
    void should_returnOk_when_callingAuthenticated() throws Exception {
      val id = "someId";
      val request =
          MockMvcRequestBuilders.post("/businessActions/doSomething/" + id)
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON);

      api.perform(request).andExpect(status().isOk());

      Mockito.verify(someBusinessService).doSomething(id);
    }
    ```

---

## Controller-Integrationstests (`rest.*ControllerIntegrationTest`)

Verwendete Muster: z.B. `WahllokalBenutzerControllerIntegrationTest`.

### Setup

- Verwende Spring-Boot-Test mit MockMvc und ggf. WireMock:
  ```java
  @SpringBootTest(
      classes = MicroServiceApplication.class,
      webEnvironment = SpringBootTest.WebEnvironment.MOCK)
  @AutoConfigureMockMvc
  @AutoConfigureWireMock
  @ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
  class SomeControllerIntegrationTest {

    @Autowired MockMvc api;
    @Autowired ObjectMapper objectMapper;

    @AfterEach
    void teardown() {
      reset(); // WireMock reset
    }
  }
  ```

### Validierungsfälle

- Ungültige Inputs führen zu fachlichen WLS-Exceptions:
  ```java
  @Test
  @WithMockUser(authorities = {Authorities.SOME_AUTHORITY})
  void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
    val invalidId = " ";
    val request = post("/businessActions/doSomething/" + invalidId).with(csrf());

    val expectedWlsExceptionDTO =
        new WlsExceptionDTO(
            WlsExceptionCategory.F,
            ExceptionConstants.MISSING_ARGUMENT.code(),
            "WLS-SERVICE",
            ExceptionConstants.MISSING_ARGUMENT.message());

    val result = api.perform(request).andExpect(status().isBadRequest()).andReturn();
    val resultBody =
        objectMapper.readValue(result.getResponse().getContentAsString(), WlsExceptionDTO.class);

    Assertions.assertThat(resultBody)
        .usingRecursiveComparison()
        .ignoringFields("message")
        .isEqualTo(expectedWlsExceptionDTO);
    Assertions.assertThat(resultBody.message()).isNotNull();
  }
  ```

### Remote-Clients / WireMock

- Simuliere erfolgreiche Remote-Aufrufe und prüfe Status:
  ```java
  stubFor(
      WireMock.get("/remote/foo/" + id)
          .willReturn(aResponse().withStatus(HttpStatus.OK.value())));

  api.perform(request).andExpect(status().isOk());
  ```

- Autoritäten werden über `@WithMockUser(authorities = {...})` gesetzt und müssen den im Projekt definierten `Authorities`-Konstanten entsprechen.

---

## Service-Securitytests (`service.*SecurityTest`)

Verwendete Muster: z.B. `WahltageServiceSecurityTest`, `WahlvorstandServiceSecurityTest`.

### Service-Security ohne BezirkIDPermissionEvaluator

- Beispiel:
  ```java
  @SpringBootTest(classes = MicroServiceApplication.class)
  @ActiveProfiles({SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS})
  class SomeServiceSecurityTest {

    @Autowired SomeService unitUnderTest;

    @Nested
    class GetFoos {

      @Test
      void should_getAccess_when_allRequiredAuthoritiesArePresent() {
        SecurityUtils.runWith(Authorities.SOME_REQUIRED_AUTHORITY);

        Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getFoos());
      }

      @Test
      void should_throwAccessDeniedException_when_requiredAuthorityIsMissing() {
        SecurityUtils.runWith("wrong_authority");

        Assertions.assertThatException()
            .isThrownBy(() -> unitUnderTest.getFoos())
            .isInstanceOf(AccessDeniedException.class);
      }
    }
  }
  ```

### Service-Security mit BezirkIDPermissionEvaluator

- Setup:
  ```java
  @SpringBootTest(classes = MicroServiceApplication.class)
  @ActiveProfiles({SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS})
  class AnotherServiceSecurityTest {

    @MockitoBean BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired AnotherService unitUnderTest;

    @BeforeEach
    void setup() {
      SecurityContextHolder.clearContext();
    }
  }
  ```

- Für jede sicherheitsrelevante Methode:
  - Test mit allen Authorities und `tokenUserBezirkIdMatches(...) == true` → kein Fehler.
  - Test mit allen Authorities und `tokenUserBezirkIdMatches(...) == false` → `AccessDeniedException`.
  - Parametrisierte Tests für fehlende Authorities mit:
    ```java
    @ParameterizedTest(name = "{index} - {1} missing")
    @MethodSource("getMissingAuthoritiesVariations")
    void should_throwAccessDeniedException_when_anyAuthorityMissing(
        final ArgumentsAccessor argumentsAccessor) {
      SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

      val id = "someId";
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(id), Mockito.any()))
          .thenReturn(true);

      Assertions.assertThatThrownBy(() -> unitUnderTest.securedMethod(id))
          .isInstanceOf(AccessDeniedException.class);
    }

    private static Stream<Arguments> getMissingAuthoritiesVariations() {
      return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
          Authorities.ALL_REQUIRED_AUTHORITIES_FOR_SECURED_METHOD);
    }
    ```

---

## Architekturtests (`ArchUnitTest`)

1. Der Skill generiert **keine neuen ArchUnit-Regeln** und passt bestehende `ArchUnitTest`-Klassen nicht an.
2. Neue Implementierungen müssen sich an:
   - die projektspezifische `ArchUnitTest`-Klasse,
   - sowie an gemeinsame Regeln aus `wls-common:testing` (`archunit.rule`-Package),
   halten.
