#!/usr/bin/env bash
startflink='/home/ziehn-ldap/flink-1.11.6_SP/bin/start-cluster.sh'
stopflink='/home/ziehn-ldap/flink-1.11.6_SP/bin/stop-cluster.sh'
flink='/home/ziehn-ldap/flink-1.11.6_SP/bin/flink'
jar='/home/ziehn-ldap/flink-cep-1.0-SNAPSHOT_SP.jar'
output_path='/home/ziehn-ldap/result'
resultFile='/home/ziehn-ldap/CollectEchoSEQ.txt'
data_path2='/home/ziehn-ldap/QnV_large.csv' # You find the file here: https://gofile.io/d/pjglkV
data_path1='/home/ziehn-ldap/QnV_R2000070.csv'
data_path3='/home/ziehn-ldap/luftdaten_11245.csv'
output_path='/path/to/CollectOutputSEQL.txt'
data_path4='/home/ziehn-ldap/luftdaten_11246.csv'


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
  $flink run -c Q9_SEQPatternLength2 $jar --inputQnV $data_path1 --output $output_path --vel 107 --qua 107 --iter 28 --tput 85000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQPatternLength2 run "$loop "--pattern length 2 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_PSEQ2L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_PSEQ2L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_PSEQ2T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_PSEQ2T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_2 $jar --inputQnV $data_path1 --output $output_path --vel 107 --qua 107 --iter 28 --tput 105000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength2 run "$loop "--pattern length 2 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ2L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ2L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ2T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ2T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_IVJ_2 $jar --inputQnV $data_path1 --output $output_path --vel 107 --qua 107 --iter 28 --tput 105000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryIVJLength2 run "$loop "--pattern length 2 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_2L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_2L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_2T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_2T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  # PatternLength 3
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQPatternLength4 $jar --inputQnV $data_path1 --inputPM $data_path3 --output $output_path --vel 104 --qua 104 --pms 15 --iter 28 --pattern 3 --tput 85000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQPatternLength4 run "$loop "--pattern length 3 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_PSEQ3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_PSEQ3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_PSEQ3T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_PSEQ3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_4 $jar --inputQnV $data_path1 --inputPM $data_path3 --output $output_path --vel 104 --qua 104 --pms 15 --iter 28 --pattern 3 --tput 105000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength4 run "$loop "--pattern length 3 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ3T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_IVJ_4 $jar --inputQnV $data_path1 --inputPM $data_path3 --output $output_path --vel 104 --qua 104 --pms 15 --iter 28 --pattern 3 --tput 105000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength4 run "$loop "--pattern length 3 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_3T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  # PatternLength 4
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQPatternLength4 $jar --inputQnV $data_path1 --inputPM $data_path3 --output $output_path --vel 104 --qua 104 --pms 15 --pmb 25 --iter 28 --pattern 4 --tput 75000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQPatternLength4 run "$loop "--pattern length 4 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_PSEQ4L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_PSEQ4L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_PSEQ4T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_PSEQ4T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_4 $jar --inputQnV $data_path1 --inputPM $data_path3 --output $output_path --vel 104 --qua 104 --pms 15 --pmb 25 --iter 28 --pattern 4 --tput 105000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength4 run "$loop "--pattern length 4: "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ4L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ4L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ4T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ4T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_IVJ_4 $jar --inputQnV $data_path1 --inputPM $data_path3 --output $output_path --vel 104 --qua 104 --pms 15 --pmb 25 --iter 28 --pattern 4 --tput 105000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength4 run "$loop "--pattern length 4: "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_4L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_4L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_4T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_4T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  # Pattern Length 5
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQPatternLength6 $jar --inputQnV $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 103 --qua 101 --pms 25 --pmb 27 --temp 17 --iter 28 --pattern 5 --tput 45000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQPatternLength6 run "$loop "--pattern length 5 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_PSEQ5L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_PSEQ5L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_PSEQ5T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_PSEQ5T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_6 $jar --input $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 103 --qua 101 --pms 25 --pmb 27 --temp 17 --iter 28 --pattern 5 --tput 105000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength6 run "$loop "--pattern length 5 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ5L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ5L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ5T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ5T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_IVJ_6 $jar --input $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 103 --qua 101 --pms 25 --pmb 27 --temp 17 --iter 28 --pattern 5 --tput 105000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength6 run "$loop "--pattern length 5 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_5L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_5L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_5T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_5T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  # Pattern Length 6
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQPatternLength6 $jar --inputQnV $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 103 --qua 101 --pms 25 --pmb 27 --temp 17 --hum 33--iter 28 --pattern 6 --tput 42500
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQPatternLength4 run "$loop "--pattern length 6 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_PSEQ6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_PSEQ6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_PSEQ6T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_PSEQ6T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_6 $jar --input $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 103 --qua 101 --pms 25 --pmb 27 --temp 17 --hum 33--iter 28 --pattern 6 --tput 105000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength6 run "$loop "--pattern length 6 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ6T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ6T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q9_SEQQuery_IVJ_6 $jar --input $data_path1 --inputPM $data_path3 --inputTH $data_path4 --output $output_path --vel 103 --qua 101 --pms 25 --pmb 27 --temp 17 --hum 33--iter 28 --pattern 6 --tput 105000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q9_SEQQueryLength6 run "$loop "--pattern length 6 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_6T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SEQExp/FOut_QSEQ_IVJ_6T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
done
