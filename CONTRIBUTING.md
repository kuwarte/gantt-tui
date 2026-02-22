# Contributing to GanttTUI

Thank you for considering contributing to GanttTUI!

---

## Where to Start

Areas we are especially interested in:

- Java version compatibility (currently compiled for Java 25; targeting broader version support)
- Cross-platform support (Linux, macOS - and eventually Windows)

- Theming and color support
- Exporting tasks to other formats (PDF, CSV)
- Bug fixes and performance improvements

---

## Development Setup

### Prerequisites

- Java 25+
- Maven 3.6+

### Running Locally

```bash
git clone https://github.com/kuwarte/gantt-tui.git
cd gantt-tui
mvn clean package
java -jar target/GanttTUI-1.0-SNAPSHOT.jar
```

---

## Submitting Changes

1. **Fork** the repository on GitHub
2. **Clone** your fork locally
3. **Create a branch** for your feature or fix

```bash
git checkout -b feature/your-feature-name
```

4. **Commit** your changes with a clear message
5. **Push** to your fork

```bash
git push origin feature/your-feature-name
```

6. **Open a Pull Request** on `kuwarte/gantt-tui` describing what you changed and why

---

## Coding Guidelines

- **Keep it simple** — this is a CLI tool, avoid large third-party dependencies to keep the JAR small
- **Unicode carefully** — aim for broad terminal compatibility; avoid hardcoding block characters unless configurable
- **Clear commits** — one change per commit, descriptive messages

---

## Reporting Bugs

Open an [Issue](https://github.com/kuwarte/gantt-tui/issues) and include:

- Your OS and terminal emulator
- Java version (`java -version`)
- Steps to reproduce
- Error message or stack trace if applicable

---

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
