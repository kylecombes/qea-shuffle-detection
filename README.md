# QEA/SmartStep Shuffle Detector

This Android app was developed as part of [a project](http://smartstep.xyz) for [Olin College's](http://www.olin.edu) Quantitative Engineering Analysis II class.
It is a rough first-pass attempt at detecting shuffling feat using a phone's accelerometer. The app was hacked together in an afternoon, and
thus the code is not beautiful. There also seems to be a problem with the peak detection not detecting the full peak. It does work most of the time, however.

## Project website
For more details on the project, as well as info on the project it is meant to accompany, visit [smartstep.xyz](http://smartstep.xyz).

## Steps to use
1. Strap the device to your leg with the device's y-axis aligned with your leg (this most likely means the phone is in the portrait orientation on your leg).
2. Launch the app
3. Tap Calibrate
4. Walk normally for 5 seconds (while the screen is yellow)
5. Shuffle for 5 seconds (while the screen is blue)
6. Press Monitor Walking and begin walking around

The screen should be green unless the phone detects you shuffling, in which case it will vibrate incessantly and turn the background red.
