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

for loop in 1 2 3 4 5; do
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3PatternLS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 128 --vel 78 --qua 67 --pmb 64 --tput 30000 #MST without fails
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3PatternLS run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-48.out' '/path/to/MWExp/FOut_PSEQW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-48.out' '/path/to/MWExp/FOut_PSEQW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-48.log' '/path/to/MWExp/FOut_PSEQW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-48.log' '/path/to/MWExp/FOut_PSEQW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-49.out' '/path/to/MWExp/FOut_PSEQW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-49.out' '/path/to/MWExp/FOut_PSEQW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-49.log' '/path/to/MWExp/FOut_PSEQW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-49.log' '/path/to/MWExp/FOut_PSEQW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-46.out' '/path/to/MWExp/FOut_PSEQW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-46.out' '/path/to/MWExp/FOut_PSEQW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-46.log' '/path/to/MWExp/FOut_PSEQW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-46.log' '/path/to/MWExp/FOut_PSEQW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-45.out' '/path/to/MWExp/FOut_PSEQW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-45.out' '/path/to/MWExp/FOut_PSEQW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-45.log' '/path/to/MWExp/FOut_PSEQW128T4_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-45.log' '/path/to/MWExp/FOut_PSEQW128T4_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    flink run -c Q10_SEQ3QueryLSRO $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 128 --vel 78 --qua 67 --pmb 64 --tput 100000 #MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-48.out' '/path/to/MWExp/FOut_QSEQW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-48.out' '/path/to/MWExp/FOut_QSEQW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-48.log' '/path/to/MWExp/FOut_QSEQW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-48.log' '/path/to/MWExp/FOut_QSEQW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-49.out' '/path/to/MWExp/FOut_QSEQW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-49.out' '/path/to/MWExp/FOut_QSEQW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-49.log' '/path/to/MWExp/FOut_QSEQW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-49.log' '/path/to/MWExp/FOut_QSEQW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-46.out' '/path/to/MWExp/FOut_QSEQW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-46.out' '/path/to/MWExp/FOut_QSEQW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-46.log' '/path/to/MWExp/FOut_QSEQW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-46.log' '/path/to/MWExp/FOut_QSEQW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-45.out' '/path/to/MWExp/FOut_QSEQW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-45.out' '/path/to/MWExp/FOut_QSEQW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-45.log' '/path/to/MWExp/FOut_QSEQW128T4_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-45.log' '/path/to/MWExp/FOut_QSEQW128T4_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3Query_IVJ_LSRO $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 128 --vel 78 --qua 67 --pmb 64 --tput 150000 # 150T = MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-48.out' '/path/to/MWExp/FOut_QSEQIVJROW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-48.out' '/path/to/MWExp/FOut_QSEQIVJROW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-48.log' '/path/to/MWExp/FOut_QSEQIVJROW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-48.log' '/path/to/MWExp/FOut_QSEQIVJROW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-49.out' '/path/to/MWExp/FOut_QSEQIVJROW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-49.out' '/path/to/MWExp/FOut_QSEQIVJROW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-49.log' '/path/to/MWExp/FOut_QSEQIVJROW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-49.log' '/path/to/MWExp/FOut_QSEQIVJROW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-46.out' '/path/to/MWExp/FOut_QSEQIVJROW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-46.out' '/path/to/MWExp/FOut_QSEQIVJROW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-46.log' '/path/to/MWExp/FOut_QSEQIVJROW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-46.log' '/path/to/MWExp/FOut_QSEQIVJROW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-45.out' '/path/to/MWExp/FOut_QSEQIVJROW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-45.out' '/path/to/MWExp/FOut_QSEQIVJROW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-45.log' '/path/to/MWExp/FOut_QSEQIVJROW128T4_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-45.log' '/path/to/MWExp/FOut_QSEQIVJROW128T4_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3Query_IVJ_LS $jar --inputQnV $data_path5 --inputPM $data_path6  --output $output_path --sensors 128 --vel 78 --qua 67 --pmb 64 --tput 135000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3Query_IVJ_LS run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-48.out' '/path/to/MWExp/FOut_QSEQIVJW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-48.out' '/path/to/MWExp/FOut_QSEQIVJW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-48.log' '/path/to/MWExp/FOut_QSEQIVJW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-48.log' '/path/to/MWExp/FOut_QSEQIVJW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-49.out' '/path/to/MWExp/FOut_QSEQIVJW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-49.out' '/path/to/MWExp/FOut_QSEQIVJW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-49.log' '/path/to/MWExp/FOut_QSEQIVJW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-49.log' '/path/to/MWExp/FOut_QSEQIVJW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-46.out' '/path/to/MWExp/FOut_QSEQIVJW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-46.out' '/path/to/MWExp/FOut_QSEQIVJW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-46.log' '/path/to/MWExp/FOut_QSEQIVJW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-46.log' '/path/to/MWExp/FOut_QSEQIVJW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-45.out' '/path/to/MWExp/FOut_QSEQIVJW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-45.out' '/path/to/MWExp/FOut_QSEQIVJW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-1-node-45.log' '/path/to/MWExp/FOut_QSEQIVJW128T4_'$loop'.txt'
    cp '/path/to/flink-1.11.6_W/log/flink-taskexecutor-0-node-45.log' '/path/to/MWExp/FOut_QSEQIVJW128T4_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
done
