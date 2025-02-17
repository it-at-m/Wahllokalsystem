import { clientsClaim } from "workbox-core";
import { cleanupOutdatedCaches } from "workbox-precaching";

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

const logID = "wahlworker: ";
const doLog = true;

/*****************************************************************************************************************
 * event listeners
 ****************************************************************************************************************/
/**
 * 'install' event is always the first one sent to a service worker
 * Todo: start the process of populating an IndexedDB, and caching site assets
 * application is preparing to make everything available for offline use
 */
self.oninstall = () => {
  // forces a newly installed (waiting) service worker to become the active one right away
  // --> no restart is required for the new sw to be active
  self.skipWaiting();

  log("installed and took control");
};

/*****************************************************************************************************************
 * utility
 ****************************************************************************************************************/
function log(message) {
  if (doLog) console.log(logID + message);
}
