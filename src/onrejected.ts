import { loadServerConfig } from "./config.js";

/**
 * Log error message and exit process
 */
export const onrejected = async (err: any) => {
  const config = await loadServerConfig();

  if (config.debug) {
    const errorObj = err instanceof Error ? err : undefined;

    if (errorObj) {
      if (errorObj.name) {
        console.error(errorObj.name);
      }

      console.error(errorObj.message);

      if (errorObj.cause) {
        console.error("cause:", errorObj.cause);
      }
    } else {
      console.error(err);
    }
  }

  throw new Error(err);
};
