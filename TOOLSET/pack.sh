#!/bin/bash
find "$@" -name ".DS_Store" -print -delete
find "$@" -name "._*" -print -delete
rm "$@.zip"
zip -r "$@.zip" "$@"
