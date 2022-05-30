# days without failure

## config.json

Example

```jsonc
{
  "host": "localhost",
  "port": 12345,
  "basepath": "/test",
  "debug": true,
  "projects": [
    {
      "id": "@mztikk/RFReborn",
      "name": "RFReborn",
      "endpoint": "https://www.mztikk.de/jenkins",
      "type": "jenkins"
    }
  ]
}
```

- **host**: Hostname of the server (`string`).
- **port**: Port of the server (`number`).
- basepath: Add a basepath to the listener. Default is `/` (`string`).
- debug: Enable debug console log (`boolean`). Default is `false`.
- **projects**: `Array` of projects.
  - id: Where this project should be avaliable at (`string`). Default is `name` of the same project.
  - **name**: Name to be used when fetching data from the api (`string`).
  - **endpoint**: URL where the API-Server is located (`string`).
  - **type**: What type of build server is at this endpoint (currently only `jenkins`).

Options with a default value can be removed.

## Environment Variables

### Jenkins

- `JENKINS_TOKEN`: Key to access the Jenkins-API
- `JENKINS_USER`: User for that Token

You can use a `.env` File or normal environment variables.
