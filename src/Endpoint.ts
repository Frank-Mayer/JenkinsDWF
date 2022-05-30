export interface Endpoint {
  getLastFailedTimestamp(project: string): Promise<number>;
}
