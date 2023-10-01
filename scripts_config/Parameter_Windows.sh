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
echo "----------$today $now------------" >>$resultFile
for loop in 1 2 3 4 5 6 7 8 9 10; do
  #SEQ(2) Window = 15 is equivalent to Baseline
  #SEQ(2) Window = 30
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQPattern $jar --input $data_path2 --output $output_path --wsize 30 --tput 110000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQPattern run "$loop " window size 30 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/WExp/FOut_PSEQW2L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/WExp/FOut_PSEQW2L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/WExp/FOut_PSEQW2T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/WExp/FOut_PSEQW2T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery $jar --input $data_path2 --output $output_path --wsize 30 --tput 160000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery run "$loop " window size 30 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/WExp/FOut_QSEQW2L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/WExp/FOut_QSEQW2L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/WExp/FOut_QSEQW2T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/WExp/FOut_QSEQW2T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery_IntervalJoin $jar --input $data_path2 --output $output_path --wsize 30 --tput 160000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery_IntervalJoin run "$loop " window size 30 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/WExp/FOut_QSEQIVJW2L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/WExp/FOut_QSEQIVJW2L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/WExp/FOut_QSEQIVJW2T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/WExp/FOut_QSEQIVJW2T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  # window size 90
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQPattern $jar --input $data_path2 --output $output_path --wsize 90 --tput 75000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQPattern run "$loop " window size 90 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/WExp/FOut_PSEQW3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/WExp/FOut_PSEQW3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/WExp/FOut_PSEQW3T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/WExp/FOut_PSEQW3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery $jar --input $data_path2 --output $output_path --wsize 90 --tput 160000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery run "$loop " window size 90 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/WExp/FOut_QSEQW3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/WExp/FOut_QSEQW3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/WExp/FOut_QSEQW3T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/WExp/FOut_QSEQW3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery_IntervalJoin $jar --input $data_path2 --output $output_path --wsize 90 --tput 160000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery_IntervalJoin run "$loop " window size 90 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/WExp/FOut_QSEQIVJW3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/WExp/FOut_QSEQIVJW3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/WExp/FOut_QSEQIVJW3T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/WExp/FOut_QSEQIVJW3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  # window size 360
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQPattern $jar --input $data_path2 --output $output_path --wsize 360 --tput 30000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQPattern run "$loop " window size 360 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/WExp/FOut_PSEQW4L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/WExp/FOut_PSEQW4L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/WExp/FOut_PSEQW4T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/WExp/FOut_PSEQW4T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery $jar --input $data_path2 --output $output_path --wsize 360 --tput 160000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery run "$loop " window size 360 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/WExp/FOut_QSEQW4L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/WExp/FOut_QSEQW4L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/WExp/FOut_QSEQW4T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/WExp/FOut_QSEQW4T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery_IntervalJoin $jar --input $data_path2 --output $output_path --wsize 360 --tput 160000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery_IntervalJoin run "$loop " window size 360 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/WExp/FOut_QSEQIVJW4L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/WExp/FOut_QSEQIVJW4L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/WExp/FOut_QSEQIVJW4T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/WExp/FOut_QSEQIVJW4T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
done
echo "Tasks executed"
