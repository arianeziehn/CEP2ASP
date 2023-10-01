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
    echo "------------ Flink stopped sensors 8 ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERPattern_I2 $jar --input $data_path5 --output $output_path --sensors 16  --tput 110000 # MST: 110000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERPattern_I2 run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_PITER16L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_PITER16L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_PITER16T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_PITER16T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I1_4 $jar --input $data_path5 --output $output_path --sensors 16 --tput 145000 # MST 145k
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11ITERQuery_I1_4 run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_QITER16L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_QITER16L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_QITER16T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_QITER16T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I1_4_IVJ $jar --input $data_path5   --output $output_path --sensors 16  --tput 225000 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_QITERIVJ16L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_QITERIVJ16L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_QITERIVJ16T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_QITERIVJ16T_'$loop'.txt'
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I2 $jar --input $data_path5 --output $output_path --sensors 16 --tput 250000 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I2 run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_QITERAGG16L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_QITERAGG16L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_QITERAGG16T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_QITERAGG16T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERPattern_I2 $jar --input $data_path5   --output $output_path --sensors 32  --tput 75000 #MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERPattern_I2 run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_PITER32L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_PITER32L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_PITER32T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_PITER32T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I1_4 $jar --input $data_path5 --output $output_path --sensors 32  --tput 150000 #
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11ITERQuery_I1_4 run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_QITER32L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_QITER32L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_QITER32T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_QITER32T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I1_4_IVJ $jar --input $data_path5 --output $output_path --sensors 32  --tput 300000 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_QITERIVJ32L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_QITERIVJ32L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_QITERIVJ32T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_QITERIVJ32T_'$loop'.txt'
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I2 $jar --input $data_path5 --output $output_path --sensors 32 --tput 325000 # 325 MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_QITERAGG32L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_QITERAGG32L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_QITERAGG32T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_QITERAGG32T_'$loop'.txt'
    echo "------------ Flink stopped sensors 32 ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERPattern_I2 $jar --input $data_path5 --output $output_path --sensors 64 --tput 90000 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERPattern_I2 run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_PITER64L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_PITER64L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_PITER64T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_PITER64T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I1_4 $jar --input $data_path5 --output $output_path --sensors 64 --tput 90000 #
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11ITERQuery_I1_4 run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_QITER64L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_QITER64L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_QITER64T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_QITER64T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I1_4_IVJ $jar --input $data_path5 --output $output_path --sensors 64 --tput 275000 # MST
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_QITERIVJ64L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_QITERIVJ64L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_QITERIVJ64T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_QITERIVJ64T_'$loop'.txt'
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I2 $jar --input $data_path5 --output $output_path --sensors 64 --tput 350000 #
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_QITERAGG64L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_QITERAGG64L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_QITERAGG64T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_QITERAGG64T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
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
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_PITER128L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_PITER128L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_PITER128T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_PITER128T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I1_4 $jar --input $data_path5 --output $output_path --sensors 128 --tput 140000 # MST: 140000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11ITERQuery_I1_4 run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_QITER128L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_QITER128L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_QITER128T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_QITER128T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I1_4_IVJ $jar --input $data_path5 --output $output_path --sensors 128 --tput 325000 #
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_QITERIVJ128L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_QITERIVJ128L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_QITERIVJ128T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_QITERIVJ128T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I2 $jar --input $data_path5 --output $output_path --sensors 128 --tput 400000 #
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.out' '/path/toITERExp/FOut_QITERAGG128L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.out' '/path/toITERExp/FOut_QITERAGG128L_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-1-node-1.log' '/path/toITERExp/FOut_QITERAGG128T_'$loop'.txt'
    cp '/path/toflink-1.11.6/log/flink-taskexecutor-0-node-1.log' '/path/toITERExp/FOut_QITERAGG128T_'$loop'.txt'
done
