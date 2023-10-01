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

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now" >>$resultFile
echo "-----$today $now------" >>$resultFile
for loop in 1 2 3 4 5 6 7 8 9 10; do
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -p 8 -c Q1_SEQPattern $jar -input $data_path2 -output $output_path -vel 175 -qua 250 -tput 95000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQPattern run "$loop " -vel 175 -qua 250 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/BaseExp/FOut_PSEQ1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/BaseExp/FOut_PSEQ1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/BaseExp/FOut_PSEQ1T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/BaseExp/FOut_PSEQ1T_'$loop'.txt'
  echo "------ Flink stopped ------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery $jar -input $data_path2 -output $output_path -vel 175 -qua 250 -tput 150000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery run "$loop " -vel 175 -qua 250 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/BaseExp/FOut_QSEQ1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/BaseExp/FOut_QSEQ1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/BaseExp/FOut_QSEQ1T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/BaseExp/FOut_QSEQ1T_'$loop'.txt'
  echo "------ Flink stopped ------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery_IntervalJoin $jar -input $data_path2 -output $output_path -vel 175 -qua 250 -tput 150000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery_IntervalJoin run "$loop " -vel 175 -qua 250 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/BaseExp/FOut_QSEQIVJ1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/BaseExp/FOut_QSEQIVJ1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/BaseExp/FOut_QSEQIVJ1T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/BaseExp/FOut_QSEQIVJ1T_'$loop'.txt'
  echo "------ Flink stopped ------" >>$resultFile
  # NOT(3)
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q5_NOTPattern $jar -input $data_path1 -inputAQ $data_path3 -output $output_path -vel 99 -qua 71 -pm2 38 -tput 60000 -iter 36
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q5_NOTPattern run "$loop " : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/BaseExp/FOut_PNSEQ1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/BaseExp/FOut_PNSEQ1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/BaseExp/FOut_PNSEQ1T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/BaseExp/FOut_PNSEQ1T_'$loop'.txt'
  echo "------ Flink stopped ------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q5_NOTQuery $jar -input $data_path1 -inputAQ $data_path3 -output $output_path -vel 99 -qua 71 -pm2 38 -tput 170000 -iter 36
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q5_NOTQuery run "$loop " : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/BaseExp/FOut_QNSEQ1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/BaseExp/FOut_QNSEQ1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/BaseExp/FOut_QNSEQ1T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/BaseExp/FOut_QNSEQ1T_'$loop'.txt'
  echo "------ Flink stopped ------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q5_NOTQuery_IntervalJoin $jar -input $data_path1 -inputAQ $data_path3 -output $output_path -vel 99 -qua 71 -pm2 38 -tput 140000 -iter 36
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q5_NOTQuery_IntervalJoin run "$loop " : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/BaseExp/FOut_QNSEQIVJ1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/BaseExp/FOut_QNSEQIVJ1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/BaseExp/FOut_QNSEQIVJ1T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/BaseExp/FOut_QNSEQIVJ1T_'$loop'.txt'
  echo "------ Flink stopped ------" >>$resultFile
  # ITER(3)
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERPattern_I2 $jar -input $data_path2 -output $output_path -tput 100000 -times 3 -vel 186
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q7_ITERPattern_I2 run "$loop " : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/BaseExp/FOut_PITER1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/BaseExp/FOut_PITER1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/BaseExp/FOut_PITER1T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/BaseExp/FOut_PITER1T_'$loop'.txt'
  echo "------ Flink stopped ------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERQuery_I1_3 $jar -input $data_path2 -output $output_path -tput 150000 -times 3 -vel 186
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q7_ITERQuery_I1_3 run "$loop " : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/BaseExp/FOut_QITER1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/BaseExp/FOut_QITER1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/BaseExp/FOut_QITER1T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/BaseExp/FOut_QITER1T_'$loop'.txt'
  echo "------ Flink stopped ------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERQuery_I1_3_IVJ $jar -input $data_path2 -output $output_path -tput 155000 -times 3 -vel 186
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q7_ITERQuery_I1_3_IVJ run "$loop " : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/BaseExp/FOut_QITER_IVJ_1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/BaseExp/FOut_QITER_IVJ_1L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/BaseExp/FOut_QITER_IVJ_1T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/BaseExp/FOut_QITER_IVJ_1T_'$loop'.txt'
  echo "------ Flink stopped ------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERQuery_I2 $jar -input $data_path2 -output $output_path -tput 175000 -times 3 -vel 186
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q7_ITERQuery_I2 run "$loop " : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/BaseExp/FOut_QITER2L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/BaseExp/FOut_QITER2L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/BaseExp/FOut_QITER2T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/BaseExp/FOut_QITER2T_'$loop'.txt'
  echo "------ Flink stopped ------" >>$resultFile
done
echo "Tasks executed"
