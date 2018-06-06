#!/bin/bash

ROOT_DIR=${HOME}"/.terminal_rss"
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

mkdir -p ${PERSISTENCE_DIR}