#!/usr/bin/env bash
startflink='/path/to/flink-1.11.6/bin/start-cluster.sh'
stopflink='/path/to/flink-1.11.6/bin/stop-cluster.sh'
flink='/path/to/flink-1.11.6/bin/flink'
resultFile='/path/to/CollectEcho.txt'
jar='/path/to/flink-cep-1.0-SNAPSHOT.jar'
data_path1='/path/to/QnV_R2000070.csv'
data_path2='/path/to/QnV_large.csv'
# You find the file here: https://www.file.io/HwE9/download/0PeYHMs05jDm
data_path3='/path/to/luftdaten_11245.csv'
output_path='/path/to/CollectOutput.txt'

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now"  >> $resultFile
echo "----------$today $now------------"  >> $resultFile
for loop in 1 2 3 4 5 6 7 8 9 10
do
  #SEQ(2) Window = 15 is equivalent to Baseline
  #SEQ(2) Window = 30
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQPattern $jar --input $data_path2 --output $output_path --wsize 30 --tput 110000
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q1_SEQPattern run "$loop " window size 30 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery $jar --input $data_path2 --output $output_path --wsize 30 --tput 110000
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q1_SEQQuery run "$loop " window size 30 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  # window size 90
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQPattern $jar --input $data_path2 --output $output_path --wsize 90 --tput 67500
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q1_SEQPattern run "$loop " window size 90 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery $jar --input $data_path2 --output $output_path --wsize 90 --tput 90000
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q1_SEQQuery run "$loop " window size 90 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  # window size 360
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQPattern $jar --input $data_path2 --output $output_path --wsize 360 --tput 27500
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q1_SEQPattern run "$loop " window size 360 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery $jar --input $data_path2 --output $output_path --wsize 360 --tput 90000
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q1_SEQQuery run "$loop " window size 360 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
done
echo "Tasks executed"





