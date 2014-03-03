#!/bin/bash

set -u
set -e

if [ ! -d .ssh ]; then
  echo "Downloading ssh.tgz, unpacking to .ssh/ ..."
  curl --silent --location ${KBILLING_SSH_TGZ:-"https://s3.amazonaws.com/kbilling/staging-QjP6jiri/ssh.tgz"} | tar xz
  echo "... done."
fi

exec java -javaagent:newrelic/newrelic.jar $JAVA_OPTS -Dhttp.port=${PORT} -cp "target/universal/stage/lib/*" play.core.server.NettyServer
