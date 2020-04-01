[![Build Status](https://travis-ci.com/julb/assets.svg?token=NrPG3HkqVTG4JHmZ7BhS&branch=develop)](https://travis-ci.com/julb/assets)

# assets

## Description

This project is a mono-repo for all assets such as:
* Libraries
* Spring Boot starters
* Examples
* Applications

## How to use

### Build the project

```bash
$ ./gradlew build
```

### Generate Docker images for Java applications
```bash
$ ./gradlew jibDockerBuild \
    -Djib.from.image=adoptopenjdk:13-openj9 \
    -Djib.container.creationTime=USE_CURRENT_TIMESTAMP \
    -Djib.to.tags=latest

$ docker run -ti --rm -p 9090:9090 -e "JAVA_TOOL_OPTIONS=-Dserver.port=9090" <api-name>[:<api-version>]
```

