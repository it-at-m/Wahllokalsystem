---
name: frontend-tests
description: Beim Schreiben oder Ändern von Frontend-Tests (Vue 3, Vuetify 3, Vitest) im WLS konsistente, fachlich sinnvolle Tests erzeugen, die sich strikt an die Projektkonventionen, ADRs und vorhandene Beispiele halten.
---


## Geltungsbereich

Der Skill greift, wenn:

1. Im Frontend-Modul `wls-gui-wahllokalsystem/` oder `wls-gui-admintool/` gearbeitet wird.
2. Dateien im Ordner `tests/` erstellt oder geändert werden, insbesondere:
   - `tests/composables/**`
   - `tests/components/**`
   - `tests/views/**`
   - `tests/stores/**`
   - `tests/unit/**`
3. Neue `.spec.ts`-Dateien angelegt oder bestehende Testdateien erweitert/überarbeitet werden.

---

## Allgemeine Regeln

1. **Ordnerstruktur**
   - Die Struktur unter `tests/` spiegelt die Struktur von `src/`:
     - `src/composables/...` → `tests/composables/...`
     - `src/components/...` → `tests/components/...`
     - `src/views/...` → `tests/views/...`
     - `src/stores/...` → `tests/stores/...`
   - Neue Tests werden immer im passenden Spiegelpfad angelegt.

2. **Namenskonventionen**
   - Test-Dateien: `<Name>.spec.ts` (analog zu bestehenden Tests).
   - Testfälle: `it("should_<expectedResult>_when_<stateUnderTest>", ...)`  
     - Englisch
     - `<expectedResult>` und `<stateUnderTest>` im camelCase.
   - Composable-Tests: `describe("<ComposableName>", () => { ... })`.
   - Komponenten-Tests: `describe("<ComponentName>.vue", () => { ... })` oder `describe("App", () => { ... })`.

3. **Imports / Werkzeuge**
   - Verwende Vitest (`describe`, `it`, `expect`, `beforeEach`, `afterEach`, `vi`) und Vue Test Utils (`mount`, `flushPromises`, `VueWrapper`) analog zu bestehenden Tests.
   - Nutze vorhandene Test-Utils:
     - `withSetup` für Composable-Tests: `import { withSetup } from "@tests/utils/testutils.ts";`
   - Pinia / Vuetify / Router in Komponententests nur wie in den bestehenden Specs verwendet (z.B. `createTestingPinia`, global `vuetify`-Plugin, `createRouter` oder `vi.mock` für `@/plugins/router.ts`).

4. **Keine Snapshots für Vue-Komponenten**
   - Der Skill **darf keine neuen Snapshot-Tests** für Vue-Komponenten erzeugen.
   - Insbesondere:
     - Kein `toMatchFileSnapshot(...)`.
     - Keine neuen Einträge unter `tests/__snapshots__/` für Vue-Dateien.
   - Vorhandene Snapshot-Tests werden nicht erweitert, sondern maximal schrittweise fachlich ersetzt (wenn ausdrücklich verlangt).

5. **Fokus der Tests**
   - Composables: reine fachliche Logik (Rückgabewerte, Refs, Aufrufe).
   - Komponenten:
     - Sichtbarkeit und Zustand (z.B. Buttons, Dialoge, Fehlermeldungen).
     - Events, Store-Aufrufe und Router-Navigation.
   - Kein Test auf fragile Details wie konkrete CSS-Klassen, Pixel-basierte Styles oder Textfragmente, sofern nicht fachlich relevant.

6. **ADR- und Doku-Bezug**
   - `adr-use-defineModel`: Two-Way-Binding mit `defineModel()` wird respektiert; Tests prüfen Verhalten (Wertänderung, Events), nicht interne Implementierungsdetails.
   - `adr001-no-user-input-override`: Die Anwendung korrigiert Eingaben nicht "still"; Tests prüfen Validierungslogik (`rules`) und Fehlerzustände, nicht bloße Styling-Details.
   - UI-ADR-Index: Material Design als Grundlage; Tests konzentrieren sich auf semantisches Verhalten (sichtbar/unsichtbar, enabled/disabled).

---

## Spezifische Regeln für Composable-Tests

Der Skill soll Composable-Unit-Tests immer nach folgendem Muster erzeugen:

1. **Setup über `withSetup`**
   - Composable-Tests nutzen `withSetup` aus `@tests/utils/testutils.ts`, um Composable und App-Instanz zu erhalten:

     ```ts
     import type { App } from "vue";

     import { withSetup } from "@tests/utils/testutils.ts";
     import { afterEach, beforeEach, describe, expect, it } from "vitest";

     import { useCounter } from "@/composables/common/useCounter.ts";
     ```

2. **Lebenszyklus im Test**
   - Im `beforeEach` wird die Unit initialisiert:

     ```ts
     let unitUnderTest: ReturnType<typeof useCounter>;
     let app: App;

     beforeEach(() => {
       [unitUnderTest, app] = withSetup(() => useCounter(0));
     });
     ```

   - Im `afterEach` wird die App sauber unmountet:

     ```ts
     afterEach(() => {
       app.unmount();
     });
     ```

3. **Testfälle**
   - Fachliche Assertions auf die Rückgabewerte / Refs, keine Snapshots:

     ```ts
     it("should_incrementCountByOne_when_incrementIsCalledOnce", () => {
       expect(unitUnderTest.count.value).toBe(0);

       unitUnderTest.increment();

       expect(unitUnderTest.count.value).toBe(1);
     });

     it(
       "should_resetCountToInitialValue_when_resetIsCalledAfterMultipleIncrements",
       () => {
         unitUnderTest.increment();
         unitUnderTest.increment();
         expect(unitUnderTest.count.value).toBe(2);

         unitUnderTest.reset();

         expect(unitUnderTest.count.value).toBe(0);
       }
     );
     ```

