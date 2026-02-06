/// <reference lib="WebWorker" />
import type { ServiceWorkerMessage } from "@/types/serviceWorker/ServiceWorkerMessage.ts";
import type { RouteHandlerCallbackOptions } from "workbox-core";

import { clientsClaim } from "workbox-core";
import { cleanupOutdatedCaches } from "workbox-precaching";
import { registerRoute } from "workbox-routing";

import { useRequestStrategyManager } from "@/composables/api/RequestStrategyManager.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";
import { ServiceWorkerMessageTypeEnum } from "@/types/serviceWorker/ServiceWorkerMessageTypeEnum.ts";

// declare let self: any;
declare let self: ServiceWorkerGlobalScope;

const indexDBSingleton = useIndexDB();
const { handleRequestWithStrategy } = useRequestStrategyManager();
const { log } = useLogging("wahl-worker");

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
indexDBSingleton.setupIndexDB();

/*****************************************************************************************************************
 * event listeners
 ****************************************************************************************************************/
self.addEventListener("message", function (event) {
  log("message erhalten: " + event.data);
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
async function postRequestHandler(options: RouteHandlerCallbackOptions) {
  log(`POST request identified - uri: ${options.url}`);
  return await handleRequestWithStrategy(options);
}

async function getRequestHandler(options: RouteHandlerCallbackOptions) {
  log(`GET request identified - uri: ${options.url}`);
  return await handleRequestWithStrategy(options);
}

const installedMessage: ServiceWorkerMessage = {
  type: ServiceWorkerMessageTypeEnum.SERVICE_WORKER_INSTALLED,
  payload: undefined,
};
self.clients.matchAll().then((match) => {
  log(`count clients: ${match.length}`);
  match.forEach((client) => {
    log(
      `sending message that service worker is installed to client: ${client.id} URL: ${client.url} Type: ${client.type}`
    );
    client.postMessage(installedMessage);
  });
});

log("installed and took control");
