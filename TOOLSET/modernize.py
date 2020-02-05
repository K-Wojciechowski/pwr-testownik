#!/usr/bin/env python3
import glob
import re
import sys

AVAILABLE_INDICATORS = ('A) ', 'a) ', '(a) ', ' - ', '- ', 'a. ')
NO_INDICATORS = '--no-indicators' in sys.argv

for fname in glob.glob("*.txt"):
    try:
        with open(fname, 'r') as fh:
            fdata = fh.read().strip().split('\n')
    except UnicodeDecodeError:
        with open(fname, 'r', encoding='windows-1250') as fh:
            fdata = fh.read().strip().split('\n')
    if fdata[0].startswith('QQ'):
        print(f"{fname}: already modern")
        continue

    fdata[0] = fdata[0].replace('X', 'QQ')
    try:
        # fdata[1] = re.sub("\\. ?", ".\t", fdata[1], 1)
        fdata[1] = re.sub("\\. +", ".\t", fdata[1], 1)
    except IndexError:
        print(f"{fname}: WTF")
        print(fdata)
        break
    trim = None
    if NO_INDICATORS:
        trim = 0

    if trim is None:
        for ind in AVAILABLE_INDICATORS:
            if fdata[2].startswith(ind):
                trim = len(ind)
                break

    if trim is None:
        try:
            for ind in AVAILABLE_INDICATORS:
                if fdata[2].strip().startswith(ind):
                    trim = fdata[2].index(ind)
                    part = fdata[2][trim:]
                    trim += part.index(part.strip())
                    break
        except ValueError:
            pass


    if trim is None:
        print(f"{fname}: unknown file format")
        break
    for i in range(2, len(fdata)):
        let = chr(0x5f + i)  # 0x61 - 2
        fdata[i] = f'\t({let}) ' + fdata[i][trim:]

    fdata.append('')
    with open(fname, 'w') as fh:
        fh.write('\n'.join(fdata))
