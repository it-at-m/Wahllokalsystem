export function useCryptoUtils() {
  const algorithm = { name: "AES-GCM", iv: new Uint8Array(16) };

  async function encrypt(data: string | undefined, key: CryptoKey | null) {
    if (!key) {
      throw new Error(
        "Verschlüsselung kann ohne CryptoKey nicht durchgeführt werden."
      );
    }
    return await crypto.subtle.encrypt(
      algorithm,
      key,
      new TextEncoder().encode(data)
    );
  }

  async function decrypt(
    data: ArrayBuffer | string | null,
    key: CryptoKey | null
  ) {
    if (!key) {
      throw new Error(
        "Entschlüsselung kann ohne CryptoKey nicht durchgeführt werden."
      );
    }
    let dataBuffer: ArrayBuffer;
    if (typeof data === "string") {
      dataBuffer = _base64ToArrayBuffer(data);
    } else {
      dataBuffer = data ?? new ArrayBuffer();
    }
    const result = await crypto.subtle.decrypt(algorithm, key, dataBuffer);
    return new TextDecoder("utf-8").decode(result);
  }

  async function importKey(password: string) {
    const keyMaterial = await crypto.subtle.importKey(
      "raw",
      new TextEncoder().encode(password),
      "PBKDF2",
      false,
      ["deriveBits", "deriveKey"]
    );

    return await crypto.subtle.deriveKey(
      {
        name: "PBKDF2",
        salt: new Uint8Array(16),
        iterations: 100000,
        hash: "SHA-256",
      },
      keyMaterial,
      { name: "AES-GCM", length: 256 },
      false,
      ["encrypt", "decrypt"]
    );
  }

  function _base64ToArrayBuffer(base64: string): ArrayBuffer {
    const binaryString = atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes.buffer;
  }

  return {
    encrypt,
    decrypt,
    importKey,
  };
}
