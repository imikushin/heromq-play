#!/bin/bash

set -u
set -e

exec java -javaagent:newrelic/newrelic.jar $JAVA_OPTS -Dhttp.port=${PORT} -cp "target/universal/stage/lib/*" play.core.server.NettyServer
