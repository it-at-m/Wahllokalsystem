/// <reference lib="WebWorker" />
import type { RouteHandlerCallbackOptions } from "workbox-core";

import { clientsClaim } from "workbox-core";
import { cleanupOutdatedCaches } from "workbox-precaching";
import { registerRoute } from "workbox-routing";

import { useLogging } from "@/composables/common/logging.ts";
import { useOfflineStrategies } from "@/composables/common/offlineStrategies.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";

// declare let self: any;
declare let self: ServiceWorkerGlobalScope;

const { setItemInIDB: _setItemInIDB, setupIndexDB } = useIndexDB();
const { findStrategy, handleRouteWithStrategy } = useOfflineStrategies();
const { log, logError } = useLogging("wahl-worker");

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

log(`iteration - 10.3`);

/*****************************************************************************************************************
 * configuration
 ****************************************************************************************************************/
setupIndexDB();

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
  log(`on install - ${new Date()}`);
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
    logError("Error saving to IDB:", error);

    // return original response
    return await fetch(event.request);
  }
}

async function getRequestHandler(options: RouteHandlerCallbackOptions) {
  log(`GET request identified - uri: ${options.url}`);

  const strategy = findStrategy(options.request);
  return await handleRouteWithStrategy(options, strategy);
}

log("installed and took control");
