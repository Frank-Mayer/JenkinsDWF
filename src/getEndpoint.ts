import { JenkinsEndpoint } from "./Jenkins/JenkinsEndpoint.js";
import type { Endpoint } from "./Endpoint.js";
import type { endpointConfig } from "./config.js";
import { btoa } from "./b64.js";
import { Option } from "./Option.js";

const endpointCache = new Map<string, Endpoint>();

export const getEndpointFor = (endpoint: endpointConfig): Option<Endpoint> => {
  const key = btoa(JSON.stringify(endpoint));

  if (endpointCache.has(key)) {
    return Option.some(endpointCache.get(key)!);
  } else {
    switch (endpoint.type) {
      case "jenkins":
        const jenkinsEndpoint = new JenkinsEndpoint(endpoint);
        endpointCache.set(key, jenkinsEndpoint);
        return Option.some(jenkinsEndpoint);
    }

    console.error(`Unknown endpoint <${endpoint.type}>`);
    return Option.none();
  }
};
