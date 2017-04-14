#!/usr/bin/env bash

buildGTK() {
   #cinterop -def ./gtk.def -pkg gtk -o gtk.kt.bc || exit 1
   #echo
   test
}

buildTime() {
   cinterop -def ./time.def -pkg time -o time.kt.bc || exit 1
   echo
}


if [ -a gtk.kt.bc ]; then
   read -p "Rebuild GTK+ library (y/n)? " choice
   case "$choice" in
     y|Y ) buildGTK ;;
     * ) echo "Skipping GTK+ rebuild" ;;
   esac
else
   buildGTK
fi

if [ -a time.kt.bc ]; then
   read -p "Rebuild Time library (y/n)? " choice
   case "$choice" in
     y|Y ) buildTime ;;
     * ) echo "Skipping Time rebuild" ;;
   esac
   echo
else
   buildTime
fi

echo "Building src/ and including libraries"
kotlinc-native src/ -library time.kt.bc -o out/test -opt || exit 1

echo
read -p "Done building. Run (y/n)? " choice
case "$choice" in
   y|Y)
   echo "Running"
   echo
   ./out/test
   ;;
   * ) echo "Not running compiled program. Exiting"
esac
