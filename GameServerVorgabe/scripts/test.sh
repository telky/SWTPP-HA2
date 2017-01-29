#!/bin/bash
./ant de.tuberlin.sese.swtpp.gameserver.test.lasca
if [ $? != 0 ]
then
 exit $?
fi
