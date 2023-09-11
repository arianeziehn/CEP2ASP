#!/usr/bin/env bash
startflink='/home/ziehn-ldap/flink-1.11.6_W/bin/start-cluster.sh'
stopflink='/home/ziehn-ldap/flink-1.11.6_W/bin/stop-cluster.sh'
flink='/home/ziehn-ldap/flink-1.11.6_W/bin/flink'
jar='/home/ziehn-ldap/flink-cep-1.0-SNAPSHOT_O3.jar'
output_path='/home/ziehn-ldap/result'
resultFile='/home/ziehn-ldap/CollectEchoO3.txt'
data_path2='/home/ziehn-ldap/QnV_large.csv' # You find the file here: https://gofile.io/d/pjglkV

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now" >>$resultFile
echo "----------$today $now------------" >>$resultFile
for loop in 1 2 3 4 5 6 7 8 9 10; do
  # iter 3
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERPattern_I2 $jar --input $data_path2 --output $output_path --times 3 --tput 112500 --vel 180
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q7_ITERPattern_I2 run "$loop "--pattern length 3 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/O3Exp/FOut_PITER3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/O3Exp/FOut_PITER3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/O3Exp/FOut_PITER3T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/O3Exp/FOut_PITER3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERQuery_I2 $jar --input $data_path2 --output $output_path --times 3 --tput 112500 --vel 180
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q7_ITERQuery_I2 run "$loop "--pattern length 3 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/O3Exp/FOut_QITER3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/O3Exp/FOut_QITER3L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/O3Exp/FOut_QITER3T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/O3Exp/FOut_QITER3T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  # iter 6
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERPattern_I2 $jar --input $data_path2 --output $output_path --times 6 --tput 112500 --vel 178
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q7_ITERPattern_I2 run "$loop "--pattern length 6 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/O3Exp/FOut_PITER6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/O3Exp/FOut_PITER6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/O3Exp/FOut_PITER6T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/O3Exp/FOut_PITER6T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERQuery_I2 $jar --input $data_path2 --output $output_path --times 6 --tput 112500 --vel 178
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q7_ITERQuery_I2 run "$loop "--pattern length 6 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/O3Exp/FOut_QITER6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/O3Exp/FOut_QITER6L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/O3Exp/FOut_QITER6T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/O3Exp/FOut_QITER6T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERPattern_I2 $jar --input $data_path2 --output $output_path --times 9 --tput 112500 --vel 174
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q7_ITERPattern_I2 run "$loop "--pattern length 9 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/O3Exp/FOut_PITER9L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/O3Exp/FOut_PITER9L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/O3Exp/FOut_PITER9T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/O3Exp/FOut_PITER9T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERQuery_I2 $jar --input $data_path2 --output $output_path --times 9 --tput 112500 --vel 174
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q7_ITERQuery_I2 run "$loop "--pattern length 9 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/O3Exp/FOut_QITER9L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/O3Exp/FOut_QITER9L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/O3Exp/FOut_QITER9T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/O3Exp/FOut_QITER9T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  echo "Tasks executed"
done
