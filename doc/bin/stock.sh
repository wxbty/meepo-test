#!/bin/sh
# ----------------------------------------------------------------------------
#  Copyright 2001-2006 The Apache Software Foundation.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
# ----------------------------------------------------------------------------

#   Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
#   reserved.

BASEDIR=`dirname $0`/..
BASEDIR=`(cd "$BASEDIR"; pwd)`



# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
darwin=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true
           if [ -z "$JAVA_VERSION" ] ; then
             JAVA_VERSION="CurrentJDK"
           else
             echo "Using Java version: $JAVA_VERSION"
           fi
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/${JAVA_VERSION}/Home
           fi
           ;;
esac

if [ -z "$JAVA_HOME" ] ; then
  if [ -r /etc/gentoo-release ] ; then
    JAVA_HOME=`java-config --jre-home`
  fi
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# If a specific java binary isn't specified search for the standard 'java' binary
if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java`
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo "  We cannot execute $JAVACMD"
  exit 1
fi

if [ -z "$REPO" ]
then
  REPO="$BASEDIR"/lib
fi

CLASSPATH=$CLASSPATH_PREFIX:"$BASEDIR"/conf:"$REPO"/jta-supports-dubbo-2.0.jar:"$REPO"/jta-supports-2.0.jar:"$REPO"/jta-core-2.0.jar:"$REPO"/javax.jms-api-2.0.jar:"$REPO"/javax.resource-api-1.7.jar:"$REPO"/commons-lang3-3.4.jar:"$REPO"/hessian-4.0.38.jar:"$REPO"/kryo-3.0.3.jar:"$REPO"/reflectasm-1.10.1.jar:"$REPO"/minlog-1.3.0.jar:"$REPO"/fst-2.56.jar:"$REPO"/jackson-core-2.8.8.jar:"$REPO"/javax.annotation-api-1.2.jar:"$REPO"/javax.inject-1.jar:"$REPO"/jsqlparser-1.2.jar:"$REPO"/fastjson-1.2.28.jar:"$REPO"/cglib-2.2.2.jar:"$REPO"/asm-3.3.1.jar:"$REPO"/aspectjweaver-1.6.8.jar:"$REPO"/druid-1.0.17.jar:"$REPO"/logback-classic-1.2.3.jar:"$REPO"/logback-core-1.2.3.jar:"$REPO"/slf4j-api-1.7.25.jar:"$REPO"/commons-dbcp2-2.1.1.jar:"$REPO"/commons-pool2-2.4.2.jar:"$REPO"/curator-recipes-2.11.0.jar:"$REPO"/curator-framework-2.11.0.jar:"$REPO"/curator-client-2.11.0.jar:"$REPO"/mybatis-3.2.3.jar:"$REPO"/mybatis-spring-1.2.1.jar:"$REPO"/spring-context-4.2.4.RELEASE.jar:"$REPO"/spring-aop-4.2.4.RELEASE.jar:"$REPO"/aopalliance-1.0.jar:"$REPO"/spring-beans-4.2.4.RELEASE.jar:"$REPO"/spring-core-4.2.4.RELEASE.jar:"$REPO"/spring-expression-4.2.4.RELEASE.jar:"$REPO"/spring-jdbc-4.2.4.RELEASE.jar:"$REPO"/spring-tx-4.2.4.RELEASE.jar:"$REPO"/guava-18.0.jar:"$REPO"/commons-lang-2.6.jar:"$REPO"/dubbo-2.6.2.jar:"$REPO"/javassist-3.20.0-GA.jar:"$REPO"/netty-3.2.5.Final.jar:"$REPO"/netty-all-4.1.0.Final.jar:"$REPO"/mysql-connector-java-5.1.38.jar:"$REPO"/httpclient-4.5.2.jar:"$REPO"/httpcore-4.4.4.jar:"$REPO"/commons-logging-1.2.jar:"$REPO"/commons-codec-1.9.jar:"$REPO"/javax.transaction-api-1.2.jar:"$REPO"/zkclient-0.10.jar:"$REPO"/zookeeper-3.4.8.jar:"$REPO"/log4j-1.2.16.jar:"$REPO"/jline-0.9.94.jar:"$REPO"/netty-3.7.0.Final.jar:"$REPO"/junit-4.12.jar:"$REPO"/sample-txc-dubbo-0.2.1-SNAPSHOT.jar
EXTRA_JVM_ARGUMENTS=""

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
  [ -n "$HOME" ] && HOME=`cygpath --path --windows "$HOME"`
  [ -n "$BASEDIR" ] && BASEDIR=`cygpath --path --windows "$BASEDIR"`
  [ -n "$REPO" ] && REPO=`cygpath --path --windows "$REPO"`
fi

exec "$JAVACMD" $JAVA_OPTS \
  $EXTRA_JVM_ARGUMENTS \
  -classpath "$CLASSPATH" \
  -Dapp.name="stock" \
  -Dapp.pid="$$" \
  -Dapp.repo="$REPO" \
  -Dbasedir="$BASEDIR" \
  com.tuya.txc.dubbo.StockServiceImpl \
  "$@"
