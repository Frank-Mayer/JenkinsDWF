import { Job } from "./Jenkins/Job.js";
import { BuildDetail } from "./Jenkins/BuildDetail.js";
import { config } from "dotenv";
import { default as fetch, Headers } from "node-fetch";
import { btoa } from "./b64.js";
import moment from "moment";

moment.locale("de");

const apiUrlSuffix = "api/json?pretty=false";

// import env
config();

// Check env
if (!process.env["JENKINS_TOKEN"]) {
  throw new Error("Envoirement variable JENKINS_TOKEN is not set");
}
if (!process.env["JENKINS_USER"]) {
  throw new Error("Envoirement variable JENKINS_USER is not set");
}

let headers = new Headers();

//headers.append('Content-Type', 'text/json');
headers.set("Accept", "application/json");
headers.set(
  "Authorization",
  "Basic " +
    btoa(process.env["JENKINS_USER"] + ":" + process.env["JENKINS_TOKEN"])
);

(async () => {
  const jobResp = await fetch(
    "https://www.mztikk.de/jenkins/job/RFReborn/" + apiUrlSuffix,
    {
      headers,
    }
  );

  // const respText: any = await resp.text();
  // writeFileSync("src/index.json", respText);
  const job = (await jobResp.json()) as Job;

  let dwf = "N/A";

  if (job.lastFailedBuild) {
    const buildResp = await fetch(job.lastFailedBuild.url + apiUrlSuffix, {
      headers,
    });

    const buildDetail = (await buildResp.json()) as BuildDetail;
    dwf = moment.duration(moment.now() - buildDetail.timestamp).humanize();
  }

  console.log("Letzer Build-Fehler:", dwf);
})();
