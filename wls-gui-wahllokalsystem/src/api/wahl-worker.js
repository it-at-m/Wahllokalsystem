import CryptoJS from "crypto-js";
import localforage from "localforage";

/*****************************************************************************************************************
 * constants/config
 ****************************************************************************************************************/

const logID = "wahlworker: ";

/**
 * All URLs that contain this string are cached by the SW.
 */
const FILTER = "businessActions";

const StatusCodeOk = [200, 201, 204];

/**
 * State of the SW
 * ACTIVE: intercepts requests and reads/saves in cache.
 * INACTIVE: only passes the requests through.
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
const STRATEGY_ONLINE_ONLY = "ONLINE_ONLY";
const STRATEGY_ONLINE_FIRST = "ONLINE_FIRST";

const PIN_KEY = "PIN";

console.info(logID + "1 nicer Wahl Arbeiter am Start vong her.");

localforage.config({
  driver: localforage.INDEXEDDB, // Force WebSQL; same as using setDriver()
  name: "wahldb",
  version: 1.0,
  storeName: "wahlstore",
  description: "store for wahlnumber",
});

/*****************************************************************************************************************
 * runtime properties
 ****************************************************************************************************************/

self.state = WahlworkerState.INACTIVE;
self.isOnline = true; // TODO update from app? or use window navigator?
self.pin = undefined;

/*****************************************************************************************************************
 * listeners
 ****************************************************************************************************************/

self.addEventListener("activate", (event) => {
  log("activate event");
  // take over clients: no reload required
  event.waitUntil(
    clients.claim().then(() => {
      log("clients claimed");
    })
  );
});

self.addEventListener("message", (event) => {
  log("pin received: " + event.data);
  self.pin = event.data;
  setPin(self.pin);
});

self.addEventListener("install", (event) => {
  //TODO: behalten ? Caution: skipWaiting() means that your new service worker is likely controlling pages that were loaded with an older version. This means some of your page's fetches will have been handled by your old service worker, but your new service worker will be handling subsequent fetches. If this might break things, don't use skipWaiting().
  // This causes your service worker to kick out the current active worker and activate itself as soon as it enters the waiting phase (or immediately if it's already in the waiting phase). It doesn't cause your worker to skip installing, just waiting. -> so behandelt nun alle nachfolgenden fetches-> kein neustart erforderlich.
  event.waitUntil(
    self.skipWaiting().then(() => {
      log("installed and took control");
    })
  );
});

/*****************************************************************************************************************
 * fetch handling
 ****************************************************************************************************************/

/**
 * If responsible for the URL, the data from GET requests is first attempted to be
 * loaded from the IDB, otherwise it is loaded remotely (and then cached in the IDB).
 * When sending POST requests, the data is first saved in the IDB.
 */
