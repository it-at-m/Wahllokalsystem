import { shallowMount } from "@vue/test-utils";
import { createPinia } from "pinia";
import { beforeAll, beforeEach, describe, expect, it } from "vitest";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

import TheSnackbar from "@/components/TheSnackbar.vue"; // Funktion oder Klasse die getestet werden soll

// = state-management Bibliothek: zentrale Verwaltung des Zustands der Anwendung und Erleichterung der Kommunikation
// zwischen Komponenten
const pinia = createPinia();

// `describe`-Funktionen können geschachtelt werden, um Tests zu gruppieren und ordnen. Es ist immer eine Top-Level
// `describe`-Funktion notwendig.
describe("Beispiele und Erklärungen", () => {
  // initialisiert Variable, die hinterher vor jedem Test zu einer vuetify-Instanz wird --> siehe `beforeEach()`
  let vuetify: ReturnType<typeof createVuetify>;

  // wird einmal vor allen Tests ausgeführt
  beforeAll(() => {
    createPinia();
    // Definition, dass vuetify benutzt wird
    createVuetify();
  });

  // wird vor jedem Test ausgeführt
  beforeEach(() => {
    // erstellt vuetify-bibliotheks-Instanz, damit in den Tests die vue-Komponenten verwendet werden können
    vuetify = createVuetify({
      components,
      directives,
    });
  });

  // Test um zu testen ob Tests funktionieren
  it("true test", () => {
    expect(true).toBe(true);
  });

  // `it`: einzelner Test-Block mit Beschreibung, was innerhalb des Blocks getestet wird
  it("renders props.message when passed", () => {
    // hier können weitere Variablen mit const initialisiert werden,
    // die anschließend im props-Teil übergeben werden, damit der expect-Teil sie findet
    const message = "Hello_World";
    // `wrapper` ist ein Objekt, das verschiedene Methoden und props enthält, im Bezug auf die übergebene Komponente
    // TheSnackbar ist die Komponente, die getestet werden soll
    const wrapper = shallowMount(TheSnackbar, {
      global: {
        plugins: [pinia, vuetify],
      },
      props: { message: message },
    });

    expect(wrapper.html()).toContain(message);
  });

  // Beispieltest für das Verständnis was der Unterschied zwischen toBe und toEqual ist
  it(".toBe vs .toEqual", () => {
    // .toBe führt eine strikte Gleichheitsprüfung durch (===)
    // --> Wert + Typ müssen übereinstimmen:
    expect(1).toBe(1); // pass
    // .toBe vergleicht zwei Referenzobjekte und nicht deren Inhalte, daher:
    const x = { a: 1 };
    const y = { a: 1 };
    //expect(x).toBe(y); // fail, weil es unterschiedliche Objekte sind: "AssertionError: expected { a: 1 } to be
    // { a: 1 } // Object.is equality"

    // .toEqual führt eine tiefe Gleichheitsprüfung durch
    // --> Wert + Struktur werden auf allen Ebenen verglichen (also zusätzlich Vergleich der Inhalte):
    expect(x).toEqual(y); // pass
    //expect(1).toEqual("1");   // fail, weil der datentyp nicht gleich ist: "AssertionError: expected 1 to deeply equal '1'"
  });
});
