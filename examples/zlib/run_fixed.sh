#!/bin/sh

python3 compress_decompress.py data/random4.png compressed4.gz decompressed4.png -o $@ method=8
python3 compress_decompress.py data/random1.txt compressed1.gz decompressed1.txt -o $@ method=8


exit 0
