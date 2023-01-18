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
data_path4='/path/to/luftdaten_11246.csv'
output_path='/path/to/CollectOutput.txt'

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now"  >> $resultFile
echo "----------$today $now------------"  >> $resultFile
for loop in 1 2 3 4 5 6 7 8 9 10
do
  # Pattern Length 2
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQPatternLength2 $jar --input $data_path1 --output $output_path --vel 115 --qua 105 --iter 28 --tput 85000
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q9_SEQPatternLength2 run "$loop "--pattern length 2 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQueryLength2 $jar --input $data_path1 --output $output_path --vel 115 --qua 105 --iter 28 --tput 105000
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q9_SEQQueryLength2 run "$loop "--pattern length 2 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  # PatternLength 3
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQPatternLength4 $jar --inputQnV $data_path1 --inputPM $data_path3 --output $output_path --vel 115 --qua 105 --pm2 5 --pm10 5 --iter 28 --pattern 3 --tput 85000
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q9_SEQPatternLength4 run "$loop "--pattern length 3 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQueryLength4 $jar --input $data_path1 --inputPM $data_path3 --output $output_path --vel 115 --qua 105 --pm2 5 --pm10 5 --iter 28 --pattern 3 --tput 105000
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q9_SEQQueryLength4 run "$loop "--pattern length 3 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
   # PatternLength 4
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQPatternLength4 $jar --inputQnV $data_path1 --inputPM $data_path3 --output $output_path --vel 115 --qua 105 --pm2 5 --pm10 5 --iter 28 --pattern 4 --tput 75000
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q9_SEQPatternLength4 run "$loop "--pattern length 4 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQueryLength4 $jar --input $data_path1 --inputPM $data_path3 --output $output_path --vel 115 --qua 105 --pm2 5 --pm10 5 --iter 28 --pattern 4 --tput 105000
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q9_SEQQueryLength4 run "$loop "--pattern length 4: "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  # Pattern Length 5
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQPatternLength6 $jar --inputQnV $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 115 --qua 105 --pm2 5 --pm10 5 --temp 15 --hum 45 --iter 28 --pattern 5 --tput 45000
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q9_SEQPatternLength6 run "$loop "--pattern length 5 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQueryLength6 $jar --input $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 115 --qua 105 --pm2 5 --pm10 5 --temp 15 --hum 45 --iter 28 --pattern 5 --tput 105000
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q9_SEQQueryLength6 run "$loop "--pattern length 5 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  # Pattern Length 6
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQPatternLength6 $jar --inputQnV $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 115 --qua 105 --pm2 5 --pm10 5 --temp 15 --hum 45 --iter 28 --pattern 6 --tput 42500
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q9_SEQPatternLength4 run "$loop "--pattern length 6 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now"  >> $resultFile
  echo "Flink start"  >> $resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQueryLength6 $jar --input $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 115 --qua 105 --pm2 5 --pm10 5 --temp 15 --hum 45 --iter 28 --pattern 6 --tput 105000
  END=$(date +%s)
  DIFF=$((END-START))
  echo "Q9_SEQQueryLength6 run "$loop "--pattern length 6 : "$DIFF"s" >> $resultFile
  $stopflink
  echo "------------ Flink stopped ------------" >> $resultFile
done


