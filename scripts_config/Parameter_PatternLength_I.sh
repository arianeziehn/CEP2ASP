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
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERPattern_I1 $jar --input $data_path2 --output $output_path --times 3 --tput 115000  --vel 176
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q6_ITERPattern_I1 run "$loop "--pattern length 3 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/PLIExp/FOut_PITER3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/PLIExp/FOut_PITER3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/PLIExp/FOut_PITER3T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/PLIExp/FOut_PITER3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERQuery_I1_3 $jar --input $data_path2 --output $output_path -times 3 --tput 115000  --vel 176
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q6_ITERQuery_I1T run "$loop "--pattern length 3 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/PLIExp/FOut_QITER3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/PLIExp/FOut_QITER3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/PLIExp/FOut_QITER3T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/PLIExp/FOut_QITER3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERQuery_I1_3_IVJ $jar --input $data_path2 --output $output_path -times 3 --tput 115000  --vel 176
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q6_ITERQuery_I1T_IVJ run "$loop "--pattern length 3 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/PLIExp/FOut_Q_IVJ_ITER3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/PLIExp/FOut_Q_IVJ_ITER3L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/PLIExp/FOut_Q_IVJ_ITER3T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/PLIExp/FOut_Q_IVJ_ITER3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERPattern_I1 $jar --input $data_path2 --output $output_path --times 6 --tput 115000 --vel 167
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q6_ITERPattern_I1 run "$loop "--pattern length 6 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/PLIExp/FOut_PITER6L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/PLIExp/FOut_PITER6L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/PLIExp/FOut_PITER6T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/PLIExp/FOut_PITER6T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERQuery_I1_6 $jar --input $data_path2 --output $output_path --times 6 --tput 115000 --vel 167
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q6_ITERQuery_I1T run "$loop "--pattern length 6 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/PLIExp/FOut_QITER6L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/PLIExp/FOut_QITER6L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/PLIExp/FOut_QITER6T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/PLIExp/FOut_QITER6T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERQuery_I1_6_IVJ $jar --input $data_path2 --output $output_path --times 6 --tput 115000 --vel 167
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q6_ITERQuery_I1T_IVJ run "$loop "--pattern length 6 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/PLIExp/FOut_Q_IVJ_ITER6L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/PLIExp/FOut_Q_IVJ_ITER6L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/PLIExp/FOut_Q_IVJ_ITER6T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/PLIExp/FOut_Q_IVJ_ITER6T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  echo "Tasks executed"
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERPattern_I1 $jar --input $data_path2 --output $output_path --times 9 --tput 115000 --vel 156
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q6_ITERPattern_I1 run "$loop "--pattern length 9 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/PLIExp/FOut_PITER9L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/PLIExp/FOut_PITER9L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/PLIExp/FOut_PITER9T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/PLIExp/FOut_PITER9T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERQuery_I1_9 $jar --input $data_path2 --output $output_path --times 9 --tput 115000 --vel 156
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q6_ITERQuery_I1T run "$loop "--pattern length 9 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/PLIExp/FOut_QITER9L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/PLIExp/FOut_QITER9L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/PLIExp/FOut_QITER9T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/PLIExp/FOut_QITER9T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  echo "Tasks executed"
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q6_ITERQuery_I1_9_IVJ $jar --input $data_path2 --output $output_path --times 9 --tput 115000 --vel 156
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q6_ITERQuery_I1T_IVJ run "$loop "--pattern length 9 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/to/PLIExp/FOut_Q_IVJ_ITER9L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/to/PLIExp/FOut_Q_IVJ_ITER9L_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/to/PLIExp/FOut_Q_IVJ_ITER9T_'$loop'.txt'
  cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/to/PLIExp/FOut_Q_IVJ_ITER9T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  echo "Tasks executed"
done
