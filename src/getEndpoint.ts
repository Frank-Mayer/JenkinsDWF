import { loadServerConfig } from "./config.js";
import { JenkinsEndpoint } from "./Jenkins/JenkinsEndpoint.js";
import type { Endpoint } from "./Endpoint.js";
import type { projectConfig } from "./config.js";
import { onrejected } from "./onrejected.js";

const endpointCache = new Map<projectConfig["type"], Endpoint>();

export const getEndpointFor = async (project: string): Promise<Endpoint> => {
  const conf = await loadServerConfig().catch(onrejected);
  const projectConf = conf.projects.find((x) => x.id === project);

  if (!projectConf) {
    throw new Error(`Project ${project} not found`);
  }

  if (endpointCache.has(projectConf.type)) {
    return endpointCache.get(projectConf.type)!;
  }

  switch (projectConf.type) {
    case "jenkins":
      const ep = new JenkinsEndpoint();
      endpointCache.set(projectConf.type, ep);
      return ep;

    default:
      throw new Error(
        `No valid endpoint for project type ${projectConf.type} found`
      );
  }
};
