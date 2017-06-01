# clusterj-memory-usage-test

## Create the following table

```create table `test_table` (`partition_id` int, `id` int, int_col1 int, int_col2 int, str_col1 varchar(3000),
str_col2 varchar(3000), str_col3 varchar(3000), PRIMARY KEY (`partition_id`,`id`)) partition by key (partition_id);```


## Supported Parameters
`./run -help` to see all parameters

i.e. 

`./run -dbHost host-where-db-is-installed -numThreads 150 -totalOps 10000000 -batchSize 100`



**Note**
change the run script to change Xmx and DirectMemory size.


