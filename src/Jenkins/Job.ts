import type { Base } from "./Base.js";
import type { Build } from "./Build.js";

export interface Job extends Base {
  actions: Array<Base>;
  description: string;
  displayName: string;
  displayNameOrNull: string | null;
  fullDisplayName: string;
  fullName: string;
  name: string;
  url: string;
  buildable: boolean;
  builds: Array<Build>;
  color: string;
  firstBuild: Build;
  healthReport: [
    {
      description: string;
      iconClassName: string;
      iconUrl: string;
      score: number;
    }
  ];
  inQueue: boolean;
  keepDependencies: boolean;
  lastBuild: Build | null;
  lastCompletedBuild: Build | null;
  lastFailedBuild: Build | null;
  lastStableBuild: Build | null;
  lastSuccessfulBuild: Build | null;
  lastUnstableBuild: Build | null;
  lastUnsuccessfulBuild: Build | null;
  nextBuildNumber: 75;
  property: Array<Base>;
  queueItem: null;
  concurrentBuild: boolean;
  disabled: boolean;
  downstreamProjects: Array<unknown>;
  labelExpression: null;
  scm: Base;
  upstreamProjects: Array<unknown>;
}
