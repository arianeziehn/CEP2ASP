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
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_PITER3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_PITER3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_PITER3T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_PITER3T_'$loop'.txt'
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
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_QITER3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_QITER3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_QITER3T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_QITER3T_'$loop'.txt'
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
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_Q_IVJ_ITER3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_Q_IVJ_ITER3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_Q_IVJ_ITER3T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_Q_IVJ_ITER3T_'$loop'.txt'
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
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_PITER6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_PITER6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_PITER6T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_PITER6T_'$loop'.txt'
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
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_QITER6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_QITER6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_QITER6T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_QITER6T_'$loop'.txt'
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
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_Q_IVJ_ITER6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_Q_IVJ_ITER6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_Q_IVJ_ITER6T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_Q_IVJ_ITER6T_'$loop'.txt'
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
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_PITER9L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_PITER9L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_PITER9T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_PITER9T_'$loop'.txt'
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
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_QITER9L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_QITER9L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_QITER9T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_QITER9T_'$loop'.txt'
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
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_Q_IVJ_ITER9L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/PLIExp/FOut_Q_IVJ_ITER9L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_Q_IVJ_ITER9T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/PLIExp/FOut_Q_IVJ_ITER9T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  echo "Tasks executed"
done
