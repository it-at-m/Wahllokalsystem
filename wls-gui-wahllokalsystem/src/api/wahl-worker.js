import CryptoJS from "crypto-js";
import localforage from "localforage";

/*****************************************************************************************************************
 * constants/config
 ****************************************************************************************************************/

const logID = "wahlworker: ";
/**
 * alle URLs die diesen String enthalten werden vom SW gecached.
 */
const FILTER = "businessActions";

/**
 *
 */
const StatusCodeOk = [200, 201, 204];

/**
 * State des SW
 * Active: intercepted Requests und liest/speichert im cache.
 * INACTIVE: leited die Requests nur durch
 */
const WahlworkerState = Object.freeze({
  ACTIVE: 1,
  INACTIVE: 2,
});

const ContentTypes = Object.freeze({
  HEADERNAME: "content-type",
  PDF: "application/pdf",
  JSON: "application/json",
  CSV: "text/csv",
});

/**
 * TODO auf false stellen.
 * true: serviceworker logt mit.
 */
const doLog = true;

const STRATEGY_HEADER = "X-WLS-SW-STRATEGY";
const STRATEGY_OFFLINE_FIRST = "OFFLINE_FIRST";
const STRATEGY_ONLINE_ONLY = "ONLINE_ONLY";
const STRATEGY_ONLINE_FIRST = "ONLINE_FIRST";

const PIN_KEY = "PIN";

console.info(logID + "1 nicer Wahl Arbeiter am Start vong her.");

localforage.config({
  driver: localforage.INDEXEDDB, // Force WebSQL; same as using setDriver()
  name: "wahldb",
  version: 1.0,
  storeName: "wahlstore", // Should be alphanumeric, with underscores.
  description: "store for wahlnumber",
});

/*****************************************************************************************************************
 * runtime propertys
 ****************************************************************************************************************/

self.state = WahlworkerState.INACTIVE;
self.isOnline = true; // TODO update from app? or use window navigator?
self.pin = undefined;

/*****************************************************************************************************************
 * listeners
 ****************************************************************************************************************/

self.addEventListener("activate", function (event) {
  log("aktivate event");
  //übernehme clients: kein reload erforderlich
  event.waitUntil(
    clients.claim().then(() => {
      log("clients claimed");
    })
  );
});

self.addEventListener("message", function (event) {
  log("pin erhalten: " + event.data);
  self.pin = event.data;
  setPin(self.pin);
});

self.addEventListener("install", (event) => {
  //TODO: behalten ? Caution: skipWaiting() means that your new service worker is likely controlling pages that were loaded with an older version. This means some of your page's fetches will have been handled by your old service worker, but your new service worker will be handling subsequent fetches. If this might break things, don't use skipWaiting().
  // This causes your service worker to kick out the current active worker and activate itself as soon as it enters the waiting phase (or immediately if it's already in the waiting phase). It doesn't cause your worker to skip installing, just waiting. -> so behandelt nun alle nachfolgenden fetches-> kein neustart erforderlich.
  event.waitUntil(
    self.skipWaiting().then(() => {
      log("installiert und kontrolle übernommen.");
    })
  );

  //    event.waitUntil(
  // caching etc
  //  );
});

/*****************************************************************************************************************
 * fetch handling
 ****************************************************************************************************************/

/**
 * falls verantwortlich für die ULR werden die Daten
 * beim Get zuerst aus der idb versucht zu laden, sonst von remote geladen(anschließend in der idb gecached).
 * beim Post diese zuvor in der idb gespeichert.
 */
self.addEventListener("fetch", function (event) {
  log("Unterbreche Request für " + event.request.url);
  if (self.isResponsible(event)) {
    // pin aus der idb holen.
    event.respondWith(
      getPin().then(() => {
        // falls kein pin gefunden, request einfach weiterleiten.
        if (self.state === WahlworkerState.ACTIVE) {
          if (event.request.method === "GET") {
            let strat = event.request.headers.get(STRATEGY_HEADER);
            if (strat === STRATEGY_ONLINE_FIRST)
              return self.handleGETonlineFirst(event);
            else return self.handleGET(event);
          } else if (event.request.method === "POST") {
            return self.handlePOST(event);
          }
        } else {
          return self.fetchEvent(event);
        }
      })
    );
  } else {
    return false;
  }
});

/**
 * Gibt true zurück falls die URL den Substring FILTER enthält
 * und zusätzlich der HEADER @const STRATEGY_HEADER
 * auf @const STRATEGY_ONLINE_ONLY gesetzt, false  zurück
 * auf @const STRATEGY_OFFLINE_FIRST gesetzt, true zurück
 * auf @const STRATEGY_ONLINE_ONLY gesetzt, true zurück
 */
