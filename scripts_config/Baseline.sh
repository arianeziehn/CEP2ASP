#!/usr/bin/env bash
startflink='/home/ziehn-ldap/flink-1.11.6/bin/start-cluster.sh'
stopflink='/home/ziehn-ldap/flink-1.11.6/bin/stop-cluster.sh'
flink='/home/ziehn-ldap/flink-1.11.6/bin/flink'
resultFile='/local-ssd/ziehn-ldap/BaselineExp.txt'
jar='/home/ziehn-ldap/flink-cep-1.0-SNAPSHOT.jar'
output_pathP='/home/ziehn-ldap/BaselineExp/resultPSEQ_E1'
output_pathQ='/home/ziehn-ldap/BaselineExp/resultQSEQ_E1'

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now" >>$resultFile
echo "----------$today $now------------" >>$resultFile
for loop in 1 2 3; do
 #SEQ(2)
 now=$(date +"%T")
 today=$(date +%d.%m.%y)
 echo "Current time : $today $now" >>$resultFile
 echo "Flink start" >>$resultFile
 $startflink
 START=$(date +%s)
 $flink run -p 8 -c PSEQ_E1 $jar --output $output_pathP'_'$loop'_8_300000' --tput 300000 --run 20
 END=$(date +%s)
 DIFF=$((END - START))
 echo "PSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
 $stopflink
 echo "------------ Flink stopped ------------" >>$resultFile
 cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultPSEQ_E1/FOut_'$loop'_8_300000.txt'
 cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultPSEQ_E1/FOut_'$loop'_8_300000.txt'
 cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultPSEQ_E1/FLog_'$loop'_8_300000.txt'
 cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultPSEQ_E1/FLog_'$loop'_8_300000.txt'
 now=$(date +"%T")
 today=$(date +%d.%m.%y)
 echo "Current time : $today $now" >>$resultFile
 echo "Flink start" >>$resultFile
 $startflink
 START=$(date +%s)
 $flink run -p 16 -c PSEQ_E1 $jar --output $output_pathP'_'$loop'_16_200000' --tput 200000 --run 20
 END=$(date +%s)
 DIFF=$((END - START))
 echo "PSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
 $stopflink
 echo "------------ Flink stopped ------------" >>$resultFile
 cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultPSEQ_E1/FOut_'$loop'_16_200000.txt'
 cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultPSEQ_E1/FOut_'$loop'_16_200000.txt'
 cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultPSEQ_E1/FLog_'$loop'_16_200000.txt'
 cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultPSEQ_E1/FLog_'$loop'_16_200000.txt'
 now=$(date +"%T")
 today=$(date +%d.%m.%y)
 echo "Current time : $today $now" >>$resultFile
 echo "Flink start" >>$resultFile
 $startflink
 START=$(date +%s)
 $flink run -p 8 -c QSEQ_E1 $jar --output $output_pathQ'_'$loop'_8_200000' --tput 200000 --run 20
 END=$(date +%s)
 DIFF=$((END - START))
 echo "QSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
 $stopflink
 echo "------------ Flink stopped ------------" >>$resultFile
 cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQSEQ_E1/FOut_'$loop'_'$p'_'$tput'.txt'
 cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQSEQ_E1/FOut_'$loop'_'$p'_'$tput'.txt'
 cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultQSEQ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
 cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultQSEQ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
