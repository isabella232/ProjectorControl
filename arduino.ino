#include <AFMotor.h>
#include <Servo.h>
#include <Timers.h>


/* Module Defines */
Servo servo1;
Servo servo2;
AF_Stepper stepMotor(200,2);
#define TIME_INTERVAL      1000
#define HORIZONTAL 170
#define VERTICAL 75
#define FAREST 118
#define NEAREST 77

/* Functional Prototypes */
unsigned char TestForKey(void);
void RespToKey(void);
void RotateMotor(unsigned char);
void StepperMotor(unsigned char);
void StopServo(void);
void StopStepper(void);
void FocusMotor(unsigned char);
void StopFocus(void);
void Far(void);
void Near(void);
void TurnLeft(void);
void TurnRight(void);
void Up(void);
void Down(void);
unsigned char TestTimerExpired(unsigned char);

int servoAngle = HORIZONTAL;
int focusAngle = (FAREST + VERTICAL)/2;
unsigned char turning = 0;
unsigned char rotating = 0;
unsigned char focusing = 0;


void setup() {
  Serial.begin(115200);           // set up Serial library at 9600 bps
  Serial.println("Start!");
  stepMotor.setSpeed(10);
  stepMotor.relefase();
  servo1.attach(9);
  servo1.write(servoAngle);
  servo2.attach(10);
  servo2.write(focusAngle);
  TMRArd_InitTimer(1,1000*TIME_INTERVAL);
  TMRArd_InitTimer(2,1000*TIME_INTERVAL);
   

}

void loop() {
  if (TestForKey()) RespToKey();
  if (turning) StepperMotor(turning);
  if (TestTimerExpired(1) && rotating) RotateMotor(rotating);
  if (TestTimerExpired(2) && focusing) FocusMotor(focusing);
}

unsigned char TestForKey(void) {
  unsigned char KeyEventOccurred;
  
  KeyEventOccurred = Serial.available();
  return KeyEventOccurred;
}

void RespToKey(void) {
  unsigned char theKey;
  
  theKey = Serial.read();
  
switch(theKey){
  case 97: {StopServo(); break; }
  case 98:  {StopStepper(); break; }
  case 99: {StopFocus(); break; }
  case 108: {TurnLeft(); break; }
  case 114: {TurnRight(); break; }
  case 117: {Up(); break;}
  case 100: {Down(); break;}
  case 110: {Near(); break;}
  case 102: {Far(); break;}
  default: { Serial.println("press keys from a b c l r u d n f"); break;}
}
  
    
}

unsigned char TestTimerExpired(unsigned char timer) {
  return (unsigned char)TMRArd_IsTimerExpired(timer);
}


void RotateMotor(unsigned char rotating) {
    if (rotating == 1) {
      TMRArd_ClearTimerExpired(1);
      servo1.write(servoAngle);
      TMRArd_InitTimer(1,(int)(0.01*TIME_INTERVAL));
      if(servoAngle > VERTICAL) {servoAngle--;}
      //Serial.println(servoAngle);
    }else if(rotating == 2) { 
      TMRArd_ClearTimerExpired(1); 
      servo1.write(servoAngle);
      TMRArd_InitTimer(1,(int)(0.01*TIME_INTERVAL));
    if (servoAngle < HORIZONTAL) {servoAngle++;} 
    
    //Serial.println(servoAngle);
    }
}

void FocusMotor(unsigned char focusing) {
    if (focusing == 1) {
      TMRArd_ClearTimerExpired(2);
      servo2.write(focusAngle);
      TMRArd_InitTimer(2,(int)(0.05*TIME_INTERVAL));
      if(focusAngle > NEAREST) {focusAngle--;}
      Serial.println(focusAngle);
    }else if(focusing == 2) { 
      TMRArd_ClearTimerExpired(2); 
      servo2.write(focusAngle);
      TMRArd_InitTimer(2,(int)(0.05*TIME_INTERVAL));
    if (focusAngle < FAREST) {focusAngle++;} 
       Serial.println(focusAngle);

    }
}



void StepperMotor(unsigned char turning) {
    if (turning == 1) {
      stepMotor.step(2, FORWARD, INTERLEAVE);
    }else if (turning == 2) {
     stepMotor.step(2, BACKWARD, INTERLEAVE);
  }
}

void StopServo(void) {
  Serial.println("Stop Servo");
  rotating = 0;
  TMRArd_InitTimer(1, 1000*TIME_INTERVAL);
}

void StopFocus(void) {
  Serial.println("Stop Focus");
  focusing = 0;
  TMRArd_InitTimer(2, 1000*TIME_INTERVAL);
}

void StopStepper(void) {
  Serial.println("Stop Stepper");
  turning = 0;
}



void TurnLeft(void) {
  Serial.println("Turn Left");
  turning = 1;
  
}

void TurnRight(void) {
  Serial.println("Turn Right");
  turning = 2;
}




void Up(void) {
  TMRArd_InitTimer(1, 0.01*TIME_INTERVAL);
  Serial.println("Up");
  rotating = 1;
}

void Down(void) {
  TMRArd_InitTimer(1, 0.01*TIME_INTERVAL);
  Serial.println("Down");
  rotating = 2;
}

void Near(void) {
  TMRArd_InitTimer(2, 0.05*TIME_INTERVAL);
  Serial.println("Near");
  focusing = 2;
}

void Far(void) {
  TMRArd_InitTimer(2, 0.05*TIME_INTERVAL);
  Serial.println("FAR");
  focusing = 1;
}
