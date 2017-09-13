#!/bin/bash

../gradlew -q assembleDist

cd build/distributions

tar xf terminal-rss-1.0.tar

cd terminal-rss-1.0/bin

./terminal-rss