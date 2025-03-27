# `defineModel()`-Funktionalität

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Im Rahmen der Entwicklung mit Storybook ist aufgefallen, dass das two-way-binding mit der
[Vue 3 `defineModel()`-Funktion](https://vuejs.org/api/sfc-script-setup.html#definemodel) nicht direkt unterstützt wird.
Dies äußert sich darin, dass variablen, die per `defineModel()` deklariert wurden, in der Dokumentation nicht unter
`PROPS` gelistet werden und auch das zugehörige event nicht unter `EVENTS` gelistet wird. Außerdem kommt dazu, dass
der Datentyp des modelValue nicht erkannt wird:

![defineModel without extra config](/defineModelAdr/defineModel_without_extra_config.png)

Daher hat sich die Frage gestellt zur vorherigen
[Vue 2 Implementierung](https://vuejs.org/guide/components/v-model.html#under-the-hood) des two-way-bindings mit
`defineProps()` und `defineEmits()` umzusteigen:

![props and defineEmit](/defineModelAdr/props-and-defineEmit.png)

## Entscheidung

Da `defienModel()` die aktuell empfohlene Vue 3 Best-Practice ist und weniger Code erfordert wurde entschieden das two-
way-binding damit zu implementieren. Um die Dokumentation trotzdem vollständig und korrekt zu halten, müssen im
Storybook-Code folgende Elemente hinzugefügt werden:

::: code-group

```typescript {5-9,13-14} [Component.stories.ts]
const meta: Meta<typeof Component> = {
    component: Component,
    argTypes: {
        modelValue: {
        // als Teil der Beschreibung konkret den Datentyp angeben
            description: "Aktuell gewählter Wert \n\n`String`", 
            table: { // konkret angeben, dass modelValue ein prop ist 
              category: "props", 
            }, 
        },
        "onUpdate:modelValue": { 
          description: "Wird ausgelöst wenn sich der aktuelle Wert ändert", 
          table: { // konkret angeben, dass onUpdate ein event ist 
            category: "events", 
          },
        },
    },
};

```

```vue [Component.vue]
<template>
  <v-autocomplete v-model="modelValue"></v-autocomplete> 
</template>

<script setup lang="ts">
import { VAutocomplete } from "vuetify/components";

// obwohl der Typ explizit angegeben wird, kann Storybook ihn nicht erkennen
const modelValue = defineModel({ type: String }); 
</script>
```

:::

So ergibt sich trotz der Nutzung von `defineModel()` für die Dokumentation das gleiche Verhalten, das mit dem Vue 2
two-way-binding erreicht wird.

## Konsequenzen

### positiv

- die empfohlene Vue 3 Best-Practice wird eingesetzt
- in der Komponente muss weniger Code geschrieben werden

### negativ

- es muss mehr Code in der zugehörigen Story geschrieben werden, um die Dokumentation trotzdem vollständig zu halten
