import localforage from "localforage";
import { clientsClaim } from "workbox-core";
import { cleanupOutdatedCaches } from "workbox-precaching";
import { registerRoute } from "workbox-routing";

/**
 * delete old assets from previous sw versions
 */
cleanupOutdatedCaches();

/**
 * automatically adds an 'activate' event listener and calls self.clients.claim() within it.
 * without calling claim, any existing clients will still continue to talk to the older service worker until a full page reload.
 * --> clientsClaim() takes control over clients: no reload required
 */
clientsClaim();

/*****************************************************************************************************************
 * configuration
 ****************************************************************************************************************/
localforage.config({
  driver: localforage.INDEXEDDB, // Force WebSQL; same as using setDriver()
  name: "wahldb",
  version: 1.0,
  storeName: "wahlstore",
  description: "store for wahlnumber",
});

/*****************************************************************************************************************
 * constants
 ****************************************************************************************************************/
const logID = "wahlworker: ";
const doLog = true;

/*****************************************************************************************************************
 * event listeners
 ****************************************************************************************************************/
/**
 * 'install' event is always the first one sent to a service worker
 * application is preparing to make everything available for offline use
 */
self.oninstall = () => {
  // forces a newly installed (waiting) service worker to become the active one right away
  // --> no restart is required for the new sw to be active
  self.skipWaiting();

  log("installed and took control");

  // to test idb
  _setItemInIDB("testkey", "testvalue")
    .then(() => {
      log("SUCCESS - saved data to idb");
    })
    .catch(() => {
      log("FAILED - couldn't save data to idb");
    });
};

/*****************************************************************************************************************
 * catch api routes
 ****************************************************************************************************************/

// registerRoute ALWAYS expects a Response as return value!!

// GET-Requests
registerRoute(
  ({ request }) => request.method === "GET",
  async (event) => {
    console.log("GET request identified");

    // return original response
    return await fetch(event.request);
  },
  "GET"
);

// POST-Requests
registerRoute(
  ({ request }) => request.method === "POST",
  async (event) => {
    log("POST request identified");

    try {
      // body can only be read once so a clone is needed to extract data for saving in idb
      const requestClone = event.request.clone();

      // check if there is a body sent with post request (eg broadcastMessageRead has no body).
      // if no "" is returned to be saved as value in idb
      const requestBody = await requestClone.json().catch(() => "");
      const response = await fetch(event.request);
      await _setItemInIDB("lastPostedData", requestBody);

      // return original response
      return response;
    } catch (error) {
      console.error("Error saving to IDB:", error);

      // return original response
      return await fetch(event.request);
    }
  },
  "POST"
);

/*****************************************************************************************************************
 * idb utility
 ****************************************************************************************************************/
function _setItemInIDB(key, value) {
  log("saving data - key: " + key + ", value: " + JSON.stringify(value));
  return localforage.setItem(key, value);
}

/*****************************************************************************************************************
 * utility
 ****************************************************************************************************************/
function log(message) {
  if (doLog) console.log(logID + message);
}