self.isResponsible = (event) => {
  let isResponsible = event.request.url.includes(FILTER);
  if (event.request.headers.has(STRATEGY_HEADER)) {
    let strat = event.request.headers.get(STRATEGY_HEADER);
    log("strategy found: " + strat);
    if (strat === STRATEGY_ONLINE_ONLY) isResponsible = false;
  }
  log(" is responsible : " + isResponsible);
  return isResponsible;
};

self.handlePOST = (event) => {
  log(" handle POST ");
  let key = event.request.url,
    clonedRequest = event.request.clone(),
    contentType = clonedRequest.headers.has(ContentTypes.HEADERNAME)
      ? clonedRequest.headers.get(ContentTypes.HEADERNAME)
      : ContentTypes.JSON;
  return self.blobOrTxtProm(clonedRequest, contentType).then((data) => {
    // ergebnismeldung senden soll status immer weiterreichen
    let is_ergebnismeldung_send = event.request.url.includes(
      "ergebnismeldung/businessActions/sendErgebnismeldung"
    );
    return self
      .fetchEvent(event)
      .then(function (response) {
        // als dirty speichern wenn wir nicht ok zurückbekommen
        let dirty = StatusCodeOk.indexOf(response.status) === -1;
        return setItemAndContentType(key, data, contentType, dirty).then(
          function () {
            // redirect, 200er, und ergebnnismeldung senden ergebnise durchreichen
            if (
              response.type === "opaqueredirect" ||
              dirty === false ||
              is_ergebnismeldung_send
            )
              return response;
            // sollten wir einen Fehler bekommen, dies nicht an den client weiterleiten. Dieser kann normal weiterarbeiten und später
            // bei dedarf synchronisieren.
            else {
              this.log(
                `POST response not OK, Statuscode was ${response.status}`
              );
              var init = {
                status: 200,
                statusText: `returning OK - orig. Statuscode is ${response.status}`,
              };
              return new Response(null, init);
            }
          }
        );
      })
      .catch(function () {
        return setItemAndContentType(key, data, contentType, true).then(() => {
          // weiterreichen das senden nicht erfolgreich war
          if (is_ergebnismeldung_send) {
            return new Response(null, {
              status: 500,
              statusText: "Ergebnismeldung send - post nicht möglich",
            });
          } else {
            return new Response(null, {
              status: 200,
              statusText: "OK - post nicht möglich",
            });
          }
        });
      });
  });
};

self.handleGET = (event) => {
  log(" handle GET ");
  let key = event.request.url;
  return getItem(key)
    .then(_handleGetIDB)
    .catch(() => {
      return self
        .fetchEvent(event)
        .then((response) => self._handleGetFetch(key, response, false));
    });
};

self.handleGETonlineFirst = (event) => {
  log(" handle GET ONLINE FIRST");
  let key = event.request.url;
  return self
    .fetchEvent(event)
    .then((response) => self._handleGetFetch(key, response, true))
    .catch((badResponse) => {
      return getItem(key).then((data) => self._handleGetIDB(data, badResponse));
    });
};

/**
 * speichert Daten aus einem get Fetch in der IDB falls erfolgreich.
 * @param key: url des requests
 * @param response: response des fetches
 * @param rejectResponse: rejected den response, falls nicht erfolgreich
 */
self._handleGetFetch = (key, response, rejectResponse = false) => {
  let responseClone = response.clone(),
    contentType = response.headers.has(ContentTypes.HEADERNAME)
      ? response.headers.get(ContentTypes.HEADERNAME)
      : ContentTypes.JSON;
  if (StatusCodeOk.indexOf(response.status) !== -1) {
    return self.blobOrTxtProm(response, contentType).then((data) => {
      return setItemAndContentType(
        key,
        data,
        contentType,
        false,
        response.status
      ).then(() => {
        return responseClone;
      });
    });
  } else {
    return rejectResponse ? Promise.reject(responseClone) : responseClone;
  }
};

/**
 * verpackt Daten aus der IDB in ein Response.
 * @param data: Daten die aus der IDB gelesen wurden.
 * @param responseIfNoData: optional, falls keine Daten vorhanden, wird dieses ResponseObjekt zurückgegeben.
 */
