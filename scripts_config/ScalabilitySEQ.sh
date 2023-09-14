#!/usr/bin/env bash
startflink='/home/ziehn-ldap/flink-1.11.6_W/bin/start-cluster.sh'
stopflink='/home/ziehn-ldap/flink-1.11.6_W/bin/stop-cluster.sh'
flink='/home/ziehn-ldap/flink-1.11.6_W/bin/flink'
jar='/home/ziehn-ldap/flink-cep-1.0-SNAPSHOT_W.jar'
output_path='/home/ziehn-ldap/result'
resultFile='/home/ziehn-ldap/CollectEchoW.txt'
data_path5='/home/ziehn-ldap/QnV_R2000070_integrated.csv'
data_path6='/home/ziehn-ldap/luftdaten_11245_integrated.csv'

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now" >>$resultFile
echo "----------$today $now------------" >>$resultFile
#Note: you can use this script to run our scalability experiments, below we provide our throughput's 1) for Changing Data Characteristics and 2) for scale out
# 1) Queries: sensors 8: 225000,  sensors 16: 250000, sensors 32:	325000, sensors 64:	325000, sensors 128: 325000
# 1) Pattern: sensors 8: 200000,  sensors 16: 200000, sensors 32:	210000, sensors 64:	300000, sensors 128: 300000
# 2) Query: sensors 128 (2W) 225000, sensors 128 (4W) 225000
# 2) Query: sensors 128 (2W) 130000, sensors 128 (4W) 125000

for loop in 1 2 3; do
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c Q10_SEQ3PatternLS $jar --inputQnV $data_path5 --inputPM $data_path6 --output $output_path --sensors 8 --vel 78 --qua 67 --pmb 64 --tput 200000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3PatternLS run "$loop "--sensors 8:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_PSEQW8L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_PSEQW8L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_PSEQW8T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_PSEQW8T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c Q10_SEQ3QueryLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 8 --vel 78 --qua 67 --pmb 64 --tput 225000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 8:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQW8L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQW8L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQW8T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQW8T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c Q10_SEQ3Query_IVJ_LS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 8 --vel 78 --qua 67 --pmb 64 --tput 225000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 8:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW8L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW8L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW8T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW8T_'$loop'.txt'
    echo "------------ Flink stopped sensors 8 ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3PatternLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 16 --vel 78 --qua 67 --pmb 64 --tput 250000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3PatternLS run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_PSEQW16L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_PSEQW16L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_PSEQW16T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_PSEQW16T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3QueryLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 16 --vel 78 --qua 67 --pmb 64 --tput 250000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQW16L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQW16L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQW16T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQW16T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3Query_IVJ_LS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 16 --vel 78 --qua 67 --pmb 64 --tput 250000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW16L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW16L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW16T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW16T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3PatternLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 32 --vel 78 --qua 67 --pmb 64 --tput 210000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3PatternLS run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_PSEQW32L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_PSEQW32L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_PSEQW32T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_PSEQW32T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3QueryLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 32 --vel 78 --qua 67 --pmb 64 --tput 325000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQW32L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQW32L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQW32T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQW32T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3Query_IVJ_LS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 32 --vel 78 --qua 67 --pmb 64 --tput 325000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW32L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW32L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW32T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW32T_'$loop'.txt'
    echo "------------ Flink stopped sensors 32 ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3PatternLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 64 --vel 78 --qua 67 --pmb 64 --tput 300000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3PatternLS run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_PSEQW64L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_PSEQW64L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_PSEQW64T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_PSEQW64T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3QueryLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 64 --vel 78 --qua 67 --pmb 64 --tput 350000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQW64L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQW64L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQW64T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQW64T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3Query_IVJ_LS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 64 --vel 78 --qua 67 --pmb 64 --tput 350000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW64L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW64L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW64T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW64T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3PatternLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 128 --vel 78 --qua 67 --pmb 64 --tput 300000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3PatternLS run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_PSEQW128L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_PSEQW128L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_PSEQW128T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_PSEQW128T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3QueryLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 128 --vel 78 --qua 67 --pmb 64 --tput 350000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQW128L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQW128L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQW128T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQW128T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3Query_IVJ_LS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 128 --vel 78 --qua 67 --pmb 64 --tput 350000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW128L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.out' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW128L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW128T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6_W/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-48.log' '/home/ziehn-ldap/WExp/FOut_QSEQIVJW128T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
done
