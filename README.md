# PokeApiTest
Testing around Pokemon public API info

## Development Rules

- **Dependency Management:** Never attempt to downgrade any version of the project's dependencies or libraries. Always seek the most recent version compatible with the current environment.

- **Post-Change Verification Workflow:** After making any modification to the code or configuration, the following steps must be followed:
    1. **Gradle Sync:** Run synchronization if the change requires it (modifications in `build.gradle.kts`, `libs.versions.toml`, etc.).
    2. **Build:** Perform a full project build to ensure there are no errors.
    3. **Unit Tests:** Run the full Unit Test suite (whenever tests are present in the project).
