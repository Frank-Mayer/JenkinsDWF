# days without failure

## config.json

Example

```json
{
  "host": "localhost",
  "port": 12345,
  "basepath": "/test",
  "debug": false,
  "locale": "de",
  "endpoints": [
    {
      "path": "j",
      "url": "https://www.myserver.de/jenkins",
      "type": "jenkins"
    }
  ]
}
```

This config makes any project of the server `https://www.myserver.de/jenkins` available at `http://localhost:12345/test/j/`. If your project is called `foobar`, you can access it at `http://localhost:12345/test/j/foobar.svg`.

- **`host`**: Hostname of the server (`string`).
- **`port`**: Port of the server (`number`).
- _`basepath`_: Add a base path to the listener. Default is `/` (`string`).
- _`debug`_: Enable debug console log (`boolean`). Default is `false`.
- _`locale`_: Localization of the output (`string`). Default is `es-US`.
- **`endpoints`**: `Array` of endpoints.
  - **`path`**: Where this project should be available at (`string`).
  - **`url`**: Where the build server is available (`string`).
  - **`type`**: What type of build server is at this endpoint (currently only `jenkins`).

Options with a default value can be removed.

## Environment Variables

### Jenkins

- **`JENKINS_TOKEN`**: Key to access the Jenkins-API
- **`JENKINS_USER`**: User for that Token

You can use a `.env` File.
