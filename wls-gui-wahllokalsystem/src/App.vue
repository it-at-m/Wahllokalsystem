<template>
  <v-app>
    <the-snackbar />
    <v-app-bar color="primary">
      <v-row align="center">
        <v-col
          cols="3"
          class="d-flex align-center justify-start"
        >
          <v-app-bar-nav-icon @click.stop="toggleDrawer()" />
          <router-link to="/">
            <v-toolbar-title class="font-weight-bold">
              <span class="text-white">WLS</span>
            </v-toolbar-title>
          </router-link>
        </v-col>
        <v-col
          cols="6"
          class="d-flex align-center justify-center"
        ></v-col>
        <v-col
          cols="3"
          class="d-flex align-center justify-end"
        >
          <v-tooltip
            location="bottom"
            text="Routing Examples"
          >
            <template v-slot:activator="{ props }">
              <router-link
                v-bind="props"
                :to="{ name: EXAMPLE_ROUTES_NEWROUTE }"
              >
                <v-btn
                  icon="$routes"
                  variant="text"
                  density="comfortable"
                  size="x-large"
                  color="white"
                >
                </v-btn>
              </router-link>
            </template>
          </v-tooltip>
          <v-btn
            variant="text"
            icon
          >
            <ad2-image-avatar
              v-if="userStore.getUser !== null"
              :username="userStore.getUser.username"
            />
          </v-btn>
        </v-col>
      </v-row>
    </v-app-bar>
    <v-navigation-drawer v-model="drawer">
      <v-list>
        <v-list-item />
      </v-list>
    </v-navigation-drawer>
    <v-main>
      <v-container fluid>
        <router-view v-slot="{ Component }">
          <v-fade-transition mode="out-in">
            <component :is="Component" />
          </v-fade-transition>
        </router-view>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { useToggle } from "@vueuse/core";
import { onMounted } from "vue";
import {
  VApp,
  VAppBar,
  VAppBarNavIcon,
  VBtn,
  VCol,
  VContainer,
  VFadeTransition,
  VList,
  VListItem,
  VMain,
  VNavigationDrawer,
  VRow,
  VToolbarTitle,
  VTooltip,
} from "vuetify/components";

import { getUser } from "@/api/user-client";
import Ad2ImageAvatar from "@/components/common/Ad2ImageAvatar.vue";
import TheSnackbar from "@/components/TheSnackbar.vue";
import { EXAMPLE_ROUTES_NEWROUTE } from "@/constants";
import { useSnackbarStore } from "@/stores/snackbar";
import { useUserStore } from "@/stores/user";
import User, { UserLocalDevelopment } from "@/types/User";

const snackbarStore = useSnackbarStore();
const userStore = useUserStore();
const [drawer, toggleDrawer] = useToggle();

onMounted(() => {
  loadUser();
});

/**
 * Loads UserInfo from the backend and sets it in the store.
 */
function loadUser(): void {
  getUser()
    .then((user: User) => userStore.setUser(user))
    .catch(() => {
      // No user info received, so fallback
      if (import.meta.env.DEV) {
        userStore.setUser(UserLocalDevelopment());
      } else {
        userStore.setUser(null);
      }
    });
}
</script>

<style>
.main {
  background-color: white;
}
</style>
