#!/usr/bin/env bash
startflink='/path/to/flink-1.11.6/bin/start-cluster.sh'
stopflink='/path/to/flink-1.11.6/bin/stop-cluster.sh'
flink='/path/to/flink-1.11.6/bin/flink'
jar='/path/to/flink-cep-1.0-SNAPSHOT.jar'
output_path='/path/to/result'
resultFile='/path/to/CollectEcho.txt'
data_path2='/path/to/QnV.csv' 
data_path5='/path/to/QnV_R2000070.csv'
data_path6='/path/to/luftdaten_11245.csv'

now=$(date +"%T")
today=$(date +%d.%m.%y)
echo "Current time : $today $now" >>$resultFile
echo "----------$today $now------------" >>$resultFile

for loop in 1 2 3 4 5 6 7; do
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3PatternLS $jar --inputQnV $data_path5 --inputPM $data_path6 --output $output_path --sensors 8 --vel 78 --qua 67 --pmb 64 --tput 35000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3PatternLS run "$loop "--sensors 8:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_PSEQW8L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_PSEQW8L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_PSEQW8T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_PSEQW8T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3QueryLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 8 --vel 78 --qua 67 --pmb 64 --tput 165000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 8:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_QSEQW8L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_QSEQW8L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_QSEQW8T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_QSEQW8T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c Q10_SEQ3Query_IVJ_LS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 8 --vel 78 --qua 67 --pmb 64 --tput 40000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 8:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_QSEQIVJW8L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_QSEQIVJW8L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_QSEQIVJW8T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_QSEQIVJW8T_'$loop'.txt'
    echo "------------ Flink stopped sensors 8 ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3PatternLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 16 --vel 78 --qua 67 --pmb 64 --tput 27500 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3PatternLS run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_PSEQW16L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_PSEQW16L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_PSEQW16T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_PSEQW16T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3QueryLSRO $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 16 --vel 78 --qua 67 --pmb 64 --tput 110000 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_QSEQW16L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_QSEQW16L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_QSEQW16T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_QSEQW16T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3Query_IVJ_LS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 16 --vel 78 --qua 67 --pmb 64 --tput 100000 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_QSEQIVJW16L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_QSEQIVJW16L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_QSEQIVJW16T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_QSEQIVJW16T_'$loop'.txt'
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3Query_IVJ_LSRO $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 16 --vel 78 --qua 67 --pmb 64 --tput 105000 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_QSEQIVJROW16L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_QSEQIVJROW16L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_QSEQIVJROW16T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_QSEQIVJROW16T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3PatternLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 32 --vel 78 --qua 67 --pmb 64 --tput 40000 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3PatternLS run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_PSEQW32L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_PSEQW32L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_PSEQW32T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_PSEQW32T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3QueryLSRO $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 32 --vel 78 --qua 67 --pmb 64 --tput 115000 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_QSEQW32L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_QSEQW32L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_QSEQW32T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_QSEQW32T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3Query_IVJ_LS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 32 --vel 78 --qua 67 --pmb 64 --tput 90000 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_QSEQIVJW32L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_QSEQIVJW32L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_QSEQIVJW32T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_QSEQIVJW32T_'$loop'.txt'
    echo "------------ Flink stopped sensors 32 ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3Query_IVJ_LSRO $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 32 --vel 78 --qua 67 --pmb 64 --tput 115000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_QSEQIVJROW32L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_QSEQIVJROW32L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_QSEQIVJROW32T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_QSEQIVJROW32T_'$loop'.txt'
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3PatternLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 64 --vel 78 --qua 67 --pmb 64 --tput 37500
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3PatternLS run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_PSEQW64L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_PSEQW64L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_PSEQW64T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_PSEQW64T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3QueryLSRO $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 64 --vel 78 --qua 67 --pmb 64 --tput 75000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_QSEQW64L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_QSEQW64L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_QSEQW64T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_QSEQW64T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3Query_IVJ_LS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 64 --vel 78 --qua 67 --pmb 64 --tput 105000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_QSEQIVJW64L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_QSEQIVJW64L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_QSEQIVJW64T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_QSEQIVJW64T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3Query_IVJ_LSRO $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 64 --vel 78 --qua 67 --pmb 64 --tput 105000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_QSEQIVJROW64L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_QSEQIVJROW64L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_QSEQIVJROW64T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_QSEQIVJROW64T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3PatternLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 128 --vel 78 --qua 67 --pmb 64 --tput 30000 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3PatternLS run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_PSEQW128L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_PSEQW128L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_PSEQW128T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_PSEQW128T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3QueryLSRO $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 128 --vel 78 --qua 67 --pmb 64 --tput 105000 #MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_QSEQW128L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_QSEQW128L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_QSEQW128T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_QSEQW128T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3Query_IVJ_LS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 128 --vel 78 --qua 67 --pmb 64 --tput 105000 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_QSEQIVJW128L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_QSEQIVJW128L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_QSEQIVJW128T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_QSEQIVJW128T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3Query_IVJ_LSRO $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 128 --vel 78 --qua 67 --pmb 64 --tput 115000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.out' '/path/to/WExp/FOut_QSEQIVJROW128L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.out' '/path/to/WExp/FOut_QSEQIVJROW128L_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-13.log' '/path/to/WExp/FOut_QSEQIVJROW128T_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-13.log' '/path/to/WExp/FOut_QSEQIVJROW128T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
done
