import { config } from "dotenv";
import moment from "moment";
import { getEndpointFor } from "./getEndpoint.js";
import { onrejected } from "./onrejected.js";

moment.locale("de");

// import env
config();

(async () => {
  const endpoint = await getEndpointFor("RFReborn").catch(onrejected);
  const ts = await endpoint
    .getLastFailedTimestamp("RFReborn")
    .catch(onrejected);
  console.debug("ts", ts);
  console.log(moment.duration(moment.now() - ts).humanize());
})();
