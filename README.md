# Task scheduler
This program creates a valid schedule that puts tasks into processors.
Both the input and output dot files contain a task graph with nodes representing tasks and edges representing the communication costs between tasks.
The output will additionally contain the processor number that the node/task should run in.

## How to access file
Approach 1. Download the `scheduler.jar` file from the Milestone 1, `v1.0` release.

Approach 2. Download the file from the `target` folder.

Approach 3. Build the project locally follow the build instructions.

## Build Instructions
1. Download this repository or use `git clone` in command line to create a local copy
2. Open the local repository using your IDE and run Maven from there
3. In the Maven lifecylce run `package`, you should then have a `target` folder available
4. Find the jar file with the wording 'shaded' in it's filename, rename the jar file to `scheduler.jar`

## Run Instructions
Run the JAR firl with the command:
```
java -jar -Xmx4G scheduler.jar <INPUT.dot> P [OPTION]
```

Note: 
To run the jar file, ensure that the desired `INPUT.dot` file is in the same folder level as the `scheduler.jar`

`P` is the number of processors the input tasks needs to be scheduled on.

OPTION:
```-p N``` specifies N cores for execution in parallel
```-v``` specifies the launching of visualisation
```-o OUTPUT``` specifies the output filename

Examples:
* For an input file called 'input.dot' and running 3 processors:

    ```
    java -jar -Xmx4G scheduler.jar input.dot 3
    ```

* For an input file called 'input.dot', running 3 processors, specifying 2 cores, specifying lauching visualisation, and specifying an output file called 'outputFile.dot':

    ```
    java -jar -Xmx4G scheduler.jar input.dot 3 -p 2 -v -o outputFile
    ```

## 
