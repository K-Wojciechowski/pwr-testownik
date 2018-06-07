#!/usr/bin/env python3
import glob
import re
import sys
NO_NUMBERS = '--no-numbers' in sys.argv
for fname in glob.glob("*.txt"):
    with open(fname, 'r') as fh:
        fdata = fh.read().strip().split('\n')

    num = int(fname[:-4])

    if NO_NUMBERS:
        fdata[1] = f"{num}.\t" + fdata[1]
    else:
        fdata[1] = re.sub(".*?\\.\t", f"{num}.\t", fdata[1], 1)

    fdata.append('')
    with open(fname, 'w') as fh:
        fh.write('\n'.join(fdata))
