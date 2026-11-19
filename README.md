# The Developer’s Nightmare: How To Survive Compliance Checklists (and Still Ship Fast)

![dependencies](https://dependency-track.cloud.thomasvitale.dev/api/v1/badge/vulns/project/developers-nightmare/main)
![policies](https://dependency-track.cloud.thomasvitale.dev/api/v1/badge/violations/project/developers-nightmare/main)

You did it! The new feature you’ve been implementing is now ready, and you can’t wait to ship it.

“Not so fast”... Oh no, it’s them: the guardians of compliance! You know what’s about to happen. You’ve been there before.

- Are you using any license that is not approved?
- Is there any CVE reported for the new dependencies you added?
- Can you guarantee the artifact running in production has not been tampered with?

Several checklists, paperwork, and meetings later, you’re finally approved for release. Not fun... Where did the developer joy go?

In this session, Alexandra and Thomas explore how to break the compliance barriers for developers, even in highly regulated industries. The goal is to enhance the developer experience while letting the platform automate and enforce compliance and security checks.

You'll follow the mishaps of a developer and learn how to deal with compliance, using practical solutions based on OSS tools like Renovate, Dependency-Track, Sigstore, Kyverno, and Buildpacks.

## The Demo Application

The application is a live audience poll used during the session. Attendees scan the QR code shown on the results page, pick their biggest compliance pain points, and the results on screen refresh every few seconds.

| Path            | Description                                                |
|-----------------|------------------------------------------------------------|
| `/`             | Survey page where attendees cast their vote.               |
| `/results`      | Live results page, meant to be shown on stage.             |
| `/api/results`  | Vote counts per answer, as JSON.                           |
| `/api/votes`    | `DELETE` to clear all votes (e.g. before a new session).   |
| `/actuator`     | Spring Boot Actuator endpoints (all exposed).              |

## Stack

* Java 25 (GraalVM)
* Spring Boot 4.1 (Web MVC, Data JDBC, Thymeleaf)
* PostgreSQL with Flyway migrations
* [Arconia](https://arconia.io) for OpenTelemetry and Dev Services
* Paketo Buildpacks for building native OCI images

## Development Environment

This project uses [devenv](https://devenv.sh) to manage the development and build environment via [Nix](https://nixos.org). After [installing](https://devenv.sh/getting-started/) devenv, activate the environment:

```shell
devenv shell
```

The environment provides GraalVM, plus all the tools used by the delivery pipeline: [Task](https://taskfile.dev), Cosign, Syft, Grype, Skopeo, slsa-verifier, jq, and HTTPie.

Alternatively, install Java 25 yourself (the `.sdkmanrc` file configures GraalVM CE via [SDKMAN!](https://sdkman.io) with `sdk env`).

Either way, you'll need a container runtime like Podman or Docker.

## Running the Application

```shell
./gradlew bootRun
```

Alternatively, use the [Arconia CLI](https://docs.arconia.io/arconia-cli/latest/index.html):

```shell
arconia dev
```

No manual setup of backing services is needed. [Arconia Dev Services](https://arconia.io/docs/arconia/latest/dev-services/) uses Testcontainers to start them automatically:

* [PostgreSQL](https://arconia.io/docs/arconia/latest/dev-services/postgresql/) database.
* [Grafana LGTM](https://arconia.io/docs/arconia/latest/dev-services/lgtm/) OpenTelemetry-based observability platform.

The application is available at http://localhost:8080 and the live results at http://localhost:8080/results.

To reset the poll:

```shell
http DELETE :8080/api/votes
```

## Observability

The application logs show the URL of the Grafana observability platform:

```logs
...Dev Service 'lgtm' links: Grafana: http://localhost:<port>
```

Logs, metrics, and traces are exported via OTLP using the HTTP/Protobuf format. In Grafana, you can query the telemetry from the "Drilldown" and "Explore" sections.

## Building and Testing

Compile the application and run the tests (they use Testcontainers, so a container runtime must be running):

```shell
task build
```

Run `task` to list all the available tasks, including those used by the pipeline to build, sign, and generate SBOMs for the OCI image.

## Secure Software Supply Chain

Compliance checks are automated in the delivery pipeline rather than handled through checklists.

* **Commit Stage** (on every push and pull request): compiles and tests the application, then generates a CycloneDX SBOM and publishes it to [Dependency-Track](https://dependencytrack.org). On `main`, it builds native OCI images for `amd64` and `arm64` with Buildpacks, publishes a multi-arch image to GitHub Container Registry, signs it with [Cosign](https://github.com/sigstore/cosign) (keyless, via Sigstore), attaches a build provenance attestation, and publishes an image SBOM generated by [Syft](https://github.com/anchore/syft).
* **Dependency-Track Policy Check** (on pull requests): evaluates the SBOM against the license and vulnerability policies defined in Dependency-Track, reports violations in a pull request comment, and fails the check if any exist. Maintainers can comment `/recheck-dt` to run it again, for example after a policy exception has been approved.
* **Acceptance Stage** (after a successful Commit Stage on `main`): runs the acceptance tests and delivers the release candidate to the staging environment via GitOps.
* **Promotion** (manual): promotes the application from one environment to the next.

### Verifying the Image

Verify the image signature with Cosign:

```shell
cosign verify ghcr.io/thomasvitale/developers-nightmare:<version> \
  --certificate-identity-regexp "https://github.com/thomasvitale/developers-nightmare/.*" \
  --certificate-oidc-issuer https://token.actions.githubusercontent.com
```

Verify the build provenance attestation with the GitHub CLI:

```shell
gh attestation verify oci://ghcr.io/thomasvitale/developers-nightmare:<version> --owner thomasvitale
```

## License

This project is licensed under the [Apache License 2.0](LICENSE).
