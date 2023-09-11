#!/usr/bin/env bash
startflink='/home/ziehn-ldap/flink-1.11.6_SP/bin/start-cluster.sh'
stopflink='/home/ziehn-ldap/flink-1.11.6_SP/bin/stop-cluster.sh'
flink='/home/ziehn-ldap/flink-1.11.6_SP/bin/flink'
jar='/home/ziehn-ldap/flink-cep-1.0-SNAPSHOT_SP.jar'
output_path='/home/ziehn-ldap/result'
resultFile='/home/ziehn-ldap/CollectEchoSP.txt'
data_path2='/home/ziehn-ldap/QnV_large.csv' # You find the file here: https://gofile.io/d/pjglkV
data_path1='/home/ziehn-ldap/QnV_R2000070.csv'
data_path3='/home/ziehn-ldap/luftdaten_11245.csv'
output_path='/path/to/CollectOutputBL.txt'

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now" >>$resultFile
echo "----------$today $now------------" >>$resultFile
for loop in 1 2 3 4 5 6 7 8 9 10; do
#SEQ(2) --vel 175 --qua 250 (sel: 5*10^⁻7) is equivalent to Baseline
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -p 8 -c Q1_SEQPattern $jar --input $data_path2 --output $output_path --vel 175 --qua 250 --tput 110000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQPattern run "$loop " --vel 175 --qua 250 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_PSEQ1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_PSEQ1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_PSEQ1T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_PSEQ1T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery $jar --input $data_path2 --output $output_path --vel 175 --qua 250 --tput 110000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery run "$loop " --vel 175 --qua 250 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQ1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQ1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQ1T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQ1T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q1_SEQQuery_IntervalJoin $jar --input $data_path2 --output $output_path --vel 175 --qua 250 --tput 110000
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q1_SEQQuery_IntervalJoin run "$loop " --vel 175 --qua 250 : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ1T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QSEQIVJ1T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  # NOT(3)
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q5_NOTPattern $jar --input $data_path1 --inputAQ $data_path3 --output $output_path --vel 99 --qua 71 --pm2 38 --tput 220000 --iter 36
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q5_NOTPattern run "$loop " : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_PNSEQ1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_PNSEQ1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_PNSEQ1T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_PNSEQ1T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q5_NOTQuery $jar --input $data_path1 --inputAQ $data_path3 --output $output_path --vel 99 --qua 71 --pm2 38 --tput 220000 --iter 36
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q5_NOTQuery run "$loop " : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QNSEQ1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QNSEQ1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QNSEQ1T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QNSEQ1T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q5_NOTQuery_IntervalJoin $jar --input $data_path1 --inputAQ $data_path3 --output $output_path --vel 99 --qua 71 --pm2 38 --tput 220000 --iter 36
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q5_NOTQuery_IntervalJoin run "$loop " : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QNSEQIVJ1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QNSEQIVJ1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QNSEQIVJ1T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QNSEQIVJ1T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  # ITER(3)
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERPattern_I2 $jar --input $data_path2 --output $output_path --tput 125000 --times 3 --vel 186
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q7_ITERPattern_I2 run "$loop " : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_PITER1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_PITER1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_PITER1T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_PITER1T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERQuery_I1_3 $jar --input $data_path2 --output $output_path --tput 125000 --times 3 --vel 186
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q7_ITERQuery_I1_3 run "$loop " : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QITER1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QITER1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QITER1T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QITER1T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERQuery_I1_3_IVJ $jar --input $data_path2 --output $output_path --tput 125000 --times 3 --vel 186
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q7_ITERQuery_I1_3_IVJ run "$loop " : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QITER_IVJ_1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QITER_IVJ_1L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QITER_IVJ_1T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QITER_IVJ_1T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
  now=$(date +"%T")
  today=$(date +%d.%m.%y)
  echo "Current time : $today $now" >>$resultFile
  echo "Flink start" >>$resultFile
  $startflink
  START=$(date +%s)
  $flink run -c Q7_ITERQuery_I2 $jar --input $data_path2 --output $output_path --tput 125000 --times 3 --vel 186
  END=$(date +%s)
  DIFF=$((END - START))
  echo "Q7_ITERQuery_I2 run "$loop " : "$DIFF"s" >>$resultFile
  $stopflink
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QITER2L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/SelExp/FOut_QITER2L_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QITER2T_'$loop'.txt'
  cp '/home/ziehn-ldap/flink-1.11.6_SP/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.log' '/home/ziehn-ldap/SelExp/FOut_QITER2T_'$loop'.txt'
  echo "------------ Flink stopped ------------" >>$resultFile
done
echo "Tasks executed"
