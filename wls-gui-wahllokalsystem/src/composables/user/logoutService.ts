export function useLogoutService() {
  async function logout() {
    try {
      const request = new Request(
        "https://kubernetes.docker.internal:8100/logout",
        {
          method: "GET",
          credentials: "include",
        }
      );
      await fetch(request).catch((reason) => console.log(reason));

      await fetch("logout", getPOSTConfig(undefined));

      console.log("logout successful");
    } catch (error) {
      console.log("get logout", error);
    }
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

  return {
    logout,
  };
}
