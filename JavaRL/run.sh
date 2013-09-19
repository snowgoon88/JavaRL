#! /bin/zsh
#
# usage is './run.sh mainClass (parameters...)'

LOCAL_CP="bin"
LIB_JAVA="/usr/share/java"
LIB_LORIA="/users/maia/dutech/lib"
LIB_CP="${LIB_JAVA}/jama.jar:${LIB_JAVA}/vecmath.jar:${LIB_JAVA}/args4j.jar:${LIB_LORIA}/apache-log4j-2.0-beta8-bin/log4j-api-2.0-beta8.jar:${LIB_LORIA}/apache-log4j-2.0-beta8-bin/log4j-core-2.0-beta8.jar"
PROJ_PATH="/home/dutech/Projets/GIT"
PROJ_CP="${PROJ_PATH}/JavaUtils/JavaUtils/bin:${PROJ_PATH}/HumanArm/HumanArm/bin"

## echo ${LOCAL_CP}:${LIB_CP}:${PROJ_CP}

echo "java -Dlog4j.configurationFile=log/log4j2.xml -cp ${LOCAL_CP}:${LIB_CP}:${PROJ_CP} $argv[1] $argv[2,-1]"
java -Dlog4j.configurationFile=log/log4j2.xml -cp ${LOCAL_CP}:${LIB_CP}:${PROJ_CP} $argv[@]