self._handleGetIDB = (data, responseIfNoData) => {
  // data ist {data: .., contentType: .., dirty:.., timestamp: ...}
  if (data === null) {
    log("keine Daten im lokalen Speicher gefunden.");
    return responseIfNoData
      ? Promise.reject(responseIfNoData)
      : Promise.reject();
  } else {
    var init = {
      status: data.status,
      statusText: "Wahlworker at your service.",
    };

    let rsp = new Response(data.data, init);
    rsp.headers.set(ContentTypes.HEADERNAME, data.contentType); // content type setzen nach dem Wert der zuvor gespeichert worden ist. TODO für alle header?
    return rsp;
  }
};

/**
 * Hilfsmethode die anhand dem contenType den Body auspackt.
 */
self.blobOrTxtProm = (request, contentType) => {
  // Das Hilfehandbuch kommt im Binärformat, daher muss hier response.blob() genutzt werden um die Daten zu kopieren.
  if (
    contentType &&
    (contentType.indexOf(ContentTypes.PDF) !== -1 ||
      contentType.indexOf(ContentTypes.CSV) !== -1)
  ) {
    return request.blob();
  } else {
    // In allen anderen Fällen reicht aktuell .text() aus.
    return request.text();
  }
};

/**
 * event fetchen
 */
self.fetchEvent = (event) => {
  return fetch(event.request).then(function (response) {
    log("fetched data from remote");
    return response;
  });
};

/*****************************************************************************************************************
 * push handling, hiermit kann man funktionen einfach testen.
 ****************************************************************************************************************/

/**
 * gibt alle Daten der idb auf der Konsole aus.
 */
self.addEventListener("push", function (event) {
  let data = [];

  function KeyVal(key, val) {
    this.key = key;
    this.val = val;
  }

  event.waitUntil(
    getPin().then(() => {
      localforage
        .iterate(function (value, key) {
          if (key !== PIN_KEY) {
            //decrypt falls kein blob
            if (!(value.data instanceof Blob)) {
              let decrypted = CryptoJS.AES.decrypt(value.data, self.pin);
              value.data = decrypted.toString(CryptoJS.enc.Utf8);
            }
            //objectify if json string
            if (
              value.contentType.includes(ContentTypes.JSON) &&
              value.data &&
              value.data !== ""
            ) {
              try {
                value.data = JSON.parse(value.data);
              } catch (error) {}
            }
            data.push(new KeyVal(key, value));
          }
        })
        .then(() => {
          console.table(data);
          return localforage.length().then(function (numberOfKeys) {
            // Outputs the length of the database.
            log("iteration complete, number of keys: " + numberOfKeys);
          });
        });
    })
  );
});

/*****************************************************************************************************************
 * idb/crypto Abstraktion
 ****************************************************************************************************************/
function _setItem(key, value) {
  log("speichere data key:" + key + " mit value: " + JSON.stringify(value));
  return localforage.setItem(key, value);
}

/**
 * setzt ein item in der idb
 * @param {any} key
 * @param {stringified object} value wird verschlüsselt, außer es handelt sich um einen Blob. Blobs können nicht verschlüsselt werden.
 * @param {any} contentType http header
 * @param {boolean} dirty noch nicht ans backend übertragen
 * @returns
 */
function setItemAndContentType(key, value, contentType, dirty, responseStatus) {
  let contentTypeData;
  if (responseStatus === 204) {
    contentTypeData = {
      data: null,
      contentType: contentType,
      dirty: dirty,
      timestamp: new Date(),
      status: responseStatus,
    };
  } else {
    let encrypted =
      value instanceof Blob
        ? value
        : CryptoJS.AES.encrypt(value, self.pin).toString();
    contentTypeData = {
      data: encrypted,
      contentType: contentType,
      dirty: dirty,
      timestamp: new Date(),
      status: 200,
    };
  }
  return _setItem(key, contentTypeData);
}

function getItem(key) {
  return localforage.getItem(key).then((item) => {
    if (item === null) return item;
    // blobs sind nicht verschlüsselt.
    if (!(item.data instanceof Blob)) {
      if (item.status !== 204) {
        let decrypted = CryptoJS.AES.decrypt(item.data, self.pin);
        item.data = decrypted.toString(CryptoJS.enc.Utf8);
      }
    }
    return item;
  });
}

function setPin(pin) {
  _setItem(PIN_KEY, pin);
}

function getPin() {
  return localforage.getItem(PIN_KEY).then((pin) => {
    self.pin = pin;
    self.state =
      self.pin === undefined || self.pin === null
        ? WahlworkerState.INACTIVE
        : WahlworkerState.ACTIVE; // kann die idb nicht verwenden wenn wir keine pin haben.
  });
}

/*****************************************************************************************************************
 * utility
 ****************************************************************************************************************/

function log(message) {
  if (doLog) console.log(logID + message);
}
