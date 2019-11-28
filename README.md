Thread skeleton
```python
import queue,threading,time

q = queue.Queue()

# q can be passed as argument
def getBtData():
  print("BT thread started")
  for i in range(10):
    q.put_nowait("mesg from BT")
  time.sleep(2)
  print("BT thread finished")

# q can be passed as argument
def sendIBUSCommand():
  print("IBUS thread started")
  while(True):
    print(q.get())
    time.sleep(1)
    if(q.empty()):
      break
  print("IBUS thread finished")

if __name__ == "__main__":
  # q can be passed as argument
  t1 = threading.Thread(target = getBtData, args = ())
  # q can be passed as argument
  t2 = threading.Thread(target = sendIBUSCommand, args = ())
  print("Starting threads")
  t1.start()
  t2.start()
  print("Waiting for threads to finish")
  t1.join()
  t2.join()
  print("Main finished")
```


https://medium.com/design-and-tech-co/connecting-a-bmw-to-the-internet-part-two-1ee2ea44d4a2

```python
from btpycom import *

def onStateChanged(state, msg):
    if state == "LISTENING":
        print "Server is listening"
    elif state == "CONNECTED":
        print "Connection established to", msg
    elif state == "MESSAGE":
    print "Got message", msg
    
serviceName = "EchoServer"
server = BTServer(serviceName, stateChanged = onStateChanged)
```

### E46 k-bus

###### k-bus modules and connections           
* A1 - General Module
* A2 - Instrument Cluster Control Unit
* A3 - Light Switching Centre Control Unit
* A11 - Heating and A/C Control Unit
* A12 - Multiple Restraint System Control Unit
* A21 - Driver's Seat Memory Control Unit
* A33 - Sunroof Module Control Unit
* A81 - Park Distance Control (PDC)
* A85 - Tire Pressure Control System Control Unit
* A169 - Switch Center
* A212 - Mirror Memory Control Unit Driver's Side
* A213 - Mirror Memory Control Unit Front Passenger Side
* A417 - Tire Defect Indicator Control Unit (RPA)
* A836 - Electronic Immobilizer Control Unit
* B57 - Rain Sensor
* IO1002 - Volute Spring (steering wheel controls)
* N9 - Radio Control Unit (head unit)
* N22 - CD Changer

![Diagram](/K-bus_diagram.PNG)

**Pin assignments at plug connector X300 (PDC - trunk)**

1. E Signal, switch off consumer load Closed-circuit current cutout relay
2. A PDC loudspeaker activation Parking aid speaker
3. Not used
4. **E/A I/K-bus signal link Connector, I/K-bus**
5. Not used
6. M Terminal 31 Ground point
7. Not used
8. M Ground for PDC loudspeaker Parking aid speaker
9. Not used
10. Not used
11. A Reversing light signal Gong, Japan
12. Not used

**Connector X10116 (above glove box - gateway for K-bus)** white, red, yellow wires for the K-bus
