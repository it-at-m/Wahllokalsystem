import { describe, expect, it, vi } from "vitest";

import { useCryptoUtils } from "@/composables/crypto/cryptoUtils.ts";

describe("cryptoUtils.ts", () => {
  const { encrypt, decrypt, importKey } = useCryptoUtils();

  describe("importKey", () => {
    it("should_importKey_when_calledWithPassword", async () => {
      const password = "mySecurePassword";

      const mockKey = {} as CryptoKey; // Mock für CryptoKey
      vi.spyOn(crypto.subtle, "importKey").mockResolvedValue(mockKey);
      vi.spyOn(crypto.subtle, "deriveKey").mockResolvedValue(mockKey);

      const key = await importKey(password);
      expect(key).toBe(mockKey);
      expect(crypto.subtle.importKey).toHaveBeenCalledWith(
        "raw",
        new TextEncoder().encode(password),
        "PBKDF2",
        false,
        ["deriveBits", "deriveKey"]
      );
    });
  });

  describe("encrypt", () => {
    it("should_encryptData_when_keyIsValid", async () => {
      const data = "Hello, World!";
      const key = {} as CryptoKey; // Mock für CryptoKey
      const mockEncryptedData = new ArrayBuffer(16);

      vi.spyOn(crypto.subtle, "encrypt").mockResolvedValue(mockEncryptedData);

      const result = await encrypt(data, key);
      expect(result).toBe(mockEncryptedData);
      expect(crypto.subtle.encrypt).toHaveBeenCalledWith(
        { name: "AES-GCM", iv: new Uint8Array(16) },
        key,
        new TextEncoder().encode(data)
      );
    });

    it("should_throwAnError_when_cryptoKeyIsMissing", async () => {
      const data = "Hello, World!";

      await expect(encrypt(data, null)).rejects.toThrow(
        "Verschlüsselung kann ohne CryptKey nicht durchgeführt werden."
      );
    });
  });

  describe("decrypt", () => {
    it("should_decryptData_when_KeyIsValid", async () => {
      const key = {} as CryptoKey;
      const mockEncryptedData = new ArrayBuffer(16);
      const mockDecryptedData = new ArrayBuffer(16);

      const mockDecoder = {
        decode: vi.fn().mockReturnValue("Hello, World!"),
      };
      global.TextDecoder = vi.fn().mockImplementation(() => mockDecoder);

      vi.spyOn(crypto.subtle, "decrypt").mockResolvedValue(mockDecryptedData);

      const result = await decrypt(mockEncryptedData, key);
      expect(result).toBe("Hello, World!");
      expect(crypto.subtle.decrypt).toHaveBeenCalledWith(
        { name: "AES-GCM", iv: new Uint8Array(16) },
        key,
        mockEncryptedData
      );
    });

    it("should_throwAnError_when_cryptoKeyIsMissing", async () => {
      const mockEncryptedData = new ArrayBuffer(16);

      await expect(decrypt(mockEncryptedData, null)).rejects.toThrow(
        "Entschlüsselung kann ohne CryptKey nicht durchgeführt werden."
      );
    });
  });
});
