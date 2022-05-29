import type { Base } from "./Base.js";

export interface BuildDetail extends Base {
  actions: [
    {
      _class: "hudson.model.CauseAction";
      causes: [
        {
          _class: "hudson.model.Cause$UserIdCause";
          shortDescription: "Started by user mztikk";
          userId: "mztikk";
          userName: "mztikk";
        }
      ];
    },
    {
      _class: "hudson.plugins.git.util.BuildData";
      buildsByBranchName: {
        "refs/remotes/origin/master": {
          _class: "hudson.plugins.git.util.Build";
          buildNumber: 67;
          buildResult: null;
          marked: {
            SHA1: "c0e8745daa3386d273f883a860fb537df3351d70";
            branch: [
              {
                SHA1: "c0e8745daa3386d273f883a860fb537df3351d70";
                name: "refs/remotes/origin/master";
              }
            ];
          };
          revision: {
            SHA1: "c0e8745daa3386d273f883a860fb537df3351d70";
            branch: [
              {
                SHA1: "c0e8745daa3386d273f883a860fb537df3351d70";
                name: "refs/remotes/origin/master";
              }
            ];
          };
        };
      };
      lastBuiltRevision: {
        SHA1: "c0e8745daa3386d273f883a860fb537df3351d70";
        branch: [
          {
            SHA1: "c0e8745daa3386d273f883a860fb537df3351d70";
            name: "refs/remotes/origin/master";
          }
        ];
      };
      remoteUrls: ["https://github.com/mztikk/RFReborn"];
      scmName: "";
    },
    {},
    {},
    {},
    {
      _class: "org.jenkinsci.plugins.displayurlapi.actions.RunDisplayAction";
    }
  ];
  artifacts: [];
  building: false;
  description: null;
  displayName: "#67";
  duration: 5487;
  estimatedDuration: 11198;
  executor: null;
  fullDisplayName: "RFReborn #67";
  id: "67";
  keepLog: false;
  number: 67;
  queueId: 2494;
  result: string;
  timestamp: number;
  url: "https://www.mztikk.de/jenkins/job/RFReborn/67/";
  builtOn: "build";
  changeSet: {
    _class: "hudson.plugins.git.GitChangeSetList";
    items: [];
    kind: "git";
  };
  culprits: [
    {
      absoluteUrl: "https://www.mztikk.de/jenkins/user/noreply";
      fullName: "noreply";
    },
    {
      absoluteUrl: "https://www.mztikk.de/jenkins/user/mztikk";
      fullName: "mztikk";
    }
  ];
}
