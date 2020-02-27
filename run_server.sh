#!/bin/bash
#project name
source /etc/profile
PATTERN="mix-recall-server-1.0-SNAPSHOT.jar"
root_path="/data/prod/service/mix-recall/"

PID=""

function find_pid(){
    PID=`ps aux |grep ${PATTERN} |grep java |grep -v grep | awk '{print $2}'`
    echo "pid: "$PID
}


function stop(){
    for i in {1..5}
    do
        find_pid
        if [  "${PID}" = "" ]; then
            echo "$PATTERN is not running."
            break
        else
            echo ${PID}
            echo "Try kill pid:${PID%/*} times $var"
            kill ${PID%/*}
            sleep 10
        fi
    done
}

function stop_kill(){
    for i in {1..5}
    do
        find_pid
        if [  "${PID}" = "" ]; then
            echo "$PATTERN is not running."
            break
        else
            echo ${PID}
            echo "Try kill -9 pid:${PID%/*} times $var"
            kill -9 ${PID%/*}
            sleep 10
        fi
    done
}

function start() {
    path=$root_path"bin/mix-recall-server-1.0-SNAPSHOT.jar"
    currentDate=`date "+%Y%m%d%H%M"`
    gc_path=$root_path"logs/gc.log"
    old_gc_path=$root_path"logs/gc.log."$currentDate
    mv $gc_path $old_gc_path
    nohup java -server -Denv=pro -Xms100g -Xmx100g -XX:+IgnoreUnrecognizedVMOptions -XX:+UnlockDiagnosticVMOptions -XX:-UseBiasedLocking -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=24 -XX:ConcGCThreads=6 -XX:+UseG1GC -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -XX:InitiatingHeapOccupancyPercent=30 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime -Xloggc:$gc_path -jar $path > /dev/null  2>&1 &
}
echo "----shutdown----"
stop
echo "----stop_kill----"
stop_kill
sleep 25
echo "----start----"
start
echo "new pid "$!
