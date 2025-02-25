# Naming Convention für Tests

## Kontext

Aktuell gibt es keine Struktur oder Vorgaben bei der Benennung von Tests. Die meisten Namen sind sehr kurz gehalten und
wenig aussagekräftig, wie zum Beispiel:

````java
void dataFound() {}
void noDataFound() {}
void serviceCalled() {}
````

Damit der Gesamtcode im Projekt übersichtlicher und einheitlicher ist, sollen Naming Conventions eingesetzt werden.
Grundlage für die Einführung sind unter anderem auch sich wiederholende Tests mit gleichem Inhalt in den verschiedenen
Services. So wird gewährleistet, dass deren Kontext schneller klar ist, ohne den Code lesen zu müssen und die Wartung
und Erweiterung des Codes wird erleichtert.

## Entscheidung

Die Bezeichnungen sollen dem Schema `should_<result>_when_<input>` folgen, wobei Result (= ExpectedBehavior, bzw.
erwartetes Ergebnis) und Input (= StateUnderTest, bzw. zu testender Zustand) in CamelCase gehalten werden. Dem Schema
entsprechend sind die Testnamen auch auf Englisch zu formulieren.

Wir haben uns darauf geeinigt, die zu testenden Methoden im Backend mit `@Nested`, und im Frontend mit
`describe("xyz", () => {}` zu gruppieren. Im Fall von überladenen Methoden werden diese innerhalb der Methodenklasse 
zusätzlich verschachtelt und ebenfalls mit `@Nested` annotiert, oder in einen neuen `describe()`-Block eingeordnet.

### Beispiele

```java
// Backend: should_expectedResultInCamelCase_when_inputInCamelCase() {}
void should_returnDTO_when_givenValidId() {}
void should_notThrowException_when_newDataSaved() {}
void should_throwAccessDeniedException_whenAuthoritiesMissing() {}
```

```typescript
// Frontend: it("should_expectedResultInCamelCase_when_inputInCamelCase", () => {});
it("should_returnResolvedResponse_when_responseCodeIs200", () => {});
it("should_executeGetMessageFunction_when_buttonClicked", () => {});
it("should_clearMessageInput_when_messageHasBeenSent", () => {});
```

##### Gruppierung überladener Methoden:

**Backend-Beispiel:** Vereinfachter Pseudocode! Beispiel aus dem Vorfälle und Vorkommnisse Service.
::: code-group
```java{2-3,5-6,11-12} [MapperTest.java]
class EreignisModelMapperTest {
  @Nested
  class ToEntity {                      // Name der zu testenden Methode
    
     @Nested
     class ToEreignisEntity {           // nested overload 1
         @Test
         void should_returnEreignis_when_givenEreignisModel() {}
     }
  
     @Nested
     class ToListOfEreignisEntity {     // nested overload 2
         @Test
         void should_returnListOfEreignis_when_givenEreignisseWriteModel() {}
     }
  }
}
```

```java [Mapper.java]

@Mapper
public interface EreignisModelMapper {

    Ereignis toEntity(EreignisModel model);

    List<Ereignis> toEntity(EreignisseWriteModel model);
}
```
:::

::: tip HINWEIS
Mit der Einführung der [Archunit-Tests](https://www.archunit.org/) im Projekt, wird die grundsätzliche Einhaltung der 
Naming Convention **im Backend** anhand der RegEx `^should_[a-z].*_when_[a-z].*` automatisiert für alle Tests mit den 
Annotationen `@Test` und `@ParametrizedTest` geprüft. Für jeden neuen Service muss dieser Test analog zu den anderen 
hinzugefügt werden.
:::

## Konsequenzen

### positiv

Durch die Einführung dieser Naming Conventions wird die Lesbarkeit und Verständlichkeit unserer Tests verbessert. Die
einheitliche Namensgebung erleichtert es anderen Entwicklern, den Code zu verstehen und zu warten. Außerdem wird die
Zusammenarbeit innerhalb des Teams effizienter, da jeder bei Bedarf schnell die relevanten Tests finden und bearbeiten
kann. Darüber hinaus ermöglicht die Verwendung von `@Nested` eine bessere Strukturierung der Tests und eine einfachere
Überprüfung der Testergebnisse. Insgesamt erwarten wir eine höhere Codequalität und eine schnellere Fehlererkennung
durch die Einführung dieser Convention.

### negativ

Gegebenenfalls müssen - neben der Anpassung der Namen - einige bestehende Tests umgeschrieben werden, um der neuen
Bezeichnung zu entsprechen. 