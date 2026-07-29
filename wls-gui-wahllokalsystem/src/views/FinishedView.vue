<template>
  <div class="container">
    <h1 class="main-title">
      {{ hasRoleErfassungsteam ? "Stimmzettelerfassung" : "Auszählung" }}
      abgeschlossen
    </h1>
    <div class="icon-container">
      <svg
        class="checkmark self-center"
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 52 52"
        aria-hidden="true"
        focusable="false"
      >
        <circle
          class="checkmark__circle"
          cx="26"
          cy="26"
          r="25"
          fill="none"
        />
        <path
          class="checkmark__check"
          fill="none"
          d="M14.1 27.2l7.1 7.2 16.7-16.8"
        />
      </svg>
    </div>
    <h2 class="sub-title">
      Vielen Dank für Ihre Unterstützung als Wahlvorstand.
    </h2>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import { useUserStore } from "@/stores/userStore.ts";

const { hasRoleErfassungsteam } = storeToRefs(useUserStore());
</script>

<style scoped>
.container {
  text-align: center;
  padding: 20px;
}

.main-title {
  font-size: 30px;
  margin-bottom: 20px;
}

.sub-title {
  font-size: 24px;
  margin-top: 20px;
}
.icon-container {
  position: relative;
  width: 50px;
  height: 50px;
  margin: 0 auto;
}

svg {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: block;
}

.checkmark {
  stroke-width: 2;
  stroke: #fff;
  stroke-miterlimit: 10;
  box-shadow: inset 0px 0px 0px rgb(var(--v-theme-success));
  animation:
    fill 0.4s ease-in-out 0.4s forwards,
    scale 0.3s ease-in-out 0.9s both;
}

.checkmark__circle {
  stroke-dasharray: 166;
  stroke-dashoffset: 166;
  stroke-width: 2;
  stroke-miterlimit: 10;
  stroke: rgb(var(--v-theme-success));
  fill: none;
  animation: stroke 0.6s cubic-bezier(0.65, 0, 0.45, 1) forwards;
}

.checkmark__check {
  transform-origin: 50% 50%;
  stroke-dasharray: 48;
  stroke-dashoffset: 48;
  animation: stroke 0.3s cubic-bezier(0.65, 0, 0.45, 1) 0.8s forwards;
}

@keyframes stroke {
  100% {
    stroke-dashoffset: 0;
  }
}

@keyframes scale {
  0%,
  100% {
    transform: none;
  }
  50% {
    transform: scale3d(1.1, 1.1, 1);
  }
}

@keyframes fill {
  100% {
    box-shadow: inset 0px 0px 0px 30px rgb(var(--v-theme-success));
  }
}

@media (prefers-reduced-motion: reduce) {
  .checkmark,
  .checkmark__circle,
  .checkmark__check {
    animation: none;
  }

  .checkmark {
    box-shadow: inset 0px 0px 0px 30px rgb(var(--v-theme-success));
  }

  .checkmark__circle,
  .checkmark__check {
    stroke-dashoffset: 0;
  }
}
</style>