self.addEventListener("fetch", (event) => {
  log("Interrupt request for " + event.request.url);
  if (self.isResponsible(event)) {
    // get pin from IDB
    event.respondWith(
      getPin().then(() => {
        // forwarding request if no pin is found
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
 * Returns false if the URL contains the substring FILTER and the
 * HEADER @const STRATEGY_HEADER is set to @const STRATEGY_ONLINE_ONLY.
 * Returns true otherwise.
 */
self.isResponsible = (event) => {
  let isResponsible = event.request.url.includes(FILTER);
  if (event.request.headers.has(STRATEGY_HEADER)) {
    let strat = event.request.headers.get(STRATEGY_HEADER);
    log("strategy found: " + strat);
    if (strat === STRATEGY_ONLINE_ONLY) isResponsible = false;
  }
  log("is responsible : " + isResponsible);
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
    // sending ergebnismeldung should always forward the status
    let is_ergebnismeldung_sent = event.request.url.includes(
      "ergebnismeldung/businessActions/sendErgebnismeldung"
    );
    return self
      .fetchEvent(event)
      .then((response) => {
        // save as dirty if we don't get back ok
        let dirty = StatusCodeOk.indexOf(response.status) === -1;
        return setItemAndContentType(key, data, contentType, dirty).then(() => {
          // redirect 200s and send ergebnismeldung + forward results
          if (
            response.type === "opaqueredirect" ||
            dirty === false ||
            is_ergebnismeldung_sent
          )
            return response;
          // If we get an error, don't forward this to the client.
          // This can continue to work normally and be synchronized later if necessary.
          else {
            this.log(`POST response not OK, statuscode was ${response.status}`);
            return new Response(null, {
              status: 200,
              statusText: `returning OK - orig. statuscode is ${response.status}`,
            });
          }
        });
      })
      .catch(() => {
        return setItemAndContentType(key, data, contentType, true).then(() => {
          // forward that sending was not successful
          if (is_ergebnismeldung_sent) {
            return new Response(null, {
              status: 500,
              statusText: "sending Ergebnismeldung - post not possible",
            });
          } else {
            return new Response(null, {
              status: 200,
              statusText: "OK - post not possible",
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
 * Saves data from a GET request to the IDB if successful.
 * @param key - request url
 * @param response - fetch response
 * @param rejectResponse - rejects response if not successful
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
 * Packs data from the IDB into a response.
 * @param data - data read from the IDB
 * @param responseIfNoData - optional, if no data is available, this responseObject is returned
 */
self._handleGetIDB = (data, responseIfNoData) => {
  // data is {data: .., contentType: .., dirty:.., timestamp: ...}
  if (data === null) {
    log("no data found in local storage");
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
 * Auxiliary method that unpacks the body based on the contentType.
 */
self.blobOrTxtProm = (request, contentType) => {
  // The help manual comes in binary format, so response.blob() has to be used to copy the data.
  if (
    contentType &&
    (contentType.indexOf(ContentTypes.PDF) !== -1 ||
      contentType.indexOf(ContentTypes.CSV) !== -1)
  ) {
    return request.blob();
  } else {
    // In all other cases, .text() is currently sufficient.
    return request.text();
  }
};

/**
 * fetching event
 */
self.fetchEvent = (event) => {
  return fetch(event.request).then((response) => {
    log("fetched data from remote");
    return response;
  });
};

/*****************************************************************************************************************
 * push handling, this makes it easy to test functions.
 ****************************************************************************************************************/

/**
 * Prints all data from the IDB to the console.
 */
self.addEventListener("push", (event) => {
  let data = [];

  function KeyVal(key, val) {
    this.key = key;
    this.val = val;
  }

  event.waitUntil(
    getPin().then(() => {
      localforage
        .iterate((value, key) => {
          if (key !== PIN_KEY) {
            // decrypt if not a blob
            if (!(value.data instanceof Blob)) {
              let decrypted = CryptoJS.AES.decrypt(value.data, self.pin);
              value.data = decrypted.toString(CryptoJS.enc.Utf8);
            }
            // objectify if json string
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
          return localforage.length().then((numberOfKeys) => {
            // Outputs the length of the database.
            log("iteration complete, number of keys: " + numberOfKeys);
          });
        });
    })
  );
});

/*****************************************************************************************************************
 * idb/crypto Abstraction
 ****************************************************************************************************************/
function _setItem(key, value) {
  log("saving data key:" + key + " with value: " + JSON.stringify(value));
  return localforage.setItem(key, value);
}

/**
 * sets an item in the IDB
 * @param {any} key
 * @param {stringified object} value - is encrypted unless it is a blob. Blobs cannot be encrypted.
 * @param {any} contentType - http header
 * @param {boolean} dirty - not yet transferred to the backend
 * @param responseStatus
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
    // blobs are not encrypted
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
        : WahlworkerState.ACTIVE; // can't use the IDB if we don't have a pin
  });
}

/*****************************************************************************************************************
 * utility
 ****************************************************************************************************************/

function log(message) {
  if (doLog) console.log(logID + message);
}
