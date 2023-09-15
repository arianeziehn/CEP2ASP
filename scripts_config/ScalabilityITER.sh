#!/usr/bin/env bash
startflink='/home/ziehn-ldap/flink-1.11.6/bin/start-cluster.sh'
stopflink='/home/ziehn-ldap/flink-1.11.6/bin/stop-cluster.sh'
flink='/home/ziehn-ldap/flink-1.11.6/bin/flink'
jar='/home/ziehn-ldap/flink-cep-1.0-SNAPSHOT.jar'
output_path='/home/ziehn-ldap/result1'
resultFile='/home/ziehn-ldap/CollectEchoScalITER.txt'
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
    $flink run -p 8 -c Q11_ITERPattern_I2 $jar --input $data_path5 --output $output_path --sensors 8 --tput 100000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERPattern_I2 run "$loop "--sensors 8:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_PITER8L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_PITER8L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_PITER8T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_PITER8T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c Q11_ITERQuery_I1_4 $jar --input $data_path5 --output $output_path --sensors 8 --tput 165000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 8:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITER8L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITER8L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITER8T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITER8T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c Q11_ITERQuery_I1_4_IVJ $jar --input $data_path5 --output $output_path --sensors 8 --tput 75000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 8:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ8L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ8L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ8T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ8T_'$loop'.txt'
    echo "------------ Flink stopped sensors 8 ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -p 8 -c Q11_ITERQuery_I2 $jar --input $data_path5 --output $output_path --sensors 8 --tput 165000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 8:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG8L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG8L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG8T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG8T_'$loop'.txt'
    echo "------------ Flink stopped sensors 8 ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERPattern_I2 $jar --input $data_path5   --output $output_path --sensors 16  --tput 35000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERPattern_I2 run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_PITER16L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_PITER16L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_PITER16T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_PITER16T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3QueryLS $jar --input $data_path5   --output $output_path --sensors 16  --tput 75000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITER16L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITER16L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITER16T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITER16T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I1_4_IVJ $jar --input $data_path5   --output $output_path --sensors 16  --tput 75000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ16L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ16L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ16T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ16T_'$loop'.txt'
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I2 $jar --input $data_path5 --output $output_path --sensors 16 --tput 165000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 16:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG16L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG16L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG16T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG16T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERPattern_I2 $jar --input $data_path5   --output $output_path --sensors 32  --tput 40000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERPattern_I2 run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_PITER32L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_PITER32L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_PITER32T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_PITER32T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3QueryLS $jar --input $data_path5   --output $output_path --sensors 32  --tput 75000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITER32L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITER32L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITER32T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITER32T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I1_4_IVJ $jar --input $data_path5 --output $output_path --sensors 32  --tput 135000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ32L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ32L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ32T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ32T_'$loop'.txt'
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I2 $jar --input $data_path5 --output $output_path --sensors 32 --tput 165000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 32:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG32L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG32L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG32T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG32T_'$loop'.txt'
    echo "------------ Flink stopped sensors 32 ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERPattern_I2 $jar --input $data_path5 --output $output_path --sensors 64 --tput 40000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERPattern_I2 run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_PITER64L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_PITER64L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_PITER64T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_PITER64T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3QueryLS $jar --input $data_path5 --output $output_path --sensors 64 --tput 75000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITER64L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITER64L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITER64T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITER64T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I1_4_IVJ $jar --input $data_path5 --output $output_path --sensors 64 --tput 130000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ64L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ64L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ64T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ64T_'$loop'.txt'
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I2 $jar --input $data_path5 --output $output_path --sensors 64 --tput 165000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 64:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG64L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG64L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG64T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG64T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERPattern_I2 $jar --input $data_path5 --output $output_path --sensors 128 --tput 40000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERPattern_I2 run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_PITER128L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_PITER128L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_PITER128T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_PITER128T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q10_SEQ3QueryLS $jar --input $data_path5 --output $output_path --sensors 128 --tput 75000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q10_SEQ3QueryLS run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITER128L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITER128L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITER128T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITER128T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I1_4_IVJ $jar --input $data_path5 --output $output_path --sensors 128 --tput 130000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ128L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ128L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ128T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERIVJ128T_'$loop'.txt'
    echo "------------ Flink stopped ------------" >>$resultFile
    now=$(date +"%T")
    today=$(date +%d.%m.%y)
    echo "Current time : $today $now" >>$resultFile
    echo "Flink start" >>$resultFile
    $startflink
    START=$(date +%s)
    $flink run -c Q11_ITERQuery_I2 $jar --input $data_path5 --output $output_path --sensors 128 --tput 165000
    END=$(date +%s)
    DIFF=$((END - START))
    echo "Q11_ITERQuery_I1_4_IVJ run "$loop "--sensors 128:"$DIFF"s" >>$resultFile
    $stopflink
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG128L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.out' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG128L_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-1-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG128T_'$loop'.txt'
    cp '/home/ziehn-ldap/flink-1.11.6/log/''flink-ziehn-ldap -taskexecutor-0-sr630-wn-a-21.log' '/home/ziehn-ldap/ITERExp/FOut_QITERAGG128T_'$loop'.txt'
done
