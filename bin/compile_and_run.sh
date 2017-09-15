#!/bin/bash

while [[ $# -gt 1 ]]; do
    key="$1"

    case $key in
        -p|--persistence)
        PERSISTENCE="$2"
        shift # past argument
        ;;
        *)
                # unknown option
        ;;
    esac
    shift # past argument or value
done

./gradlew -q assembleDist

cd build/distributions

tar xf terminal-rss-1.0.tar

cd terminal-rss-1.0/bin

./terminal-rss ${PERSISTENCE}