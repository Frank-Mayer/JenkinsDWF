/**
 * Plain string to base64 encoded string
 */
export const btoa = (str: string): string =>
  Buffer.from(str).toString("base64");

/**
 * Base64 encoded string to plain string
 */
export const atob = (str: string): string =>
  Buffer.from(str, "base64").toString();
