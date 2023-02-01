#!/usr/bin/env bash
startflink='/path/to/flink-1.11.6/bin/start-cluster.sh'
stopflink='/path/to/flink-1.11.6/bin/stop-cluster.sh'
flink='/path/to/flink-1.11.6/bin/flink'
resultFile='/path/to/CollectEcho.txt'
jar='/path/to/flink-cep-1.0-SNAPSHOT.jar'
data_path1='/path/to/QnV_R2000070.csv'
data_path2='/path/to/QnV_large.csv'
# You find the file here: https://gofile.io/d/89UwWg
data_path3='/path/to/luftdaten_11245.csv'
output_path='/path/to/CollectOutput.txt'

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now"  >> $resultFile
echo "----------$today $now------------"  >> $resultFile
for loop in 1 2 3 4 5 6 7 8 9 10
do
 # iter 3
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERPattern_I2 $jar --input $data_path2 --output $output_path --times 3 --tput 87500 --vel 215
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q7_ITERPattern_I2 run "$loop "--pattern length 3 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERQuery_I2 $jar --input $data_path2 --output $output_path --times 3 --tput 112500 --vel 215
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q7_ITERQuery_I2 run "$loop "--pattern length 3 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  # iter 5
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERPattern_I2 $jar --input $data_path2 --output $output_path --times 5 --tput 75000 --vel 200
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q7_ITERPattern_I2 run "$loop "--pattern length 5 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERQuery_I2 $jar --input $data_path2 --output $output_path --times 5 --tput 112500 --vel 200
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q7_ITERQuery_I2 run "$loop "--pattern length 5 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERPattern_I2 $jar --input $data_path2 --output $output_path --times 7 --tput 75000 --vel 185
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q7_ITERPattern_I2 run "$loop "--pattern length 7 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERQuery_I2 $jar --input $data_path2 --output $output_path --times 7 --tput 110000 --vel 185
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q7_ITERQuery_I2 run "$loop "--pattern length 7 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  echo "Tasks executed"
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERPattern_I2 $jar --input $data_path2 --output $output_path --times 9 --tput 75000 --vel 180
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q7_ITERPattern_I2 run "$loop "--pattern length 9 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERQuery_I2 $jar --input $data_path2 --output $output_path --times 9 --tput 110000 --vel 180
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q7_ITERQuery_I2 run "$loop "--pattern length 9 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  echo "Tasks executed"
done


