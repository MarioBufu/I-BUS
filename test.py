import dbus, dbus.mainloop.glib, sys
import subprocess
 
def on_property_changed(interface, changed, invalidated):
    if interface != 'org.bluez.MediaPlayer1':
        return
    for prop, value in changed.iteritems():
        if prop == 'Status':
            print('Playback Status: {}'.format(value))
        elif prop == 'Track':
            print('Music Info:')
            for key in ('Title', 'Artist', 'Album'):
                print('   {}: {}'.format(key, value.get(key, '')))
 
def on_playback_control(command):
    
    if command.startswith('play'):
        player_iface.Play()
    elif command.startswith('pause'):
        player_iface.Pause()
    elif command.startswith('next'):
        player_iface.Next()
    elif command.startswith('prev'):
        player_iface.Previous()
    return True
 def setupBluetoothConnection():
  returnOutput = subprocess.check_output("sudo rm /var/run/bluealsa/*", shell = True)
  returnOutput = subprocess.check_output("bluealsa-aplay 00:00:00:00:00:00", shell = True)
if __name__ == '__main__':
    dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)
    bus = dbus.SystemBus()
    obj = bus.get_object('org.bluez', "/")
    mgr = dbus.Interface(obj, 'org.freedesktop.DBus.ObjectManager')
    for path, ifaces in mgr.GetManagedObjects().iteritems():
        adapter = ifaces.get('org.bluez.MediaPlayer1')
        print str(ifaces.get('org.bluez.MediaPlayer1')) + "next :\n"
        if not adapter:
            continue
        player = bus.get_object('org.bluez',path)
        player_iface = dbus.Interface(
                player,
                dbus_interface='org.bluez.MediaPlayer1')
        break
        
    if not adapter:
        sys.exit('Error: Media Player not found.')
 
    bus.add_signal_receiver(
            on_property_changed,
            bus_name='org.bluez',
            signal_name='PropertiesChanged',
            dbus_interface='org.freedesktop.DBus.Properties')
	on_playback_control("pause")
