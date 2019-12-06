#!/bin/bash

# Originally written by Ralf Kistner <ralf@embarkmobile.com>, but placed in the public domain

#set -x
set +e

bootanim=""
failcounter=0
#timeout_in_sec=360 # 6 minutes
timeout_in_sec=600 # 10 minutes

until [[ "$bootanim" =~ "stopped" ]]; do
  bootanim=`adb -e shell getprop init.svc.bootanim 2>&1 &`
#echo bootanim=\`$bootanim\`
  if [[ "$bootanim" =~ "device not found" || "$bootanim" =~ "device offline"
    || "$bootanim" =~ "running" || "$bootanim" =~  "error: no emulators found" ]]; then
    let "failcounter += 1"
    echo "Waiting for emulator to start: $failcounter of $timeout_in_sec : status: $bootanim"
    if [[ $failcounter -ge timeout_in_sec ]]; then
      echo "Timeout ($timeout_in_sec seconds) reached; failed to start emulator"
      exit 1
    fi
  fi
  sleep 1
done

echo "Emulator is ready"