#!/usr/bin/env python3
import glob
import re
import sys

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
        fdata[1] = re.sub("\\. ?", ".\t", fdata[1], 1)
    except IndexError:
        print(f"{fname}: WTF")
        print(fdata)
        break
    if fdata[2].startswith(('A) ', 'a) ', ' - ')):
        trim = 3
    elif fdata[2].startswith('- '):
        trim = 2
    elif NO_INDICATORS:
        trim = 0
    else:
        print(f"{fname}: unknown file format")
        break
    for i in range(2, len(fdata)):
        let = chr(0x5f + i)  # 0x61 - 2
        fdata[i] = f'\t({let}) ' + fdata[i][trim:]

    fdata.append('')
    with open(fname, 'w') as fh:
        fh.write('\n'.join(fdata))
