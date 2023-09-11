#!/usr/bin/env bash
startflink='/home/ziehn-ldap/flink-1.11.6_SP/bin/start-cluster.sh'
stopflink='/home/ziehn-ldap/flink-1.11.6_SP/bin/stop-cluster.sh'
flink='/home/ziehn-ldap/flink-1.11.6_SP/bin/flink'
jar='/home/ziehn-ldap/flink-cep-1.0-SNAPSHOT_SP.jar'
output_path='/home/ziehn-ldap/result'
resultFile='/home/ziehn-ldap/CollectEchoSP.txt'
data_path2='/home/ziehn-ldap/QnV_large.csv' # You find the file here: https://gofile.io/d/pjglkV

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now" >>$resultFile
echo "----------$today $now------------" >>$resultFile
for loop in 1 2 3 4 5 6 7 8 9 10; do
  #SEQ(2) --vel 150 --qua 200 (sel: 3*10^-5)
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -p 8 -c Q1_SEQPattern $jar --input $data_path2 --output $output_path --vel 150 --qua 200 --tput 67500
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQPattern run "$loop " --vel 150 --qua 200 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_PSEQ2L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_PSEQ2L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_PSEQ2T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_PSEQ2T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery $jar --input $data_path2 --output $output_path --vel 150 --qua 200 --tput 110000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery run "$loop " --vel 150 --qua 200 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQ2L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQ2L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQ2T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQ2T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery_IntervalJoin $jar --input $data_path2 --output $output_path --vel 150 --qua 200 --tput 110000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery_IntervalJoin run "$loop " --vel 150 --qua 200 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ2L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ2L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ2T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ2T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  #SEQ(2) --vel 125 --qua 200 (sel: 6*10^-4)
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -p 8 -c Q1_SEQPattern $jar --input $data_path2 --output $output_path --vel 125 --qua 200 --tput 13500
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQPattern run "$loop " --vel 125 --qua 200: "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_PSEQ3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_PSEQ3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_PSEQ3T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_PSEQ3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery $jar --input $data_path2 --output $output_path --vel 125 --qua 200 --tput 110000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery run "$loop " --vel 125 --qua 200 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQ3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQ3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQ3T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQ3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery_IntervalJoin $jar --input $data_path2 --output $output_path --vel 125 --qua 200 --tput 110000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery_IntervalJoin run "$loop " --vel 125 --qua 200 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ3T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  #SEQ(2) --vel 120 --qua 150 (sel: 0.01)
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -p 8 -c Q1_SEQPattern $jar --input $data_path2 --output $output_path --vel 120 --qua 150 --tput 7500
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQPattern run "$loop " --vel 120 --qua 150 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_PSEQ4L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_PSEQ4L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_PSEQ4T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_PSEQ4T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery $jar --input $data_path2 --output $output_path --vel 120 --qua 150 --tput 110000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery run "$loop " --vel 120 --qua 150 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQ4L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQ4L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQ4T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQ4T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery_IntervalJoin $jar --input $data_path2 --output $output_path --vel 120 --qua 150 --tput 110000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery run "$loop " --vel 120 --qua 150 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ4L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ4L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ4T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ4T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  #SEQ(2) --vel 100 --qua 150 (sel: 0.3)
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -p 8 -c Q1_SEQPattern $jar --input $data_path2 --output $output_path --vel 100 --qua 150 --tput 500
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQPattern run "$loop " --vel 100 --qua 150 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_PSEQ5L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_PSEQ5L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_PSEQ5T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_PSEQ5T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery $jar --input $data_path2 --output $output_path --vel 100 --qua 150 --tput 75000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery run "$loop " --vel 100 --qua 150 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQ5L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQ5L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQ5T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQ5T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery_IntervalJoin $jar --input $data_path2 --output $output_path --vel 100 --qua 150 --tput 75000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery_IntervalJoin run "$loop " --vel 100 --qua 150 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ5L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ5L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ5T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ5T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
done
echo "Tasks executed"
