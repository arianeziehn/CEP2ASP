#!/usr/bin/env bash
startflink='/path/to/flink-1.11.6/bin/start-cluster.sh'
stopflink='/path/to/flink-1.11.6/bin/stop-cluster.sh'
flink='/path/to/flink-1.11.6/bin/flink'
resultFile='/path/to/CollectEcho.txt'
jar='/path/to/flink-cep-1.0-SNAPSHOT.jar'
data_path5='/path/to/QnV_R2000070.csv'
data_path1='/path/to/QnV_R2000070_i.csv'
# You find the file here: https://gofile.io/d/pjglkV
data_path2='/path/to/QnV_large.csv'
# You find the file here: https://gofile.io/d/pjglkV
data_path3='/path/to/luftdaten_11245.csv'
data_path4='/path/to/luftdaten_11246.csv'
output_path='/path/to/CollectOutput.txt'

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now" >>$resultFile
echo "----------$today $now------------" >>$resultFile
#Note: you can use this script to run our scalability experiments, below we provide our throughput's 1) for Changing Data Characteristics and 2) for scale out
# 1) Queries: sensors 8: 225000,  sensors 16: 250000, sensors 32:	325000, sensors 64:	325000, sensors 128: 325000
# 1) Pattern: sensors 8: 200000,  sensors 16: 200000, sensors 32:	210000, sensors 64:	300000, sensors 128: 300000
# 2) Query: sensors 128 (2W) 225000, sensors 128 (4W) 225000
# 2) Query: sensors 128 (2W) 130000, sensors 128 (4W) 125000

for loop in 1 2 3 4 5 6 7 8 9 10; do
  for sensors in 8 16 32 64 128; do
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q8_SEQPatternLS $jar --input $data_path5 --output $output_path --sensors $sensors --vel 100 --qua 110 --tput 250000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q8_SEQPatternLS run "$loop "--sensors "$sensors ":"$DIFF"s" >>$resultFile
    $stopflink
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q8_SEQQueryLS $jar --input $data_path5 --output $output_path --sensors $sensors --vel 100 --qua 110 --tput 250000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q8_SEQQueryLS run "$loop "--sensors "$sensors " : "$DIFF"s" >>$resultFile
    $stopflink
    echo "------------ Flink stopped ------------" >>$resultFile
  done
done
