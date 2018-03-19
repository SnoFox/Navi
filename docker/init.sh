#!/bin/bash

function trap_signal {
  kill -HUP $!
}

trap "trap_signal" SIGINT SIGTERM SIGHUP

/usr/sbin/runuser -unavi -- java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -jar /opt/navi.jar /volumes/config
wait
