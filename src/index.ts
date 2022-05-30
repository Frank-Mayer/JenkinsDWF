import { config } from "dotenv";
import moment from "moment";
import { getEndpointFor } from "./getEndpoint.js";
import { onrejected } from "./onrejected.js";
import express from "express";
import compression from "compression";
import { loadServerConfig } from "./config.js";
import { normalize, join, relative } from "path/posix";
import { svg } from "./Image/svg.js";

// import env
config();

const app = express();
app.use(
  compression({
    level: 9,
  })
);

const serverConfig = await loadServerConfig();

if (serverConfig.locale) {
  moment.locale(serverConfig.locale);
}

const bPath = normalize(serverConfig.basepath) || "/";

const cacheControl = (res: express.Response) => {
  res.set("Cache-control", "no-store");
};

for (const project of serverConfig.projects) {
  console.log(`Registering ${project.name} <${project.type}>`);
  app.get(join(bPath, project.id), async (req, res) => {
    try {
      if (serverConfig.debug) {
        const remote =
          req.headers["x-forwarded-for"] || req.socket.remoteAddress;
        const now = new Date().toISOString();

        if (remote) {
          console.debug(`${remote} > ${now} > ${req.url}`);
        } else {
          console.debug(`${now} > ${req.url}`);
        }
      }

      cacheControl(res);

      const project = relative(bPath, req.path);

      const endpoint = await getEndpointFor(project).catch(onrejected);

      const ts = await endpoint
        .getLastFailedTimestamp("RFReborn")
        .catch(onrejected);

      const text = moment.duration(moment.now() - ts).humanize();

      res.status(200);
      res.set("Content-Type", "image/svg+xml");
      res.send(svg(text));
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
