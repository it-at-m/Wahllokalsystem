import { describe, expect, it } from "vitest";

// `describe`-Funktionen können geschachtelt werden, um Tests zu gruppieren und ordnen. Es ist immer eine Top-Level
// `describe`-Funktion notwendig.
describe("Beispiele und Erklärungen", () => {
  // Test um zu testen ob Tests funktionieren
  it("true test", () => {
    expect(true).toBe(true);
  });

  // Beispieltest für das Verständnis was der Unterschied zwischen toBe und toEqual ist
  it(".toBe vs .toEqual vs .toStrictEqual", () => {
    // .toBe führt eine strikte Gleichheitsprüfung durch (===)
    // --> Wert + Typ müssen übereinstimmen:
    expect(1).toBe(1); // pass: beides ist 1 und beides ist eine Zahl
    // .toBe vergleicht zwei Referenzobjekte und nicht deren Inhalte, daher:
    const w = { a: 1, b: undefined };
    const x = { a: 1 };
    const y = { a: 1 };
    const z = x;
    expect(x).toBe(z); // pass: das Objekt ist das gleiche
    //expect(x).toBe(y); // fail, weil es unterschiedliche Objekte sind: "AssertionError: expected { a: 1 } to be
    // { a: 1 } // Object.is equality"

    // .toEqual führt eine tiefe Gleichheitsprüfung durch
    // --> Wert + Struktur/Inhalte müssen übereinstimmen:
    expect(x).toEqual(y); // pass: beide Objekte (x und y) haben die gleichen Werte
    expect(w).toEqual(y); // pass: `b: undefined` wird bei der Überprüfung nicht berücksichtigt und somit sind die Objekte gleich
    //expect(1).toEqual("1");   // fail, weil der datentyp nicht gleich ist: "AssertionError: expected 1 to deeply equal '1'"

    // .toStrictEqual prüft zusätzlich die Typen
    // --> Wert + Struktur/Inhalte + Typ müssen übereinstimmen:
    expect(x).toStrictEqual(y); // pass: das Objekt ist gleich und hat den gleichen Aufbau und die gleichen Typen
    // expect(w).toStrictEqual(y); // fail: im Gegensatz zu `.toEqual` wird `b: undefined` hier berücksichtigt
    expect(w).not.toStrictEqual(y); // pass
    // expect({ a: "1" }).toStrictEqual(x); // fail, weil die 1 ein Mal eine Zahl ist und ein Mal ein String
    // expect(w).toStrictEqual(x); // fail, weil es eine zusätzliche Eigenschaft gibt.
  });
});
