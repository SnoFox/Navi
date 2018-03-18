#!/bin/bash

function trap_signal {
  kill -HUP $!
}

trap "trap_signal" SIGINT SIGTERM SIGHUP

/usr/sbin/runuser -unobody -- java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -jar /root/navi.jar /volumes/config
wait
