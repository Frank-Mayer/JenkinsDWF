import { readFile } from "fs";
import { Validator } from "jsonschema";
import type { Schema } from "jsonschema";

const validator = new Validator();

const configSchema: Schema = {
  id: "/config",
  type: "object",
  properties: {
    host: { type: "string" },
    port: { type: "number" },
    basepath: { type: "string" },
    projects: {
      id: "/config/projects",
      type: "array",
      items: {
        id: "/config/projects/item",
        properties: {
          name: { type: "string" },
          endpoint: { type: "string" },
          type: { type: "string", enum: ["jenkins", "github"] },
        },
      },
      required: ["endpoint", "type"],
    },
  },
  required: ["host", "port", "projects"],
};

export type projectConfig = {
  name: string;
  endpoint: string;
  type: "jenkins" | "github";
};

export type config = {
  host: string;
  port: number;
  basepath: string;
  projects: Array<projectConfig>;
};

let cachedConfig: config | null = null;

export const loadServerConfig = () =>
  cachedConfig
    ? Promise.resolve(cachedConfig)
    : new Promise<config>((resolve, reject) => {
        readFile("./config.json", "utf8", (err, data) => {
          if (err) {
            return reject(err);
          } else {
            const confObj = JSON.parse(data);
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

              reject(validationResult);
            }
          }
        });
      });
