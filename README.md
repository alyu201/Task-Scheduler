# Milestone 1

## Task scheduler
This program creates a valid schedule that puts tasks into processors.
Both the input and output dot files contain a task graph with nodes representing tasks and edges representing the communication costs between tasks.
The output will additionally contain the processor number that the node/task should run in.

## How to access file
* scheduler.jar is located in the 'target' folder

## How to run file
* To run the jar file, ensure that the desired input.dot file is in the same folder level as the scheduler.jar.

* Options available: 
  * For specifying the name of the output file: ```-o```
  
* Examples of how to run the jar file in the command line:
  * For an input file called 'input.dot' and running 3 processors:
    ```java -jar -Xmx4G scheduler.jar input.dot 3```
  * For an input file called 'input.dot', running 3 processors and specifying an output file called 'outputFile.dot':
    ```java -jar -Xmx4G scheduler.jar input.dot 3 -o outputFile```

## 
