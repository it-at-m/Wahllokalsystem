# Namingconventions für Tests im Backend

Durch die Einführung dieser Naming Conventions wird die Lesbarkeit und Verständlichkeit unserer Tests verbessert. Die
einheitliche Namensgebung erleichtert es anderen Entwicklern, den Code zu verstehen und zu warten. Außerdem wird die
Zusammenarbeit innerhalb des Teams effizienter, da jeder bei Bedarf schnell die relevanten Tests finden und bearbeiten
kann. Darüber hinaus ermöglicht die Verwendung von `@Nested` eine bessere Strukturierung der Tests und eine einfachere
Überprüfung der Testergebnisse. Insgesamt erwarten wir eine höhere Codequalität und eine schnellere Fehlererkennung
durch die Einführung dieser Convention.

## Beispiele

```java
void should_returnDTO_when_givenValidId() {}
void should_notThrowException_when_newDataSaved() {}
void should_throwAccessDeniedException_whenAuthoritiesMissing() {}
```

### Gruppierung überladener Methoden:

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