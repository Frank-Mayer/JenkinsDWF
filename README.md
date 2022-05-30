# days without failure

## config.json

Example

```jsonc
{
  "host": "localhost",
  "port": 12345,
  "basepath": "/dwf",
  "debug": true,
  "locale": "de",
  "projects": [
    {
      "id": "001",
      "name": "myproject",
      "endpoint": "https://www.yoursite.de/jenkins",
      "type": "jenkins"
    }
  ]
}
```

This config makes the project `myproject` available at `http://localhost:12345/dwf/001`

- **host**: Hostname of the server (`string`).
- **port**: Port of the server (`number`).
- basepath: Add a basepath to the listener. Default is `/` (`string`).
- debug: Enable debug console log (`boolean`). Default is `false`.
- locale: Localization of the output (`string`). Default is `es-US`.
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
