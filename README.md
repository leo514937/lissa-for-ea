# LiSSA: A Framework for Generic Traceability Link Recovery

<img src=".github/images/approach.svg" alt="Approach Overview" style="width: 100%;" /><br/>

Welcome to the LiSSA project!
This framework leverages Large Language Models (LLMs) enhanced through Retrieval-Augmented Generation (RAG) to establish traceability links across various software artifacts.

> [!TIP]
> If you have any questions, don't hesitate to [contact us](https://ardoco.de).

## Overview

In software development and maintenance, numerous artifacts such as requirements, code, and architecture documentation are produced.
Understanding the relationships between these artifacts is crucial for tasks like impact analysis, consistency checking, and maintenance.
LiSSA aims to provide a generic solution for Traceability Link Recovery (TLR) by utilizing LLMs in combination with RAG techniques.

The concept and evaluation of LiSSA are detailed in our paper:

> Fuchß, D., Hey, T., Keim, J., Liu, H., Ewald, N., Thirolf, T., & Koziolek, A. (2025). LiSSA: Toward Generic Traceability Link Recovery through Retrieval-Augmented Generation. In Proceedings of the IEEE/ACM 47th International Conference on Software Engineering, Ottawa, Canada.

You can access the paper [here](https://ardoco.de/c/icse25).

## Features

- **Generic Applicability**: LiSSA is designed to recover traceability links across various types of software artifacts, including:
  - [Requirements to code](https://ardoco.de/c/icse25)
  - [Documentation to code](https://ardoco.de/c/icse25)
  - [Architecture documentation to architecture models](https://ardoco.de/c/icse25)
  - [Requirements to requirements](https://ardoco.de/c/refsq25)
- **Advanced Retrieval Architectures**: Supports both standard IR-based召回 and modern **SLM-based Candidate Filtering (Path B)** using a Cartesian product approach for higher recall.
- **Retrieval-Augmented Generation**: By combining LLMs with RAG, LiSSA enhances the accuracy and relevance of the recovered traceability links.

## Documentation

- [Architecture](docs/architecture.md): Detailed information about the project's architecture and components
- [Configuration](docs/configuration.md): Guide for configuring LiSSA
- [CLI Usage](docs/cli.md): Information about using the command line interface
- [Caching](docs/caching.md): Information about the caching system and Redis setup
- [Development](docs/development.md): Development setup and contribution guidelines
- [Prompt Optimization](docs/prompt-optimization.md): Guide for optimizing LLM prompts

## Getting Started

To get started with LiSSA, follow these steps:

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/leo514937/lissa-for-ea.git
   cd lissa
   ```
2. **Install Dependencies**:
   Ensure you have Java JDK 21 or later installed. Then, build the project using Maven:

   ```bash
   mvn clean package
   ```
3. **Run LiSSA**:
   Execute the main application:

   ```bash
   java -jar target/lissa-0.2.0-SNAPSHOT-jar-with-dependencies.jar eval -c config.json
   ```

### Performance & Comparison Tools

LiSSA includes a specialized script for comparing two different configurations (e.g., Before/After an optimization or comparing IR vs. SLM paths):

```powershell
.\tools\scripts\pipelines\compare_before_after.ps1 -BeforeConfig path/to/before.json -AfterConfig path/to/after.json
```

## Project Structure

The project has been organized to keep utility scripts and datasets in a unified location:

- **`src/`**: Core Java implementation.
- **`tools/`**:
  - `datasets/`: Pre-configured datasets (WARC, Dronology, etc.).
  - `scripts/`: Python and PowerShell utilities for pipeline management, configuration generation, and API health checks.
  - `test-configs/`: Ready-to-use configurations for internal verification and benchmarking.
- **`example-configs/`**: Reference configuration files for different scenarios.

## Contributing

We welcome contributions from the community! If you're interested in contributing to LiSSA, please read our [Code of Conduct](CODE_OF_CONDUCT.md) and [Development Guide](docs/development.md) to get started.

## License

This project is licensed under the MIT License. See the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgments

LiSSA is developed by researchers from the Modelling for Continuous Software Engineering (MCSE) group of KASTEL - Institute of Information Security and Dependability at the Karlsruhe Institute of Technology (KIT).

For more information about the project and related research, visit our [website](https://ardoco.de/).

---

*Note: This README provides a brief overview of the LiSSA project. For comprehensive details, please refer to our [documentation](docs/).*
