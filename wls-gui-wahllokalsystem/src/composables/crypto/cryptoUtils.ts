export function useCryptoUtils() {

  /*async function encrypt(data: string | undefined, pin: string) {
    console.debug("Encrypt with pin: ", pin);
    return await crypto.subtle.encrypt(
      algorithm,
      key,
      new TextEncoder().encode(data)
    );
  }

  async function decrypt(data: ArrayBuffer, pin: string) {
    console.debug("Decrypt with pin: ", pin);
    console.debug("Key:", key);
    console.debug("algorithm:", algorithm);
    console.debug("data:", data);
    return await crypto.subtle.decrypt(
      algorithm,
      key,
      data
    );
  }*/

  async function importKey(password: string) {
    const keyMaterial = await crypto.subtle.importKey(
      "raw",
      new TextEncoder().encode(password),
      "PBKDF2",
      false,
      ["deriveBits", "deriveKey"]
    );

    const salt = crypto.getRandomValues(new Uint8Array(16));
    return await crypto.subtle.deriveKey(
      {
        name: "PBKDF2",
        salt: salt,
        iterations: 100000,
        hash: "SHA-256",
      },
      keyMaterial,
      { name: "AES-GCM", length: 256 },
      false,
      ["encrypt", "decrypt"]
    );
  }

  return {
    /*encrypt,
    decrypt,*/
    importKey,
  };
}