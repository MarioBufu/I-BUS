https://medium.com/design-and-tech-co/connecting-a-bmw-to-the-internet-part-two-1ee2ea44d4a2

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
