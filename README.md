# Java-Code-Identifier-Extractor
A program using the javaparser library to extract code identifiers from Java source code for further analysis. This tool was developed for the research project "Using Identifiers to Measure Cohesion" at the University of Auckland.

## Setup
This project is built using Gradle 5.4.1 and developed using Java 8. All dependencies are defined in build.gradle.

## Building
To build an executable (fat)jar, run ```gradle buildFat```. This will also run any tests that have been written (none so far), and put the built jar in `./build/libs/IdentifierIndexer-all.jar`

## Running
To run the fatjar, run ```java -jar <fatjar_name>.jar <options>```. Full CLI instructions are as follows:
```
JavaIdentifierExtractor [-hr] [-f=<formatterType>] [-v=<verbosity>] ROOT-PATHS...

Looks at Java source code and extracts code identifiers

      ROOT-PATHS...   1 or more files/directories for inspection
  -f, --format=<formatterType>
                      Format from: TABULATED, JSON
                      (default: JSON)
  -h, --help          Displays a help message
  -r, --recursive     Recursively inspect directories
  -v, --verbosity=<verbosity>
                      Amount of identifiers to extract, from: DECLARATIONS,
                        USAGES
                      (default: USAGES)
```
All output is printed to stdout. This can be piped to another program or to a file accordingly.
### Options
#### Format
Tabulated: Prints the identifiers to stdout in a simple tabulated form. Easy to read for humans, but not a standardized format. Should only be used when interested in visually inspecting extracted identifiers' structure. <br>
JSON: Prints a standardized JSON format to stdout. Intended for interoping with other programs.
#### Verbosity
Declarations: Only extracts identifiers that has been explicitly declared by the programmers of the system (i.e. excludes any identifiers coming from libraries; standard or external)<br>
Usages: Extracts any identifier that is used programmatically (excludes annotations and comments).
## Additional Information
javaparser API documentation can be found here:
https://www.javadoc.io/doc/com.github.javaparser/javaparser-core/3.14.3