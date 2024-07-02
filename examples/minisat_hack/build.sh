#!/bin/bash

SHDIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
if [ -z "$SHDIR" ]; then SHDIR="."; fi

cd $SHDIR

cd sources/simp
make clean

make rs V=1
chmod +x minisat_HACK_999ED_CSSC_static
cp -f minisat_HACK_999ED_CSSC_static ../../

