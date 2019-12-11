# file: comm.py

import threading
try:
   import queue
except ImportError:
   import Queue as queue
from bluetooth import *
import serial
import sys

server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

handle = serial.Serial(
        port='/dev/ttyUSB0', # this is our USB IBUS adapter 
        baudrate=9600, 
        parity=serial.PARITY_EVEN, 
        timeout=1, 
        stopbits=1
    )

dataBuffer = queue.Queue()

def readBtData():
    print("read bt data thread")
    client_sock, client_info = server_sock.accept()
    print("Accepted connection from ", client_info)
    try:
        while True:
            data = client_sock.recv(1024)
            if len(data) == 0: continue
            print("received [%s]" % data)
            if data == "exit":
                print("Am iesit")
                dataBuffer.put(data)
                client_sock.close()
                return
            elif (data.startswith("hex: ")):
                print data[5:]
                dataBuffer.put(data[5:])
    except IOError:
        pass
        client_sock.close()
    print("disconnected")
    readBtData()
def au8getChecksum(datareceived):
 checksum = 0
 index = 0
 for index in datareceived:
  checksum ^= index
 #print("checksum type")
 #print(type(checksum))
 return checksum
def writeIbusData():
    au8data = []
    print("write ibus data thread")
    while True:
        if(not dataBuffer.empty()):
            data = dataBuffer.get()
            print(data)
            if data == "exit" :  break
            #print("Received data: "+data)
            au8data = bytearray.fromhex(data)#data.decode('hex')
            #print(au8data)
            #print(type(au8data))
            #print(type(data.decode('hex')))
            #print(type(augetChecksum(au8data)))
            #print(au8getChecksum(au8data))
            data += format(au8getChecksum(au8data), '02x')
            #print ("Data to send " + data)
            handle.write(data.decode('hex'))
            

def consume_bus():
    return 0 #handle.read(1024)
def parse(data):
    packets = []
    bus_dump = bytearray(data)
    packet = bytearray()

    def is_packet_complete():
        # packet must have source, length, destination
        if len(packet) < 3:
            return False

        length = packet[1]
        entire_length = length + 2  # the source_id and length bytes
        if len(packet) == entire_length:
            return True

        return False
#    print("Buss = ",bus_dump)

    # process each byte that was received
    for index, byte in enumerate(bus_dump):
        packet.append(byte)

        if is_packet_complete():
            packets.append(packet)
            packet = bytearray()  # reset packet
    
    return packets

bluetoothThread = threading.Thread(target = readBtData, args = ())
ibusThread = threading.Thread(target = writeIbusData, args = ())

if(__name__ == "__main__"):
    #start listen for devices
    advertise_service( server_sock, "SampleServer",
                   service_id = uuid,
                   service_classes = [ uuid, SERIAL_PORT_CLASS ],
                   profiles = [ SERIAL_PORT_PROFILE ])
                   
    print("Waiting for connection on RFCOMM channel %d" % port)
    
    bluetoothThread.start()
    ibusThread.start()
    
    #import binascii
    #while 1:
        #data = consume_bus()
        #l = parse(data)     
        #for array in l:
            #if (testx != binascii.hexlify(array))and(testy != binascii.hexlify(array)):
            #for x in array:
            #print (hex(x) for x in array)
            #print(binascii.hexlify(array))
    
    #wait for threads to finish
    bluetoothThread.join()
    print("bt thread finished")
    ibusThread.join()
    print("ibus therad finished")
    
    #close the socket
    server_sock.close()
    #print the queue at exit
    while(not dataBuffer.empty()):
        print(dataBuffer.get())
    print("all done")
