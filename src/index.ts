import { config } from "dotenv";
import moment from "moment";
import { getEndpointFor } from "./getEndpoint.js";
import { onrejected } from "./onrejected.js";
import express from "express";
import { loadServerConfig } from "./config.js";
import { normalize, join, relative } from "path/posix";

moment.locale("de");

// import env
config();

const app = express();
const serverConfig = await loadServerConfig();

const bPath = normalize(serverConfig.basepath) || "/";

const cacheControl = (res: express.Response) => {
  res.set("Cache-control", "no-store");
};

for (const project of serverConfig.projects) {
  console.log(`Registering ${project.name} <${project.type}>`);
  app.get(join(bPath, project.id) + ".svg", async (req, res) => {
    try {
      cacheControl(res);

      const project = relative(bPath, req.path);

      const endpoint = await getEndpointFor(project).catch(onrejected);

      const ts = await endpoint
        .getLastFailedTimestamp("RFReborn")
        .catch(onrejected);

      res.send(moment.duration(moment.now() - ts).humanize());
    } catch (err: any) {
      res.status(500);

      if (err.message) {
        res.send(err.message);
      } else if (typeof err === "string") {
        res.send(err);
      } else {
        res.json(err);
      }
    }
  });
}

app.listen(serverConfig.port, serverConfig.host, () => {
  console.log(
    `Server listening on http://${serverConfig.host}:${serverConfig.port}${bPath}`
  );
});
