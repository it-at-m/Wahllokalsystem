# Naming Convention für Tests

## Kontext

Damit der Code übersichtlicher und einheitlicher ist, sollen Naming Conventions eingesetzt werden. Damit wird für Tests gewährleistet, dass deren Inhalt klar ist, ohne den Code lesen zu müssen.

## Entscheidung

Die Bezeichnungen sollen dem Schema `should_<result>_when_<input>` folgen, wobei Result (= ExpectedBehavior, bzw. erwartetes Ergebnis) und Input (= StateUnderTest, bzw. zu testender Zustand) in CamelCase gehalten werden. Dem Schema entsprechend sind die Testnamen auch auf englisch zu formulieren.

Wir haben uns darauf geeinigt, die zu testenden Methoden mit `@Nested` zu gruppieren. Im Fall von überladenen Methoden werden diese innerhalb der Methodenklasse zusätzlich verschachtelt und ebenfalls mit `@Nested` annotiert.

### Beispiele

```java
//   should_expectedResultInCamelCase_when_inputInCamelCase() {}
void should_returnDTO_when_givenValidId() {}
void should_notThrowException_when_newDataSaved() {}
void should_throwAccessDeniedException_whenAuthoritiesMissing() {}
```

##### Gruppierung überladener Methoden:
Vereinfachter Pseudocode! Beispiel aus Vorfälle und Vorkommnisse Service
```java
@Mapper
public interface EreignisModelMapper {

  Ereignis toEntity(EreignisModel model);    
    
  List<Ereignis> toEntity(EreignisseWriteModel model);  
}
```

```java{2-3,5-6,11-12} 
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

## Konsequenzen

### positiv

Durch die Einführung dieser Naming Conventions wird die Lesbarkeit und Verständlichkeit unserer Tests verbessert. Die einheitliche Namensgebung erleichtert es anderen Entwicklern, den Code zu verstehen und zu warten. Außerdem wird die Zusammenarbeit innerhalb des Teams effizienter, da jeder bei Bedarf schnell die relevanten Tests finden und bearbeiten kann. Darüber hinaus ermöglicht die Verwendung von `@Nested` eine bessere Strukturierung der Tests und eine einfachere Überprüfung der Testergebnisse. Insgesamt erwarten wir eine höhere Codequalität und eine schnellere Fehlererkennung durch die Einführung dieser Convention.

### negativ

Gegebenenfalls müssen - neben der Anpassung der Namen - einige bestehende Tests umgeschrieben werden, um der neuen Bezeichnung zu entsprechen. 