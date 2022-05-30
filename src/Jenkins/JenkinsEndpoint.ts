import { Endpoint } from "../Endpoint.js";
import { btoa } from "../b64.js";
import { default as fetch, Headers } from "node-fetch";
import type { Job } from "./Job.js";
import type { BuildDetail } from "./BuildDetail.js";
import type { RequestInit } from "node-fetch";
import { loadServerConfig } from "../config.js";
import { onrejected } from "../onrejected.js";

export class JenkinsEndpoint implements Endpoint {
  private readonly apiUrlSuffix = "api/json?pretty=false";
  private readonly headers: RequestInit;

  constructor() {
    // Check env
    if (!process.env["JENKINS_TOKEN"]) {
      throw new Error("Envoirement variable JENKINS_TOKEN is not set");
    }
    if (!process.env["JENKINS_USER"]) {
      throw new Error("Envoirement variable JENKINS_USER is not set");
    }

    const headers = new Headers();
    headers.set("Accept", "application/json");
    headers.set(
      "Authorization",
      "Basic " +
        btoa(process.env["JENKINS_USER"] + ":" + process.env["JENKINS_TOKEN"])
    );

    this.headers = {
      headers,
    };
  }

  public async getLastFailedTimestamp(project: string): Promise<number> {
    const conf = (await loadServerConfig().catch(onrejected)).projects.find(
      (x) => x.name === project
    );

    if (!conf) {
      throw new Error(`Project ${project} not found`);
    }

    const jobResp = await fetch(
      `${conf.endpoint}/job/${project}/${this.apiUrlSuffix}` +
        "&tree=lastFailedBuild[timestamp,url]",
      this.headers
    ).catch(onrejected);

    const job = (await jobResp.json().catch(onrejected)) as Job;

    if (job.lastFailedBuild) {
      if (job.lastFailedBuild.timestamp) {
        return job.lastFailedBuild.timestamp;
      } else if (job.lastFailedBuild.url) {
        const buildResp = await fetch(
          job.lastFailedBuild.url + this.apiUrlSuffix,
          this.headers
        ).catch(onrejected);

        const buildDetail = (await buildResp
          .json()
          .catch(onrejected)) as BuildDetail;

        if (buildDetail.timestamp) {
          return buildDetail.timestamp;
        }
      }
    }

    throw new Error("No failed build found");
  }
}
