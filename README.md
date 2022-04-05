# Map Reduce Framework

Map Reduce is a power distribution framework for computation. In this project,
we aim on building this framework from scratch with some supervisor hint advice.

## Requirements

1. Maven (3.8.4)
2. Java (17.0.1)

## Project generation

```
mvn archetype:generate \
    -DgroupId=usth.master \
    -DartifactId=map_reduce_project \
    -DarchetypeArtifactId=maven-archetype-quickstart
```

## Project build

From the base directory, run:

```
mvn package
```

## Project run

### Daemon:

```
java -cp "map_reduce_project-0.1.0.jar:" usth.master.Driver -d binding_name:binding_port
```

### Client:

1. Create file "hosts" with each line corresponding to a worker following pattern:

```
worker_host:binding_port:binding_name
```

**NOTE** example could be seen in data/hosts

2. Run client

```
java -cp "map_reduce_project-0.1.0.jar:" usth.master.Driver -e hosts file_example.txt
```

**NOTE** file_example.txt is input file for wordcount map reduce

3. The result file will be notified after client finished.

## Bug report

Not yet successfully run on distributed mode ( multiple machine ) with error of
RMI connection fail.
