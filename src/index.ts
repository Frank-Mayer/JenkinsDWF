import { config } from "dotenv";
import moment from "moment";
import { getEndpointFor } from "./getEndpoint.js";
import express from "express";
import compression from "compression";
import { loadServerConfig } from "./config.js";
import { normalize, relative, join, extname } from "path/posix";
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

let bPath = normalize(serverConfig.basepath) || "/";
if (!bPath.startsWith("/")) {
  bPath = "/" + bPath;
}
if (!bPath.endsWith("/")) {
  bPath = bPath + "/";
}

const cacheControl = (res: express.Response) => {
  res.set("Cache-control", "no-store");
  return res;
};

app.get(join(bPath, "*"), (req, res) => {
  const path = relative(bPath, req.path);
  console.log(`GET > ${path}`);

  const extension = extname(path);

  let resolved = false;

  for (const ep_ of serverConfig.endpoints) {
    if (resolved) {
      break;
    }

    if (path.startsWith(ep_.path)) {
      resolved = true;
      getEndpointFor(ep_).map(
        (ep) => {
          const filename = relative(ep_.path, path);
          const project = filename.substring(
            0,
            filename.length - extension.length
          );

          ep.getLastFailedTimestamp(project)
            .then((result) => {
              const lastFailed = moment
                .duration(Date.now() - result)
                .humanize();

              switch (extension) {
                case ".svg":
                  cacheControl(res).status(200).send(svg(lastFailed));
                  break;
                default:
                  cacheControl(res).status(404).send("Invalid extension");
              }
            })
            .catch((err) => {
              console.error(err);
              cacheControl(res)
                .status(500)
                .send(typeof err === "string" ? err : JSON.stringify(err));
            });
        },
        () => {
          cacheControl(res).status(404).send(svg("404"));
        }
      );
    }
  }
});

app.listen(serverConfig.port, serverConfig.host, () => {
  console.log(
    `Server listening on http://${serverConfig.host}:${serverConfig.port}${bPath}`
  );
});
