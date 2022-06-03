# days-without-failure

Days without failure

[![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/)
[![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)

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

Jenkins project `foo` would be available at `http://localhost:12345/jenkins/foo`

```markdown
![jenkins/foo](http://localhost:12345/jenkins/foo.svg)
```

## .env

```bash
JENKINS_USER=<Jenkins Nutzername>
JENKINS_TOKEN=<Jenkins api key>
```
