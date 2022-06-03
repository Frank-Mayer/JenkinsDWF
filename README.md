# days-without-failure

Days without failure

## config.yaml

```yaml
address: localhost
port: 12345
basepath: /
debug: false
timezone: Europe/Berlin
endpoints:
  - path: jenkins
    url: https://www.myserver.de/jenkins
    type: JENKINS
    timezone: Europe/Berlin
```

## .env

```bash
JENKINS_USER=<Jenkins Nutzername>
JENKINS_TOKEN=<Jenkins api key>
```
