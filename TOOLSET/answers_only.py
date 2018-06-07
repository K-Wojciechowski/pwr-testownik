#!/usr/bin/env python3
import glob
entries = []

for fname in sorted(glob.glob("*.txt")):
    with open(fname, 'r') as fh:
        fdata = fh.read().strip().split('\n')

    qdata = fdata[0].split(';')
    qline = qdata[0]
    entry = [fdata[1]]
    for i in qdata:
        if i.startswith('img='):
            entry[0] += " [obrazek: " + i.split('=')[1] + "]"
            break

    for idx, line in enumerate(fdata):
        if line.strip() and qline[idx] == '1':
            entry.append(line)
    entries.append('\n'.join(entry))

print('\n\n'.join(entries))
