# The Developer’s Nightmare: How To Survive Compliance Checklists (and Still Ship Fast)

You did it! The new feature you’ve been implementing is now ready and you can’t wait to ship it.

“Not so fast”. Oh no, it’s them: the guardians of compliance! You know what’s about to happen. You’ve been there before.

- Are you using any license that is not approved?
- Is there any CVE reported for the new dependencies you added?
- Can you guarantee the artifact running in production has not been tampered with?

Several checklists, paperwork, and meetings later, you’re finally approved for release. Not fun. Where did the developer joy go?

In this session, Alexandra and Thomas explore how to break the compliance barriers for developers, even in highly-regulated industries. The goal is to enhance the developer experience while letting the platform automate and enforce compliance and security checks.

You'll follow the mishaps of a developer and learn how to deal with compliance, using practical solutions based on OSS tools like Backstage, Dependency-Track, Sigstore and Buildpacks.

## Stack

* Java 25 (with GraalVM)
* Spring Boot 4.0
* Vaadin
* Arconia

## Development environment

This project uses [Flox](https://flox.dev/) to manage the development and build environment via [Nix](https://nixos.org). After [installing](https://flox.dev/docs/install-flox/install/) the Flox CLI (open-source), activate the environment:

```shell
flox activate
```

Alternatively, ensure you have Java 25 installed.

Either way, you'll need a container runtime like Podman or Docker installed on your machine.

## Running the application

Run the application.

```shell
./gradlew bootRun
```

Alternatively, you can use the [Arconia CLI](https://docs.arconia.io/arconia-cli/latest/index.html):

```shell
arconia dev
```

Under the hood, the Arconia framework will automatically spin up the needed backing services using [Arconia Dev Services](https://arconia.io/docs/arconia/latest/dev-services/) and Testcontainers:

* [Grafana LGTM](https://arconia.io/docs/arconia/latest/dev-services/lgtm/) OpenTelemetry-based observability platform
* [PostgreSQL](https://arconia.io/docs/arconia/latest/dev-services/postgresql/) database.

The application will be accessible at http://localhost:8080.

## Accessing Grafana

The application logs will show you the URL where you can access the Grafana observability platform.

```logs
...o.t.grafana.LgtmStackContainer: Access to the Grafana dashboard: http://localhost:<port>
```

By default, logs, metrics, and traces are exported via OTLP using the HTTP/Protobuf format.

In Grafana, you can query the telemetry from the "Drilldown" and "Explore" sections.
