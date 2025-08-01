/// <reference lib="WebWorker" />
/* eslint-disable no-console */
import type { RouteHandlerCallbackOptions } from "workbox-core";

import localforage from "localforage";
import { clientsClaim } from "workbox-core";
import { cleanupOutdatedCaches } from "workbox-precaching";
import { registerRoute } from "workbox-routing";

import { useIndexDB } from "@/composables/indexDB/indexDB.ts";

// declare let self: any;
declare let self: ServiceWorkerGlobalScope;

const {
  getItemFromIDB: _getItemFromIDB,
  setItemInIDB: _setItemInIDB,
  setupIndexDB,
} = useIndexDB();

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

console.log(`iteration - 10.2`);
// log(`iteration - 10.2`);

/*****************************************************************************************************************
 * configuration
 ****************************************************************************************************************/
setupIndexDB();
console.log(`before wait`);

console.log(`after wait`);

/*****************************************************************************************************************
 * constants
 ****************************************************************************************************************/
const logID = "wahlworker: ";
const doLog = true;

/*****************************************************************************************************************
 * event listeners
 ****************************************************************************************************************/
self.addEventListener("message", function (event) {
  log("pin erhalten: " + event.data);
});

/**
 * 'install' event is always the first one sent to a service worker
 * application is preparing to make everything available for offline use
 */
self.oninstall = () => {
  console.log(`on install - 1`);
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
async function postRequestHandler(event: RouteHandlerCallbackOptions) {
  log(`POST request identified - uri: ${event.url}`);

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

async function getRequestHandler(event: RouteHandlerCallbackOptions) {
  log(`GET request identified - uri: ${event.url}`);

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
 * utility
 ****************************************************************************************************************/
function log(message: string) {
  if (doLog) {
    console.log(logID + message);
  }
}
log("installed and took control");
