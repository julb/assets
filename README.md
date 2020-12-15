![Build](https://github.com/julb/assets/workflows/Build/badge.svg)

# Julb Spring Boot Assets

## Description

This project is a mono-repo providing Spring Boot assets such as:

- Utility libraries
- Spring Boot starters (Consumer, Gateway, Job, Messaging, OpenTracing)
- Examples (API, Jobs)
- Applications

## How to use

### Generate Eclipse IDE files

```bash
$ ./gradlew eclipse
```

### Build the project

```bash
$ ./gradlew build docs
```

### Generate Docker images using Buildpacks

```bash
$ ./gradlew bootBuildImage
```

## Contributing

This project is totally open source and contributors are welcome.

When you submit a PR, please ensure that the code follows formatting rules provided under [config/eclipse](./config/eclipse) directory.
