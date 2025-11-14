<template>
  <v-container max-width="800px">
    <div>
      <v-btn @click="onLogoutClicked">Logout</v-btn><br />
      {{ text }}
    </div>
    <base-offline-loading />
  </v-container>
</template>

<script setup lang="ts">
import { ref } from "vue";

import BaseOfflineLoading from "@/components/wlsComponents/BaseOfflineLoading.vue";

const text = ref("");

async function onLogoutClicked() {
  text.value = "";
  // await fetch(
  //   "http://kubernetes.docker.internal:8100/logout",
  //   getPOSTConfig(undefined)
  // );

  try {
    const request = new Request(
      "https://kubernetes.docker.internal:8100/logout",
      {
        method: "GET",
        credentials: "include",
      }
    );
    await fetch(request).catch((reason) => console.log(reason));
  } catch (error) {
    console.log("get logout", error);
  }

  try {
    await fetch("logout", getPOSTConfig(undefined));
  } catch (error) {
    console.log("post logout", error);
  }
  window.location.href = "/";
  text.value = "logout done";
}

function getPOSTConfig(body: unknown): RequestInit {
  return {
    method: "POST",
    body: getBody(body),
    headers: getHeaders(),
    mode: "cors",
    credentials: getCredentials(),
    redirect: "manual",
  };
}

function getGETTConfig(): RequestInit {
  return {
    method: "GET",
    headers: getHeaders(),
    mode: "cors",
    credentials: getCredentials(),
    redirect: "manual",
  };
}

function getHeaders(): Headers {
  const headers = new Headers({
    "Content-Type": "application/json",
  });
  const csrfCookie = _getXSRFToken();
  if (csrfCookie !== "") {
    headers.append("X-XSRF-TOKEN", csrfCookie);
  }
  return headers;
}

function _getXSRFToken(): string {
  const help = document.cookie.match(
    "(^|;)\\s*" + "XSRF-TOKEN" + "\\s*=\\s*([^;]+)"
  );
  return (help ? help.pop() : "") as string;
}

function getBody(body: unknown): string | undefined {
  if (!body) {
    return undefined;
  } else if (typeof body == "string") {
    return body;
  } else {
    return JSON.stringify(body);
  }
}

function getCredentials(): RequestCredentials {
  // return import.meta.env.MODE === "developmentSecurity" ||
  //   import.meta.env.MODE === "development"
  //   ? "include"
  //   : "same-origin";
  return "include";
}
</script>
