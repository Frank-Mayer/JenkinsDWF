import { readFile, existsSync } from "fs";
import { Validator } from "jsonschema";
import type { Schema } from "jsonschema";

if (!existsSync("./config.json")) {
  console.error("config.json not found");
  process.exit(1);
}

const validator = new Validator();

const configSchema: Schema = {
  id: "/config",
  type: "object",
  properties: {
    host: { type: "string" },
    port: { type: "number" },
    basepath: { type: "string" },
    debug: { type: "boolean" },
    locale: { type: "string" },
    endpoints: {
      id: "/config/endpoints",
      type: "array",
      items: {
        id: "/config/endpoints/item",
        properties: {
          path: { type: "string" },
          url: { type: "string" },
          type: { type: "string", enum: ["jenkins", "github"] },
        },
        required: ["path", "url", "type"],
      },
      required: ["endpoint", "type"],
    },
  },
  required: ["host", "port", "endpoints"],
};

export type endpointConfig = {
  path: string;
  url: string;
  type: "jenkins" | "github";
};

export type config = {
  host: string;
  port: number;
  basepath: string;
  debug: boolean;
  locale?: string;
  endpoints: Array<endpointConfig>;
};

let cachedConfig: config | null = null;

export const loadServerConfig = () =>
  cachedConfig
    ? Promise.resolve(cachedConfig)
    : new Promise<config>((resolve) => {
        readFile("./config.json", "utf8", (err, data) => {
          if (err) {
            console.error(err.name, err.message);
            process.exit(1);
          } else {
            const confObj: config = JSON.parse(data);
            const validationResult = validator.validate(confObj, configSchema, {
              nestedErrors: true,
              allowUnknownAttributes: false,
              throwError: false,
            });

            if (validationResult.valid) {
              cachedConfig = confObj;
              resolve(confObj);
            } else {
              for (const err of validationResult.errors) {
                console.error(
                  ["Config error:", err.path.join("."), err.message].join(" ")
                );
              }

              process.exit(1);
            }
          }
        });
      });
