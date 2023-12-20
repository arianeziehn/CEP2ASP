# Scripts

In this folder, we provide details about our Flink configurations (flink-conf.yaml) and the scripts for our evaluation section. 
At the top of each script, you find a set of variables (i.e., paths) that need to be adjusted for your cluster.

## Queries used in Experimental Evaluation 

| Query | Elementary Operators (§ 5.2) | Pattern Length | Scalability | 
|-------|:----------------------------:|:--------------:|-------------|
| Q1    |          SEQ<sub>1           |                |             |
| Q2    |                              |                |             |
| Q3    |                              |                |             |
| Q4    |                              |                |             |
| Q5    |          NOT<sub>1           |                |             |
| Q6    |                              |   ITER<sub>2   |             |
| Q7    |          ITER<sub>1          |   ITER<sub>3   |             |
| Q8    |                              |                |             |
| Q9    |                              |  SEQ<sub>2-6   |             |
| Q10   |                              |                | SEQ<sub>7   |
| Q11   |                              |                | ITER<sub>4  |

 
## Maximal Sustainable Throughput
Furthermore, we use threshold filters to control the output selectivity of a pattern. We evaluate the maximal maintainable throughput in preliminary experiments. Depending on your machines, 
you may need to adjust the throughputs (i.e., the ingestion rate) provided in the scripts. A maximal sustainable throughput is the maximal throughput the system can reach without creating backpressure on the upstream operators of the execution pipeline.
Thus, leading to a similar value for the ingestion rate and the maximal maintainable throughput. We exploratory identified the maximal maintainable throughput for each pattern and query using the ThroughputLogger in the util folder.
We ensure that the ingestion rate is equivalent to the derived average throughput (mean(result)) with a tolerance bound of 10%. 
Furthermore, we ensure that the standard deviation of all 10 runs is smaller than 5% and includes the median. 
You can use our R code below to verify your throughput results, which are printed in the Flink log files. 
```
path <- '/to/your/logFiles.txt'
result=c()
tput = # set ingestion rate

for (i in 1:10){ # runs 
    data <- read.csv(paste(path, paste(i,'.txt', sep=""), sep=""), header = FALSE, sep ="$")
    dataAGG <- aggregate(data0$V9, by = list(data0$V3), FUN = mean, na.rm=TRUE)
    result[i] <- sum(dataAGG$x)
}

mean(result) >= tput*0.9 && mean(result) <= tput*1.1
sd(result) <= tput*0.05
mean(result) + sd(result) >= median(result) && mean(result) - sd(result) <= median(result)
``` 

## Micro Benchmarks
We use the command line tool Dool [1] to monitor CPU und memory utilization in our scalability experiment. 

[1] https://github.com/scottchiefbaker/dool

