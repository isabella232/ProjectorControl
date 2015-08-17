import serial
import time
from bottle import route,post,view,get,run,request,put,delete,response
ser = serial.Serial('/dev/ttyACM0', 115200)
ser.open()
print ser.name
time.sleep(3)
print ser.readline();



@get("/up")
def up():
	print "sending 'up' message to arduino"
	ser.write('u');
	print ser.readline();
	print "sent 'up' message to arduino"

@get("/near")
def near():
	print "sending 'near' message to arduino"
	ser.write('n');
	print ser.readline();
	print "sent 'near' message to arduino"

@get("/far")
def far():
	print "sending 'far' message to arduino"
	ser.write('f');
	print ser.readline();
	print "sent 'far' message to arduino"

@get("/stopfocus")
def stopfocus():
	print "sending 'stop focus' message to arduino"
	ser.write('c');
	print ser.readline();
	print "sent 'stop focus' message to arduino"


@get("/down")
def down():
	print "sending 'down' message to arduino"
	ser.write('d');
	print ser.readline();
	print "sent 'down' message to arduino"

@get("/right")
def right():
	print "sending 'turn right' message to arduino"
	ser.write('r');
	print ser.readline();
	print "sent 'turn right' message to arduino"

@get("/left")
def left():
	print "sending 'turn left' message to arduino"
	ser.write('l');
	print ser.readline();
	print "sent 'turn left' message to arduino"


@get("/stopservo")
def stopservo():
	print "sending 'stop servo' message to arduino"
	ser.write('a');
	print ser.readline();
	print "sent 'stop servo' message to arduino"


@get("/stopstepper")
def stopstepper():
	print "sending 'stop stepper' message to arduino"
	ser.write('b');
	print ser.readline();
	print "sent 'stop stepper' message to arduino"


run(host='0.0.0.0', port= '8080')

ser.close()
