import { shallowMount } from "@vue/test-utils";
import { createPinia } from "pinia";
import { beforeAll, beforeEach, describe, expect, it } from "vitest";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

import TheSnackbar from "@/components/TheSnackbar.vue"; // funktion oder klasse die getestet werden soll

// = state management bibliothek: zentrale verwaltung des zustands der anwendung und erleichterung der kommunikation zwischen komponenten
const pinia = createPinia();

// describe ist wie klassenfunktion --> describe funktionen können geschachtelt werden, um tests zu gruppieren und ordnen
// beschreibt, was getestet wird
describe("Beispiele und Erklärungen", () => {
  // initialisiert Variable, die hinterher vor jedem test zu einer vuetify instanz wird --> beforeEach()
  let vuetify: ReturnType<typeof createVuetify>;

  // wird einmal vor allen Tests ausgeführt
  beforeAll(() => {
    createPinia();
    // definition, dass vuetify benutzt wird
    createVuetify();
  });

  //wird vor jedem test ausgeführt
  beforeEach(() => {
    // erstellt vuetify-bibliotheks-instanz, damit in den Tests die vue-Komponenten verwendet werden können
    vuetify = createVuetify({
      components,
      directives,
    });
  });

  // test um zu testen ob tests funktionieren
  it("true test", () => {
    expect(true).toBe(true);
  });

  // it: einzelner test block mit beschreibung, was innerhalb des blocks getestet wird
  it("renders props.message when passed", () => {
    // hier können weitere variablen mit const initialisiert werden,
    // die anschließend im props teil übergeben werden, damit der expect teil sie findet
    const message = "Hello_World";
    // wrapper ist ein objekt, das verschiedene methoden und props enthält, im bezug auf die übergebene komponente
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
    //expect(x).toBe(y);      // fail, weil es unterschiedliche Objekte sind: "AssertionError: expected { a: 1 } to be { a: 1 } // Object.is equality"

    // .toEqual führt eine tiefe Gleichheitsprüfung durch
    // --> Wert + Struktur werden auf allen Ebenen verglichen (also zusätzlich Vergleich der Inhalte):
    expect(x).toEqual(y); // pass
    //expect(1).toEqual("1");   // fail, weil der datentyp nicht gleich ist: "AssertionError: expected 1 to deeply equal '1'"
  });
});
