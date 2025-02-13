/* eslint-disable no-console */
/* eslint-disable no-undef */
import CryptoJS from "crypto-js";
import localforage from "localforage";
import { clientsClaim } from "workbox-core";
import { cleanupOutdatedCaches } from "workbox-precaching";
import { registerRoute } from "workbox-routing";

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

/**
 * delete old assets from previous sw versions
 */
cleanupOutdatedCaches();

console.info(logID + "1 nicer Wahl Arbeiter am Start vong her.");

/**
 * automatically adds an activate event listener and calls self.clients.claim() within it.
 * takes over clients: no reload required
 */
clientsClaim();
console.info(logID + "clients claimed");

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

self.addEventListener("message", (event) => {
  log("pin received: " + event.data);
  self.pin = event.data;
  setPin(self.pin);

  if (event.data && event.data.type === "SKIP_WAITING") self.skipWaiting();
});

self.addEventListener("install", () => {
  // skipWaiting() forces a newly installed and waiting sw to get active
  // --> no restart is required for the new sw to take control
  self.skipWaiting();
  log("installed and took control");
});

/*****************************************************************************************************************
 * fetch handling
 ****************************************************************************************************************/

/**
 * The data from GET requests is first attempted to be loaded from the IDB (handleGETofflineFirst),
 * otherwise it is loaded remotely (and then cached in the IDB; handleGETonlineFirst).
 */
registerRoute(
  ({ request }) => request.method === "GET",
  (event) =>
    processRequest(event, (event) => {
      const strat = event.request.headers.get(STRATEGY_HEADER);
      return strat === STRATEGY_ONLINE_FIRST
        ? handleGETonlineFirst(event)
        : handleGETofflineFirst(event);
    })
);

/**
 * When sending POST requests, the data is first saved in the IDB.
 */
registerRoute(
  ({ request }) => request.method === "POST",
  (event) => processRequest(event, handlePOST),
  "POST"
);

async function processRequest(event, handleRequestMethod) {
  log(`Interrupting ${event.request.method} request for ${event.request.url}`);

  if (self.isResponsible(event)) {
    await getPin(); // if pin found WahlworkerState is set to ACTIVE, otherwise it es set to INACTIVE
    if (self.state === WahlworkerState.ACTIVE) {
      return handleRequestMethod(event);
    } else {
      // forwarding request if no pin is found
      return performRemoteRequest(event);
    }
  } else {
    return false;
  }
}

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
    let sending_ergebnismeldung = event.request.url.includes(
      "ergebnismeldung/businessActions/sendErgebnismeldung"
    );
    return self
      .performRemoteRequest(event)
      .then((response) => {
        // save as dirty = true, if we don't get back one of [200, 201, 204]
        let dirty = StatusCodeOk.indexOf(response.status) === -1;
        return setItemAndContentTypeinIDB(key, data, contentType, dirty).then(
          () => {
            // forward response if redirect, status ok or sending ergebnismeldung
            if (
              response.type === "opaqueredirect" ||
              dirty === false ||
              sending_ergebnismeldung
            )
              return response;
            // If we get an error, don't forward this to the client.
            // This can continue to work normally and be synchronized later if necessary.
            else {
              this.log(
                `POST response not OK, statuscode was ${response.status}`
              );
              return new Response(null, {
                status: 200,
                statusText: `returning OK - orig. statuscode is ${response.status}`,
              });
            }
          }
        );
      })
      .catch(() => {
        return setItemAndContentTypeinIDB(key, data, contentType, true).then(
          () => {
            // forward that sending was not successful
            if (sending_ergebnismeldung) {
              return new Response(null, {
                status: 500,
                statusText: "sending Ergebnismeldung - post not possible",
              });
            } else {
              // return ok: request saved in IDB and has to be synchronized later
              return new Response(null, {
                status: 200,
                statusText: "OK - post not possible",
              });
            }
          }
        );
      });
  });
};

self.handleGETofflineFirst = (event) => {
  log(" handle GET ");
  let key = event.request.url;
  return getItemFromIDB(key)
    .then(_handleGetIDB)
    .catch(() => {
      // if no data found in IDB fetch from remote
      return self
        .performRemoteRequest(event) // fetch event holt die daten vom server (online)
        .then((response) => self._handleGetRemote(key, response, false));
    });
};

self.handleGETonlineFirst = (event) => {
  log(" handle GET ONLINE FIRST");
  let key = event.request.url;
  return self
    .performRemoteRequest(event)
    .then((response) => self._handleGetRemote(key, response, true))
    .catch((badResponse) => {
      return getItemFromIDB(key).then((data) =>
        self._handleGetIDB(data, badResponse)
      );
    });
};

/**
 * Saves data from a GET request to the IDB if successful.
 * @param key - request url
 * @param response - fetch response
 * @param rejectResponse - rejects response if not successful
 */
self._handleGetRemote = (key, response, rejectResponse = false) => {
  let responseClone = response.clone(),
    contentType = response.headers.has(ContentTypes.HEADERNAME)
      ? response.headers.get(ContentTypes.HEADERNAME)
      : ContentTypes.JSON;
  // code one of [200, 201, 204]
  if (StatusCodeOk.indexOf(response.status) !== -1) {
    return self.blobOrTxtProm(response, contentType).then((data) => {
      return setItemAndContentTypeinIDB(
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
    // when code not in [200, 201, 204]
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
    let rsp = new Response(data.data, {
      status: data.status,
      statusText: "Wahlworker at your service.",
    });
    // set content type according to the value that was previously saved TODO für alle header?
    rsp.headers.set(ContentTypes.HEADERNAME, data.contentType);
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
self.performRemoteRequest = (event) => {
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
self.addEventListener("push", () => {
  let data = [];

  function KeyVal(key, val) {
    this.key = key;
    this.val = val;
  }

  getPin()
    .then(() => {
      return localforage.iterate((value, key) => {
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
              // eslint-disable-next-line no-empty
            } catch {}
          }
          data.push(new KeyVal(key, value));
        }
      });
    })
    .then(() => {
      console.table(data);
      return localforage.length().then((numberOfKeys) => {
        // Outputs the length of the database.
        log("iteration complete, number of keys: " + numberOfKeys);
      });
    });
});

/*****************************************************************************************************************
 * idb/crypto Abstraction
 ****************************************************************************************************************/
function _setItemInIDB(key, value) {
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
function setItemAndContentTypeinIDB(
  key,
  value,
  contentType,
  dirty,
  responseStatus
) {
  let contentTypeData;
  // when nothing has been found remote
  if (responseStatus === 204) {
    contentTypeData = {
      data: null,
      contentType: contentType,
      dirty: dirty,
      timestamp: new Date(),
      status: responseStatus,
    };
  } else {
    // all other `StatusCodeOk` Codes (201 or 200)
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
  return _setItemInIDB(key, contentTypeData);
}

function getItemFromIDB(key) {
  return localforage.getItem(key).then((item) => {
    if (item === null) return item;
    // decrypt if item.data is not a blob, because blobs are not encrypted
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
  _setItemInIDB(PIN_KEY, pin);
}

function getPin() {
  return localforage.getItem(PIN_KEY).then((pin) => {
    self.pin = pin;
    self.state =
      self.pin === undefined || self.pin === null
        ? WahlworkerState.INACTIVE
        : WahlworkerState.ACTIVE; // can't use the IDB if we don't have a pin because it is used to encrypt the data
  });
}

/*****************************************************************************************************************
 * utility
 ****************************************************************************************************************/

function log(message) {
  if (doLog) console.log(logID + message);
}
