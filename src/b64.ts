/**
 * Plain string to base64 encoded string
 */
export const btoa = (str: string): string =>
  Buffer.from(str).toString("base64");
