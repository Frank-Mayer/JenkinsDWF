/**
 * Log error message and exit process
 */
export const onrejected = (err: any) => {
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

  process.exit(1);
};
