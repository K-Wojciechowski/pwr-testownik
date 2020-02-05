#!/usr/bin/env python3
# ~/Library/Android/sdk/platform-tools/adb backup -noapk com.shadowtesseract.politests
# dd if=backup.ab bs=24 skip=1|openssl zlib -d > mybackup.tar
# (openssl zlib works on Linux only,  alternative:)
# dd if=backup.ab bs=1 skip=24 | python3 -c "import zlib,sys;sys.stdout.buffer.write(zlib.decompress(sys.stdin.buffer.read()))" | tar -xvf -
# JSON files are somewhere in apps/com.shadowtesseract.politests/f/Testownikdata
# Images are somewhere in     apps/com.shadowtesseract.politests/f/Testownik/photosdata
import json
import sys
with open(sys.argv[1]) as fh:
    data = json.load(fh)

for question in data['listOfQuestions']:
    modeline = 'QQ'
    for ans in question['right_answers']:
        modeline += '1' if ans == 'true' else '0'
    if question['has_photo'] == 'true':
        modeline += ';img={}.jpg'.format(question["number"])
    qline = "{}.\t{}".format(question["number"], question["question"].replace("\n", " "))
    anslines = ['\t(' + a for a in question["answers"]]
    fname = question['id'] or "{:03}.txt".format(question["number"])
    with open(fname, "w") as fh:
        fh.write(modeline + '\n')
        fh.write(qline + '\n')
        fh.write('\n'.join(anslines))
