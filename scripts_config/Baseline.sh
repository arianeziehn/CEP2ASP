#!/usr/bin/env bash
startflink='/home/ziehn-ldap/flink-1.11.6/bin/start-cluster.sh'
stopflink='/home/ziehn-ldap/flink-1.11.6/bin/stop-cluster.sh'
flink='/home/ziehn-ldap/flink-1.11.6/bin/flink'
resultFile='/local-ssd/ziehn-ldap/BaselineExp.txt'
jar='/home/ziehn-ldap/flink-cep-1.0-SNAPSHOT.jar'
output_pathP='/local-ssd/ziehn-ldap/BaselineExp/resultPSEQ_E1'
output_pathQ='/local-ssd/ziehn-ldap/BaselineExp/resultQSEQ_E1'

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now" >>$resultFile
echo "----------$today $now------------" >>$resultFile
for loop in 1 2 3; do
  for p in 8 16; do
    for tput in 100000 200000 400000; do
      #SEQ(2)
      now=$(date +"%T")
      today=$(date +%d.%m.%y)
      echo "Current time : $today $now" >>$resultFile
      echo "Flink start" >>$resultFile
      $startflink
      START=$(date +%s)
      $flink run -p $p -c PSEQ_E1 $jar --output $output_pathP --tput $tput --run 20
      END=$(date +%s)
      DIFF=$((END - START))
      echo "PSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
      $stopflink
      echo "------------ Flink stopped ------------" >>$resultFile
      cp '/local-ssd/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultPSEQ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp '/local-ssd/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultPSEQ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp '/local-ssd/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-12.out' '/home/ziehn-ldap/BaselineExp/resultPSEQ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp '/local-ssd/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-12.out' '/home/ziehn-ldap/BaselineExp/resultPSEQ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp -R '/local-ssd/ziehn-ldap/BaselineExp/resultPSEQ_E1' '/home/ziehn-ldap/BaselineExp/resultPSEQ_E1/latency_'$loop'_'$p'_'$tput'/'
      now=$(date +"%T")
      today=$(date +%d.%m.%y)
      echo "Current time : $today $now" >>$resultFile
      echo "Flink start" >>$resultFile
      $startflink
      START=$(date +%s)
      $flink run -p $p -c QSEQ_E1 $jar --output $output_pathQ --tput $tput --run 20
      END=$(date +%s)
      DIFF=$((END - START))
      echo "QSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
      $stopflink
      echo "------------ Flink stopped ------------" >>$resultFile
      cp '/local-ssd/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQSEQ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp '/local-ssd/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQSEQ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp '/local-ssd/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-12.out' '/home/ziehn-ldap/BaselineExp/resultQSEQ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp '/local-ssd/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-12.out' '/home/ziehn-ldap/BaselineExp/resultQSEQ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp -R '/local-ssd/ziehn-ldap/BaselineExp/resultQSEQ_E1' '/home/ziehn-ldap/BaselineExp/resultQSEQ_E1/latency_'$loop'_'$p'_'$tput'/'
done
done
done
echo "Tasks executed"
