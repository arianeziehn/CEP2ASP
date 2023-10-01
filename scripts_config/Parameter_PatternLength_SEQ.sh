#!/usr/bin/env bash
startflink='/path/to/flink-1.11.6/bin/start-cluster.sh'
stopflink='/path/to/flink-1.11.6/bin/stop-cluster.sh'
flink='/path/to/flink-1.11.6/bin/flink'
jar='/path/to/flink-cep-1.0-SNAPSHOT.jar'
output_path='/path/to/result'
resultFile='/path/to/CollectEcho.txt'
data_path2='/path/to/QnV.csv' 
data_path1='/path/to/QnV_R2000070.csv'
data_path3='/path/to/luftdaten_11245.csv'
data_path4='/path/to/luftdaten_11246.csv'

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now" >>$resultFile
echo "----------$today $now------------" >>$resultFile
for loop in 1 2 3 4 5 6 7 8 9 10; do
  # Pattern Length 2
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQPatternLength2 $jar --inputQnV $data_path1 --output $output_path --vel 107 --qua 107 --iter 28 --tput 175000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQPatternLength2 run "$loop "--pattern length 2 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_PSEQ2L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_PSEQ2L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_PSEQ2T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_PSEQ2T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_2 $jar --inputQnV $data_path1 --output $output_path --vel 107 --qua 107 --iter 28 --tput 175000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength2 run "$loop "--pattern length 2 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_QSEQ2L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_QSEQ2L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_QSEQ2T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_QSEQ2T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_IVJ_2 $jar --inputQnV $data_path1 --output $output_path --vel 107 --qua 107 --iter 28 --tput 175000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryIVJLength2 run "$loop "--pattern length 2 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_QSEQ_IVJ_2L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_QSEQ_IVJ_2L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_QSEQ_IVJ_2T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_QSEQ_IVJ_2T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  # PatternLength 3
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQPatternLength4 $jar --inputQnV $data_path1 --inputPM $data_path3 --output $output_path --vel 104 --qua 104 --pms 15 --iter 28 --pattern 3 --tput 50000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQPatternLength4 run "$loop "--pattern length 3 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_PSEQ3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_PSEQ3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_PSEQ3T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_PSEQ3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_4 $jar --inputQnV $data_path1 --inputPM $data_path3 --output $output_path --vel 104 --qua 104 --pms 15 --iter 28 --pattern 3 --tput 175000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength4 run "$loop "--pattern length 3 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_QSEQ3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_QSEQ3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_QSEQ3T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_QSEQ3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_IVJ_4 $jar --inputQnV $data_path1 --inputPM $data_path3 --output $output_path --vel 104 --qua 104 --pms 15 --iter 28 --pattern 3 --tput 175000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength4 run "$loop "--pattern length 3 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_QSEQ_IVJ_3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_QSEQ_IVJ_3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_QSEQ_IVJ_3T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_QSEQ_IVJ_3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  # PatternLength 4
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQPatternLength4 $jar --inputQnV $data_path1 --inputPM $data_path3 --output $output_path --vel 104 --qua 104 --pms 15 --pmb 25 --iter 28 --pattern 4 --tput 50000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQPatternLength4 run "$loop "--pattern length 4 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_PSEQ4L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_PSEQ4L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_PSEQ4T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_PSEQ4T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_4 $jar --inputQnV $data_path1 --inputPM $data_path3 --output $output_path --vel 104 --qua 104 --pms 15 --pmb 25 --iter 28 --pattern 4 --tput 175000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength4 run "$loop "--pattern length 4: "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_QSEQ4L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_QSEQ4L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_QSEQ4T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_QSEQ4T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_IVJ_4 $jar --inputQnV $data_path1 --inputPM $data_path3 --output $output_path --vel 104 --qua 104 --pms 15 --pmb 25 --iter 28 --pattern 4 --tput 175000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength4 run "$loop "--pattern length 4: "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_QSEQ_IVJ_4L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_QSEQ_IVJ_4L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_QSEQ_IVJ_4T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_QSEQ_IVJ_4T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  # Pattern Length 5
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQPatternLength6 $jar --inputQnV $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 103 --qua 101 --pms 25 --pmb 27 --temp 17 --iter 28 --pattern 5 --tput 25000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQPatternLength6 run "$loop "--pattern length 5 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_PSEQ5L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_PSEQ5L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_PSEQ5T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_PSEQ5T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_6 $jar --inputQnV $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 103 --qua 101 --pms 25 --pmb 27 --temp 17 --iter 28 --pattern 5 --tput 175000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength6 run "$loop "--pattern length 5 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_QSEQ5L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_QSEQ5L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_QSEQ5T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_QSEQ5T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_IVJ_6 $jar --inputQnV $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 103 --qua 101 --pms 25 --pmb 27 --temp 17 --iter 28 --pattern 5 --tput 175000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength6 run "$loop "--pattern length 5 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_QSEQ_IVJ_5L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_QSEQ_IVJ_5L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_QSEQ_IVJ_5T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_QSEQ_IVJ_5T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  # Pattern Length 6
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQPatternLength6 $jar --inputQnV $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 103 --qua 101 --pms 25 --pmb 27 --temp 17 --hum 33 --iter 28 --pattern 6 --tput 10000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQPatternLength4 run "$loop "--pattern length 6 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_PSEQ6L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_PSEQ6L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_PSEQ6T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_PSEQ6T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_6 $jar --inputQnV $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 103 --qua 101 --pms 25 --pmb 27 --temp 17 --hum 33--iter 28 --pattern 6 --tput 175000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength6 run "$loop "--pattern length 6 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_QSEQ6L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_QSEQ6L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_QSEQ6T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_QSEQ6T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_IVJ_6 $jar --input $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 103 --qua 101 --pms 25 --pmb 27 --temp 17 --hum 33--iter 28 --pattern 6 --tput 160000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength6 run "$loop "--pattern length 6 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/SEQExp/FOut_QSEQ_IVJ_6L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/SEQExp/FOut_QSEQ_IVJ_6L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/SEQExp/FOut_QSEQ_IVJ_6T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/SEQExp/FOut_QSEQ_IVJ_6T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
done
