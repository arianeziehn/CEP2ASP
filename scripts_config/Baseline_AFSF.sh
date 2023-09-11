#!/usr/bin/env bash
startflink='/home/ziehn-ldap/flink-1.11.6/bin/start-cluster.sh'
stopflink='/home/ziehn-ldap/flink-1.11.6/bin/stop-cluster.sh'
flink='/home/ziehn-ldap/flink-1.11.6/bin/flink'
resultFile='/local-ssd/ziehn-ldap/BaselineExp.txt'
jar='/home/ziehn-ldap/flink-cep-1.0-SNAPSHOT.jar'
output_path='/home/ziehn-ldap/result'

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now" >>$resultFile
echo "------------ Start Next Task Testing ------------" >>$resultFile
for loop in 1 2 3; do
    #NSEQ(2) Pattern - new
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c PNSEQ_E1 $jar --output $output_path --tput 350000 --run 20
    END=$(date +%s)
    DIFF=$((END - START))
    echo "PNSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
    $stopflink
    echo "------------ Flink stopped ------------" >>$resultFile
    #cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultPNSEQL_E1/FOut_'$loop'_'$p'_'$tput'.txt'
    #cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultPNSEQL_E1/FOut_'$loop'_'$p'_'$tput'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultPNSEQT_E1/FLog_'$loop'_8_350000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultPNSEQT_E1/FLog_'$loop'_8_350000.txt'
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 16 -c QNSEQ_E1 $jar --output $output_path --tput 450000 --run 20
    END=$(date +%s)
    DIFF=$((END - START))
    echo "QNSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
    $stopflink
    echo "------------ Flink stopped ------------" >>$resultFile
    #cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQNSEQL_E1/FOut_'$loop'_16_250000.txt'
    #cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQNSEQL_E1/FOut_'$loop'_16_250000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultQNSEQT_E1/FLog_'$loop'_16_450000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultQNSEQT_E1/FLog_'$loop'_16_450000.txt'
    #SEQ(2) Query
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c QSEQ_E1 $jar --output $output_path --tput 200000 --run 20 --sel 5
    END=$(date +%s)
    DIFF=$((END - START))
    echo "QSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
    $stopflink
    echo "------------ Flink stopped ------------" >>$resultFile
    #cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQSEQL_E1/FOut_'$loop'_8_300000.txt'
    #cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-13.out' '/home/ziehn-ldap/BaselineExp/resultQSEQL_E1/FOut_'$loop'_8_300000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultQSEQT_E1/FLogS5_'$loop'_8_200000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultQSEQT_E1/FLogS5_'$loop'_8_200000.txt'
