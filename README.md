# CYSEC Coach Standard Language Library

This repository contains a standard language to express CySeC coach logic. It is an attempt to allow
simplified access to coding hooks and functionalities without the need to insert custom code into a
coach an rather have a standard subset of possibilites within a coach.

## Requirements

This project depends on the [CYSEC Platform Project](https://github.com/cysec-platform/cysec-platform).
In order to correctly build and run the coach-standard-langauge the cysec-platform-bridge projects needs to be installed
in your local maven cache.

You can accomplish this by cloning the CYSEC Platform and running `maven install` in the cysec-platform directory.

## Code formatting

This project uses the Spotless code formatter with the palantir-java-format backend to ensure a clean and
uniform code style throughout the project.
Please make sure to format your code using the configured formatter setup.

There are multiple ways to run the formatter. You can run it on the command line through maven:

```sh
mvn spotless:check
```

This just checks if there are formatting errors.
To actually fix them, you can run:

```sh
mvn spotless:apply
```

It's very convenient to have the formatter integrated with your IDE.
This is how to setup the formatter in IntelliJ to automatically format when you save:

- Open Settings -> Plugins -> Marketplace -> Search for "palantir-java-format" -> Install
- You might have to restart your IDE after this step
- Open Settings -> Tools -> Actions on Save -> Check "Reformat Code"
- Optionally, you can select the file types to automatically format by clicking on "All File Types".
  This is useful because only Java files will be formatted by the "palantir-java-format" formatter.
  All other files will be formatted using the default IntelliJ formatter which might not be what you want.


## License

This project is licensed under the Apache 2.0 license, see [LICENSE](LICENSE).