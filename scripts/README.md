# Scripts

In this folder we provide the scripts for our evaluation section. 

At the top of each script, you find a set of variables (i.e., paths) that need to be adjusted for your cluster. 

## 

| Query |    Baseline    | Parameter Evaluation | Scalability | 
|-------|:--------------:|----------------------|-------------|
| Q1    |     SEQ(2)     | SEQ with parameters  |             |
| Q2    | 𝐴𝑁𝐷(2;𝐶1)  |                      |             |
|       | 𝐴𝑁𝐷(2;𝐶2)* |                      |             |
| Q3    |    OR(2,D1)    |                      |             |
| Q4    |    OR(3,D2)    |                      |             |
| Q5    |     NOT(3)     |                      |             |
| Q6    |   ITER(3,I1)   |                      |             |
| Q7    |   ITER(3,I2)   |                      |             |

Note: * For C2 uncomment ```.keyby()``` in Q2 files. 
 
## Maximal Maintainable Throughput
Furthermore, we use threshold filters and the maximal maintainable throughput evaluated in preliminary experiments. Depending on your machines, 
you may need to adjust the throughput's. A maximal maintainable throughput is the maximal throughput the system can reach without creating backpressure on the upstream operators of the execution pipeline.
We exploratory identified the maximal maintainable throughput for each pattern and query using the ThroughputLogger in the util folder.
We made sure that the defined throughput is equivalent to the derived average throughput (mean(result)) with a tolerance bound of 10%. 
Furthermore, we made sure that the standard deviation of all 10 runs is smaller than 5% and includes the median. 
You can use our R code below to verify your throughput results which are printed in the flink log files. 
```
path <- '/to/your/logFiles.txt'
result=c()
tput = # set throuhput 

for (i in 1:10){ # runs 
    data <- read.csv(paste(path, paste(i,'.txt', sep=""), sep=""), header = FALSE, sep ="$")
    dataAGG <- aggregate(data$V8, by = list(data$V2,data$V6), FUN = mean)
    dataAGG2 <- aggregate(dataAGG$x, by = list(dataAGG$Group.1), FUN = sum)
    result[i] <- sum(dataAGG2$x)
}

mean(result) >= tput*0.9 && mean(result) <= tput*1.1
sd(result) <= tput*0.05
mean(result) + sd(result) >= median(result) && mean(result) - sd(result) <= median(result)
``` 