done
#get first latency results
for loop in 1 2 3 4 5 6 7 8 9 10; do
    # Elementary SEQ (Length 2, Sel 1%, Window 100, 20 min runtime)
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c QSEQ_E1 $jar --output $output_path --tput 550000 --run 20
    END=$(date +%s)
    DIFF=$((END - START))
    echo "QSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
    $stopflink
    echo "------------ Flink stopped ------------" >>$resultFile
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultQSEQL_E1/FOut_'$loop'_8_550000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultQSEQL_E1/FOut_'$loop'_8_550000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultQSEQT_E1/FLog_'$loop'_8_550000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultQSEQT_E1/FLog_'$loop'_8_550000.txt'
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c PSEQ_E1 $jar --output $output_path --tput 250000 --run 20
    END=$(date +%s)
    DIFF=$((END - START))
    echo "PSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
    $stopflink
    echo "------------ Flink stopped ------------" >>$resultFile
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultPSEQL_E1/FOut_'$loop'_8_250000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultPSEQL_E1/FOut_'$loop'_8_250000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultPSEQT_E1/FLog_'$loop'_8_250000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultPSEQT_E1/FLog_'$loop'_8_250000.txt'
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 16 -c QSEQ_E1_IntervalJoin $jar --output $output_path --tput 400000 --run 20
    END=$(date +%s)
    DIFF=$((END - START))
    echo "QSEQ_E1_IntervalJoin run "$loop " : "$DIFF"s" >>$resultFile
    $stopflink
    echo "------------ Flink stopped ------------" >>$resultFile
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultQSEQL_IVJ_E1/FOut_'$loop'_16_400000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultQSEQL_IVJ_E1/FOut_'$loop'_16_400000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultQSEQT_IVJ_E1/FLog_'$loop'_16_400000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultQSEQT_IVJ_E1/FLog_'$loop'_16_400000.txt'
    # Elementary Iter (Length 3, window 100, sel 1% ) IVJ discarded due to preliminary exp. (low throughput)
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c QITER_E1 $jar --output $output_path --tput 150000 --run 20
    END=$(date +%s)
    DIFF=$((END - START))
    echo "QITER_E1 run "$loop " : "$DIFF"s" >>$resultFile
    $stopflink
    echo "------------ Flink stopped ------------" >>$resultFile
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultQITERL_E1/FOut_'$loop'_8_150000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultQITERL_E1/FOut_'$loop'_8_150000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultQITERT_E1/FLog_'$loop'_8_150000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultQITERT_E1/FLog_'$loop'_8_150000.txt'
    # P ITER
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c PITER_E1 $jar --output $output_path --tput 150000 --run 20
    END=$(date +%s)
    DIFF=$((END - START))
    echo "PITER_E1 run "$loop " : "$DIFF"s" >>$resultFile
    $stopflink
    echo "------------ Flink stopped ------------" >>$resultFile
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultPITERL_E1/FOut_'$loop'_8_150000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultPITERL_E1/FOut_'$loop'_8_150000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultPITERT_E1/FLog_'$loop'_8_150000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultPITERT_E1/FLog_'$loop'_8_150000.txt'
    #Elementary NSEQ
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 16 -c QNSEQ_E1 $jar --output $output_path --tput 450000 --run 20
    END=$(date +%s)
    DIFF=$((END - START))
    echo "QNSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
    $stopflink
    echo "------------ Flink stopped ------------" >>$resultFile
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultQNSEQL_E1/FOut_'$loop'_16_450000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultQNSEQL_E1/FOut_'$loop'_16_450000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultQNSEQT_E1/FLog_'$loop'_16_450000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultQNSEQT_E1/FLog_'$loop'_16_450000.txt'
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c PSEQ_E1 $jar --output $output_path --tput 150000 --run 20 --sel 5
    END=$(date +%s)
    DIFF=$((END - START))
    echo "PSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
    $stopflink
    echo "------------ Flink stopped ------------" >>$resultFile
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultPSEQL_E1/FOutS5_'$loop'_8_150000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultPSEQL_E1/FOutS5_'$loop'_8_150000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultPSEQT_E1/FLogS5_'$loop'_8_150000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultPSEQT_E1/FLogS5_'$loop'_8_150000.txt'
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c PSEQ_E1 $jar --output $output_path --tput 100000 --run 20 --sel 10
    END=$(date +%s)
    DIFF=$((END - START))
    echo "PSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
    $stopflink
    echo "------------ Flink stopped ------------" >>$resultFile
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultPSEQL_E1/FOutS10_'$loop'_8_100000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultPSEQL_E1/FOutS10_'$loop'_8_100000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultPSEQT_E1/FLogS10_'$loop'_8_100000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultPSEQT_E1/FLogS10_'$loop'_8_100000.txt'
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c QSEQ_E1 $jar --output $output_path --tput 100000 --run 20 --sel 10
    END=$(date +%s)
    DIFF=$((END - START))
    echo "QSEQ_E1 run "$loop " : "$DIFF"s" >>$resultFile
    $stopflink
    echo "------------ Flink stopped ------------" >>$resultFile
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultQSEQL_E1/FOutS10_'$loop'_8_100000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/BaselineExp/resultQSEQL_E1/FOutS10_'$loop'_8_100000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultQSEQT_E1/FLogS10_'$loop'_8_100000.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/BaselineExp/resultQSEQT_E1/FLogS10_'$loop'_8_100000.txt'
done
echo "Tasks executed"
