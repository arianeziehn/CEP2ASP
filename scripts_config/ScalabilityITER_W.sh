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

for loop in 1 2 3 4 5 6 7 8 9 10; do
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERPattern_I2 $jar --input $data_path5 --output $output_path --sensors 128 --tput 75000 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERPattern_I2 run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-48.out' '/path/to/MWITERExp/FOut_PITERW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-48.out' '/path/to/MWITERExp/FOut_PITERW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-48.log' '/path/to/MWITERExp/FOut_PITERW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-48.log' '/path/to/MWITERExp/FOut_PITERW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-49.out' '/path/to/MWITERExp/FOut_PITERW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-49.out' '/path/to/MWITERExp/FOut_PITERW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-49.log' '/path/to/MWITERExp/FOut_PITERW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-49.log' '/path/to/MWITERExp/FOut_PITERW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-46.out' '/path/to/MWITERExp/FOut_PITERW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-46.out' '/path/to/MWITERExp/FOut_PITERW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-46.log' '/path/to/MWITERExp/FOut_PITERW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-46.log' '/path/to/MWITERExp/FOut_PITERW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-45.out' '/path/to/MWITERExp/FOut_PITERW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-45.out' '/path/to/MWITERExp/FOut_PITERW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-45.log' '/path/to/MWITERExp/FOut_PITERW128T4_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-45.log' '/path/to/MWITERExp/FOut_PITERW128T4_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I1_4 $jar --input $data_path5 --output $output_path --sensors 128 --tput 140000 # MST: 140000 (145: 2.3M-2.2M)
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11ITERQuery_I1_4 run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-48.out' '/path/to/MWITERExp/FOut_QITERW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-48.out' '/path/to/MWITERExp/FOut_QITERW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-48.log' '/path/to/MWITERExp/FOut_QITERW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-48.log' '/path/to/MWITERExp/FOut_QITERW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-49.out' '/path/to/MWITERExp/FOut_QITERW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-49.out' '/path/to/MWITERExp/FOut_QITERW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-49.log' '/path/to/MWITERExp/FOut_QITERW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-49.log' '/path/to/MWITERExp/FOut_QITERW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-46.out' '/path/to/MWITERExp/FOut_QITERW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-46.out' '/path/to/MWITERExp/FOut_QITERW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-46.log' '/path/to/MWITERExp/FOut_QITERW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-46.log' '/path/to/MWITERExp/FOut_QITERW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-45.out' '/path/to/MWITERExp/FOut_QITERW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-45.out' '/path/to/MWITERExp/FOut_QITERW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-45.log' '/path/to/MWITERExp/FOut_QITERW128T4_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-45.log' '/path/to/MWITERExp/FOut_QITERW128T4_'$loop'.txt'    
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I1_4_IVJ $jar --input $data_path5 --output $output_path --sensors 128 --tput 325000 # previous 230 ok, 250 ok, 275 ok, 300 ok
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-48.out' '/path/to/MWITERExp/FOut_QITERIVJROW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-48.out' '/path/to/MWITERExp/FOut_QITERIVJROW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-48.log' '/path/to/MWITERExp/FOut_QITERIVJROW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-48.log' '/path/to/MWITERExp/FOut_QITERIVJROW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-49.out' '/path/to/MWITERExp/FOut_QITERIVJROW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-49.out' '/path/to/MWITERExp/FOut_QITERIVJROW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-49.log' '/path/to/MWITERExp/FOut_QITERIVJROW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-49.log' '/path/to/MWITERExp/FOut_QITERIVJROW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-46.out' '/path/to/MWITERExp/FOut_QITERIVJROW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-46.out' '/path/to/MWITERExp/FOut_QITERIVJROW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-46.log' '/path/to/MWITERExp/FOut_QITERIVJROW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-46.log' '/path/to/MWITERExp/FOut_QITERIVJROW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-45.out' '/path/to/MWITERExp/FOut_QITERIVJROW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-45.out' '/path/to/MWITERExp/FOut_QITERIVJROW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-45.log' '/path/to/MWITERExp/FOut_QITERIVJROW128T4_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-45.log' '/path/to/MWITERExp/FOut_QITERIVJROW128T4_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I2 $jar --input $data_path5 --output $output_path --sensors 128 --tput 400000 # previous 350
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-48.out' '/path/to/MWITERExp/FOut_QAGGIVJW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-48.out' '/path/to/MWITERExp/FOut_QAGGIVJW128L1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-48.log' '/path/to/MWITERExp/FOut_QAGGIVJW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-48.log' '/path/to/MWITERExp/FOut_QAGGIVJW128T1_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-49.out' '/path/to/MWITERExp/FOut_QAGGIVJW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-49.out' '/path/to/MWITERExp/FOut_QAGGIVJW128L2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-49.log' '/path/to/MWITERExp/FOut_QAGGIVJW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-49.log' '/path/to/MWITERExp/FOut_QAGGIVJW128T2_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-46.out' '/path/to/MWITERExp/FOut_QAGGIVJW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-46.out' '/path/to/MWITERExp/FOut_QAGGIVJW128L3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-46.log' '/path/to/MWITERExp/FOut_QAGGIVJW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-46.log' '/path/to/MWITERExp/FOut_QAGGIVJW128T3_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-45.out' '/path/to/MWITERExp/FOut_QAGGIVJW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-45.out' '/path/to/MWITERExp/FOut_QAGGIVJW128L4_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-1-node-45.log' '/path/to/MWITERExp/FOut_QAGGIVJW128T4_'$loop'.txt'
    cp '/path/to/flink-1.11.6/log/flink-taskexecutor-0-node-45.log' '/path/to/MWITERExp/FOut_QAGGIVJW128T4_'$loop'.txt'
done
