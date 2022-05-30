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
    projects: {
      id: "/config/projects",
      type: "array",
      items: {
        id: "/config/projects/item",
        properties: {
          name: { type: "string" },
          id: { type: "string" },
          endpoint: { type: "string" },
          type: { type: "string", enum: ["jenkins", "github"] },
        },
        required: ["name", "endpoint", "type"],
      },
      required: ["endpoint", "type"],
    },
  },
  required: ["host", "port", "projects"],
};

export type projectConfig = {
  id: string;
  name: string;
  endpoint: string;
  type: "jenkins" | "github";
};

export type config = {
  host: string;
  port: number;
  basepath: string;
  debug: boolean;
  locale?: string;
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
            const confObj: config = JSON.parse(data);
            const validationResult = validator.validate(confObj, configSchema, {
              nestedErrors: true,
              allowUnknownAttributes: false,
              throwError: false,
            });

            if (validationResult.valid) {
              for (const proj of confObj.projects) {
                if (!proj.id) {
                  proj.id = proj.name;
                }
              }

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