4. **Timer / Zeit-bezogene Composables**
   - Bei Zeit- oder Timer-Composables (z.B. `useCurrentTime`):
     - `vi.useFakeTimers()` in `beforeEach`.
     - `vi.useRealTimers()` in `afterEach`.
     - Zeitfortschritt über `vi.advanceTimersByTime(...)` testen.
   - Beispiel (bestehendes Pattern):

     ```ts
     beforeEach(() => {
       vi.useFakeTimers();
       [unitUnderTest, app] = withSetup(useCurrentTime);
     });

     afterEach(() => {
       app.unmount();
       vi.useRealTimers();
     });

     it("should_updateCurrentTime_when_timeIncrementsForOneSecond", () => {
       const initialTime = new Date(unitUnderTest.currentTime.value);

       vi.advanceTimersByTime(1000);

       expect(unitUnderTest.currentTime.value).not.toEqual(initialTime);
       expect(unitUnderTest.currentTime.value.getSeconds()).toBe(
         (initialTime.getSeconds() + 1) % 60
       );
     });
     ```

---

## Spezifische Regeln für Komponenten-Tests (ohne Snapshots)

Auch wenn die Anfrage den Fokus auf Composables legt, soll der Skill Komponenten-Tests ohne Snapshots konsistent unterstützen:

1. **Mounting mit Vuetify / Pinia / Router**
   - Beispiel mit Vuetify-only:

      ```ts
      import { flushPromises, mount, VueWrapper } from "@vue/test-utils";
      import { describe, it, expect, beforeEach, afterEach } from "vitest";
      import { VBtn } from "vuetify/components";

      import {
        COMPONENT_EVENT_TESTS,
        COMPONENT_RENDER_TESTS,
        stubVisualViewport,
      } from "@tests/utils/testutils.ts";
      import WlsExampleDialog from "@/components/common/WlsExampleDialog.vue";
      import vuetify from "@/plugins/vuetify.ts";

      describe("WlsExampleDialog.vue", () => {
        let wrapper: VueWrapper;

        stubVisualViewport();

        beforeEach(() => {
          wrapper = mount(WlsExampleDialog, {
            global: {
              plugins: [vuetify],
              stubs: {
                // Render teleport content within the mounted wrapper.
                teleport: true,
              },
            },
            props: {
              modelValue: false,
            },
          });
        });

        afterEach(() => {
          if (wrapper) {
            wrapper.unmount();
          }
        });

        describe(COMPONENT_RENDER_TESTS, () => {
          it("should_notRenderDialog_when_modelValueIsFalse", async () => {
            await flushPromises();

            expect(
              wrapper.find('[data-test="wls-example-dialog"]').exists()
            ).toBe(false);
          });

          it("should_renderDialog_when_modelValueIsTrue", async () => {
            await wrapper.setProps({ modelValue: true });

            await flushPromises();

            expect(
              wrapper.find('[data-test="wls-example-dialog"]').exists()
            ).toBe(true);
          });
        });

        describe(COMPONENT_EVENT_TESTS, () => {
          it(
            "should_emitUpdateModelValueFalse_when_confirmButtonClicked",
            async () => {
              await wrapper.setProps({ modelValue: true });
              await flushPromises();

              const confirmButton = wrapper
                .findAllComponents(VBtn)
                .find(
                  (btn) =>
                    btn.attributes("data-test") ===
                    "wls-example-dialog-confirm"
                );

              await confirmButton?.trigger("click");

              expect(wrapper.emitted("update:modelValue")).toStrictEqual([
                [false],
              ]);
            }
          );
        });
      });
      ```

2. **Keine Snapshots**
   - Komponenten-Tests verwenden **nur** `exists`, `emitted`, `attributes`, `text`, Store-/Mock-Aufrufe, Router-Navigation etc.
   - Der Skill soll niemals `toMatchFileSnapshot`, `getSnapshotFilename` oder `__snapshots__`-Pfade in neuem Code verwenden.

---

## Dinge, die der Skill explizit nicht tun darf

1. Keine neuen Snapshot-Tests (`toMatchFileSnapshot`, `__snapshots__/`-Struktur) für Vue-Komponenten.
2. Keine Änderungen an ArchUnit-Tests oder backend-spezifischen Testkonzept-Dateien.
3. Keine Tests, die sich auf temporäre oder instabile technische Details stützen:
   - zufällige IDs
   - CSS-Klassen/Inline-Styles ohne fachliche Relevanz
   - Texte, die klar als UI-Text ohne fachliche Bedeutung erkennbar sind

---

## Kurzfassung für Implementation

Wenn der Agent mit diesem Skill Tests schreibt:

1. Für Composables:
   - Nutze `withSetup`.
   - Nutze `App`-Instanz und unmount in `afterEach`.
   - Schreibe `it("should_..._when_...", ...)` mit fachlichen Assertions auf Refs und Rückgabewerte.
   - Keine Snapshots.

2. Für Komponenten:
    - Mount mit `vuetify`, ggf. `createTestingPinia` und Router-Mocks wie in bestehenden Tests.
    - Gruppiere Render- und Verhaltenstests mit `COMPONENT_RENDER_TESTS` bzw. `COMPONENT_EVENT_TESTS` aus `@tests/utils/testutils.ts`.
    - Selektiere Elemente mit `data-test`.
    - Prüfe fachliches Verhalten (Sichtbarkeit, Events, Store-Aufrufe).
    - Keine Snapshots, nur direkte Assertions.
