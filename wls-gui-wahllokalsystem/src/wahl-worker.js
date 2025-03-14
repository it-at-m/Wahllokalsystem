/* eslint-disable no-console */
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
  _setItemInIDB("testkey", "testvalue", "", false)
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
registerRoute(new RegExp("/api/.+"), getRequestHandler, "GET");

// POST-Requests
registerRoute(new RegExp("/api/.+"), postRequestHandler, "POST");

/*****************************************************************************************************************
 * handler functions
 ****************************************************************************************************************/
async function postRequestHandler(event) {
  log("POST request identified");

  try {
    // body can only be read once so a clone is needed to extract data for saving in idb
    const requestClone = event.request.clone();

    // check if there is a body sent with post request (eg broadcastMessageRead has no body).
    // if no "" is returned to be saved as value in idb
    const requestBody = await requestClone
      .json()
      .catch(() => "last message has been read"); // string is specific for broadcast service
    const response = await fetch(event.request);
    // set dirty flag to data that has to be synchronized
    if (response.ok) {
      await _setItemInIDB(
        "lastPostedData",
        requestBody,
        requestClone.url,
        false
      );
    } else {
      await _setItemInIDB(
        "lastPostedData",
        requestBody,
        requestClone.url,
        true
      );
    }

    // return original response
    return response;
  } catch (error) {
    console.error("Error saving to IDB:", error);

    // return original response
    return await fetch(event.request);
  }
}

async function getRequestHandler(event) {
  log("GET request identified");

  try {
    const response = await fetch(event.request);

    try {
      if (!response.ok) {
        // get idb data as fallback
        const storedData = await _getItemFromIDB("lastPostedData");
        if (storedData) {
          log("fetched from idb: " + JSON.stringify(storedData));
          return new Response(JSON.stringify(storedData), {
            status: 200,
            statusText: "fetched from idb",
          });
        } else {
          return new Response(
            JSON.stringify({
              error: "no data found in idb",
              status: 500,
            }),
            { status: 500 }
          );
        }
      }
    } catch (error) {
      console.error("Error fetching idb data:", error);
    }

    // return original response
    return response;
  } catch (error) {
    console.error("Error fetching remote data:", error);

    // get idb data as fallback not only when response is not ok but also when fetching fails entirely
    const storedData = await _getItemFromIDB("lastPostedData");
    if (storedData) {
      log("fetched from idb: " + JSON.stringify(storedData));
      return new Response(JSON.stringify(storedData), {
        status: 200,
        statusText: "fetched from idb",
      });
    } else {
      return new Response(
        JSON.stringify({
          error: "no data found in idb",
          status: 500,
        }),
        { status: 500 }
      );
    }
  }
}

/*****************************************************************************************************************
 * idb utility
 ****************************************************************************************************************/
function _setItemInIDB(key, data, url, dirty) {
  log("saving data - value: " + JSON.stringify(data) + ", dirty: " + dirty);
  const value = { data: data, url: url, dirty: dirty };
  return localforage.setItem(key, value);
}

async function _getItemFromIDB(key) {
  try {
    return await localforage.getItem(key);
  } catch (error) {
    console.error("Fehler beim Laden aus IDB:", error);
    return null;
  }
}

/*****************************************************************************************************************
 * utility
 ****************************************************************************************************************/
function log(message) {
  if (doLog) {
    console.log(logID + message);
  }
}
