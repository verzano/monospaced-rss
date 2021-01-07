#!/bin/bash

# TODO usage/help
# TODO some output for the user
# App properties
ROOT_DIR=${HOME}"/.terminal_rss"
PERSISTENCE_DIR=${ROOT_DIR}"/data"

# TODO better app name/version vals
# TODO include the logging.properties in the build
APP_VERSION="monospaced-rss"
DISTRIBUTIONS_DIR="build/distributions"

# TODO use getopts and drop the longform
while [[ $# -gt 1 ]]; do
    key="$1"

    case ${key} in
        -p|--persistence)
        PERSISTENCE_DIR="$2"
        shift
        ;;
        *)

        ;;
    esac
    shift
done

mkdir -p ${PERSISTENCE_DIR}

./gradlew -q assembleDist

# TODO different output dir
tar xf ${DISTRIBUTIONS_DIR}/${APP_VERSION}.tar -C ${DISTRIBUTIONS_DIR}\

cd ${DISTRIBUTIONS_DIR}/${APP_VERSION}

# TODO do this in the build.gradle.kts file
mkdir -p log

./bin/monospaced-rss ${PERSISTENCE_DIR}