# ProjectorControl

## To run the program
use the ssh command to connect your PC with Raspberry Pi:

- ssh 10.0.3.160 -l pi 



The password is peel1234

After log into the Pi, you will see

pi@picontroller ~ $ 

Type
- sudo su //log into the root environment
- cd
- python controller.py //run the python script on the Pi 
 
Now you can control the platform from your phone app
 
 
##To remote control the desktop of the Pi

After remote connect to the Pi through ssh, type

- vncserver :1

Download and install VNCViewer app

https://www.realvnc.com/download/viewer/

Start VNCViewer in the Applications on your Mac/PC,

VNC Server:  10.0.3.160:1 //ip address of the Pi 

Click Connect, and type the password: peel1234

You write the python script and move it to the root of the Pi by typing
- sudo cp Desktop/controller.py /root/







