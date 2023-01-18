#!/usr/bin/env bash
startflink='/path/to/flink-1.11.6/bin/start-cluster.sh'
stopflink='/path/to/flink-1.11.6/bin/stop-cluster.sh'
flink='/path/to/flink-1.11.6/bin/flink'
resultFile='/path/to/CollectEcho.txt'
jar='/path/to/flink-cep-1.0-SNAPSHOT.jar'
data_path1='/path/to/QnV_R2000070.csv'
data_path2='/path/to/QnV_large.csv'
# You find the file here: https://gofile.io/d/p8vJ6X
data_path3='/path/to/luftdaten_11245.csv'
output_path='/path/to/CollectOutput.txt'

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now"  >> $resultFile
echo "----------$today $now------------"  >> $resultFile
for loop in 1 2 3 4 5 6 7 8 9 10
do
  # iter 3 equals baseline experiment
  # iter 5
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERPattern_I1 $jar --input $data_path2 --output $output_path --times 5 --tput 87500 --vel 179
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q6_ITERPattern_I1 run "$loop "--pattern length 5 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERQuery_I1T $jar --input $data_path2 --output $output_path --times 5 --tput 105000 --vel 179
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q6_ITERQuery_I1T run "$loop "--pattern length 5 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERPattern_I1 $jar --input $data_path2 --output $output_path --times 7 --tput 60000 --sel 172
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q6_ITERPattern_I1 run "$loop "--pattern length 7 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERQuery_I1T $jar --input $data_path2 --output $output_path --times 7 --tput 105000 --sel 172
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q6_ITERQuery_I1T run "$loop "--pattern length 7 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  echo "Tasks executed"
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERPattern_I1 $jar --input $data_path2 --output $output_path --times 9 --tput 45000 --sel 168
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q6_ITERPattern_I1 run "$loop "--pattern length 9 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERQuery_I1T $jar --input $data_path2 --output $output_path --times 9 --tput 105000 --sel 168
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q6_ITERQuery_I1T run "$loop "--pattern length 9 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  echo "Tasks executed"
done


