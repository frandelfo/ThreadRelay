# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**ThreadRelay** — a Java application for a TEPSIT course (Class 4) at Franchetti Salviani school. The project is in early development; only the NetBeans skeleton exists so far.

## Build and Run

This is an Apache Ant / NetBeans J2SE project targeting **Java 24**.

```bash
ant build        # Compile sources to build/
ant run          # Run the application
ant jar          # Package to dist/ThreadRelay.jar
ant clean        # Remove build artifacts
ant javadoc      # Generate Javadoc
```

Main class: `threadrelay.ThreadRelay`
JAR output: `dist/ThreadRelay.jar`

## Architecture

- `src/threadrelay/ThreadRelay.java` — sole source file; application entry point
- `nbproject/project.properties` — compiler settings (source/target Java 24, no external dependencies)
- `build.xml` — thin Ant wrapper that imports `nbproject/build-impl.xml`

No external libraries or modules are configured. All new classes should go under the `threadrelay` package in `src/`.
