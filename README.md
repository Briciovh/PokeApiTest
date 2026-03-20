# PokeApiTest
Testing around Pokemon public API info

## Development Rules | Reglas de Desarrollo

- **Dependency Management | Gestión de Dependencias:** Never attempt to downgrade any version of the project's dependencies or libraries. Always seek the most recent version compatible with the current environment. / Nunca se debe intentar hacer downgrade de ninguna versión de las dependencias o librerías del proyecto. Siempre se debe buscar la versión más reciente que sea compatible con el entorno actual.

- **Architecture | Arquitectura:** Android applications must follow MVVM, Clean Architecture, and use the Repository pattern for data retrieval. / Las aplicaciones Android deben tener una arquitectura MVVM, Clean Architecture y usar el patrón Repository para la obtención de datos.

- **Post-Change Verification Workflow | Flujo de Verificación Post-Cambio:** After making any modification to the code or configuration, the following steps must be followed: / Tras realizar cualquier modificación en el código o configuración, se deben seguir estos pasos:
    1. **Gradle Sync:** Run synchronization if the change requires it (modifications in `build.gradle.kts`, `libs.versions.toml`, etc.). / Ejecutar sincronización si el cambio lo requiere (modificaciones en `build.gradle.kts`, `libs.versions.toml`, etc.).
    2. **Build:** Perform a full project build to ensure there are no errors. / Realizar una compilación completa del proyecto para asegurar que no hay errores.
    3. **Unit Tests:** Run the full Unit Test suite (whenever tests are present in the project). / Ejecutar la suite de Unit Tests completa (siempre que haya tests presentes en el proyecto).
