#!/bin/bash

ROOT_DIR=${HOME}"/termina_rss"
PERSISTENCE_DIR=${ROOT_DIR}"/data"

while [[ $# -gt 1 ]]; do
    key="$1"

    case $key in
        -p|--persistence)
        PERSISTENCE_DIR="$2"
        shift
        ;;
        *)

        ;;
    esac
    shift
done

./gradlew -q assembleDist

cd build/distributions

tar xf terminal-rss-1.0.tar

cd terminal-rss-1.0/bin

./terminal-rss ${PERSISTENCE_DIR}