done
echo "------------ Start Next Task ------------" >>$resultFile
for loop in 1 2 3; do
  for p in 8 16; do
    for tput in 200000 300000; do
      #SEQ(2)
      now=$(date +"%T")
      today=$(date +%d.%m.%y)
      echo "Current time : $today $now" >>$resultFile
      echo "Flink start" >>$resultFile
      $startflink
      START=$(date +%s)
      $flink run -p $p -c PITER_E1 $jar --output $output_pathP'_'$loop'_'$p'_'$tput --tput $tput --run 20
      END=$(date +%s)
      DIFF=$((END - START))
      echo "PITER_E1 run "$loop " : "$DIFF"s" >>$resultFile
      $stopflink
      echo "------------ Flink stopped ------------" >>$resultFile
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultPITER_E1/FOut_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultPITER_E1/FOut_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultPITER_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultPITER_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      now=$(date +"%T")
      today=$(date +%d.%m.%y)
      echo "Current time : $today $now" >>$resultFile
      echo "Flink start" >>$resultFile
      $startflink
      START=$(date +%s)
      $flink run -p $p -c QITER_E1 $jar --output $output_pathQ'_'$loop'_'$p'_'$tput --tput $tput --run 20
      END=$(date +%s)
      DIFF=$((END - START))
      echo "QITER_E1 run "$loop " : "$DIFF"s" >>$resultFile
      $stopflink
      echo "------------ Flink stopped ------------" >>$resultFile
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQITER_E1/FOut_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQITER_E1/FOut_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultQITER_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultQITER_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      now=$(date +"%T")
      today=$(date +%d.%m.%y)
      echo "Current time : $today $now" >>$resultFile
      echo "Flink start" >>$resultFile
      $startflink
      START=$(date +%s)
      $flink run -p $p -c QITER_E1_IntervalJoin $jar --output $output_pathQ'_'$loop'_'$p'_'$tput --tput $tput --run 20
      END=$(date +%s)
      DIFF=$((END - START))
      echo "QITER_E1_IntervalJoin run "$loop " : "$DIFF"s" >>$resultFile
      $stopflink
      echo "------------ Flink stopped ------------" >>$resultFile
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQITER_IVJ_E1/FOut_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQITER_IVJ_E1/FOut_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultQITER_IVJ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultQITER_IVJ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      now=$(date +"%T")
      today=$(date +%d.%m.%y)
      echo "Current time : $today $now" >>$resultFile
      echo "Flink start" >>$resultFile
      $startflink
      START=$(date +%s)
      $flink run -p $p -c QSEQ_E1_IntervalJoin $jar --output $output_pathQ'_'$loop'_'$p'_'$tput --tput $tput --run 20
      END=$(date +%s)
      DIFF=$((END - START))
      echo "QITER_E1_IntervalJoin run "$loop " : "$DIFF"s" >>$resultFile
      $stopflink
      echo "------------ Flink stopped ------------" >>$resultFile
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQSEQ_IVJ_E1/FOut_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQSEQ_IVJ_E1/FOut_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultQSEQ_IVJ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultQSEQ_IVJ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      #SEQ(2)
      now=$(date +"%T")
      today=$(date +%d.%m.%y)
      echo "Current time : $today $now" >>$resultFile
      echo "Flink start" >>$resultFile
      $startflink
      START=$(date +%s)
      $flink run -p $p -c PNSEQ_E1 $jar --output $output_pathP'_'$loop'_'$p'_'$tput --tput $tput --run 20
      END=$(date +%s)
      DIFF=$((END - START))
      echo "PNSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
      $stopflink
      echo "------------ Flink stopped ------------" >>$resultFile
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultPNSEQ_E1/FOut_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultPNSEQ_E1/FOut_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultPNSEQ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultPNSEQ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      now=$(date +"%T")
      today=$(date +%d.%m.%y)
      echo "Current time : $today $now" >>$resultFile
      echo "Flink start" >>$resultFile
      $startflink
      START=$(date +%s)
      $flink run -p $p -c QNSEQ_E1 $jar --output $output_pathQ'_'$loop'_'$p'_'$tput --tput $tput --run 20
      END=$(date +%s)
      DIFF=$((END - START))
      echo "QNSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
      $stopflink
      echo "------------ Flink stopped ------------" >>$resultFile
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQNSEQ_E1/FOut_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQNSEQ_E1/FOut_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultQNSEQ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultQNSEQ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      now=$(date +"%T")
      today=$(date +%d.%m.%y)
      echo "Current time : $today $now" >>$resultFile
      echo "Flink start" >>$resultFile
      $startflink
      START=$(date +%s)
      $flink run -p $p -c QNSEQ_E1_IntervalJoin $jar --output $output_pathQ'_'$loop'_'$p'_'$tput --tput $tput --run 20
      END=$(date +%s)
      DIFF=$((END - START))
      echo "QNSEQ_E1_IntervalJoin run "$loop " : "$DIFF"s" >>$resultFile
      $stopflink
      echo "------------ Flink stopped ------------" >>$resultFile
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQNSEQ_IVJ_E1/FOut_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQNSEQ_IVJ_E1/FOut_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultQNSEQ_IVJ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
      cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/BaselineExp/resultQNSEQ_IVJ_E1/FLog_'$loop'_'$p'_'$tput'.txt'
done
done
done
echo "Tasks executed"
