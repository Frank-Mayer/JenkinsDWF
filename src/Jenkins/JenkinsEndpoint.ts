import { btoa } from "../b64.js";
import { default as fetch, Headers } from "node-fetch";
import type { Endpoint } from "../Endpoint.js";
import type { Job } from "./Job.js";
import type { BuildDetail } from "./BuildDetail.js";
import type { RequestInit } from "node-fetch";
import type { endpointConfig } from "../config.js";

export class JenkinsEndpoint implements Endpoint {
  private readonly apiUrlSuffix = "api/json?pretty=false";
  private readonly headers: RequestInit;
  private readonly conf: endpointConfig;

  constructor(config: endpointConfig) {
    // Check env
    if (!process.env["JENKINS_TOKEN"]) {
      throw new Error("Envoirement variable JENKINS_TOKEN is not set");
    }
    if (!process.env["JENKINS_USER"]) {
      throw new Error("Envoirement variable JENKINS_USER is not set");
    }

    this.conf = config;

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

  public getLastFailedTimestamp(project: string) {
    return new Promise<number>((res, rej) => {
      fetch(
        `${this.conf.url}/job/${project}/${this.apiUrlSuffix}` +
          "&tree=lastFailedBuild[timestamp,url]",
        this.headers
      )
        .then((jobResp) => {
          (jobResp.json() as Promise<Job>)
            .then((job) => {
              if (job.lastFailedBuild) {
                if (job.lastFailedBuild.timestamp) {
                  res(job.lastFailedBuild.timestamp);
                } else if (job.lastFailedBuild.url) {
                  fetch(
                    job.lastFailedBuild.url + this.apiUrlSuffix,
                    this.headers
                  )
                    .then((buildResp) => {
                      (buildResp.json() as Promise<BuildDetail>)
                        .then((buildDetail) => {
                          if (buildDetail.timestamp) {
                            res(buildDetail.timestamp);
                          } else {
                            rej("No timestamp found");
                          }
                        })
                        .catch(rej);
                    })
                    .catch(rej);
                } else {
                  rej("No timestamp found");
                }
              } else {
                rej("No lastFailedBuild found");
              }
            })
            .catch(rej);
        })
        .catch(rej);
    });
  }
}
