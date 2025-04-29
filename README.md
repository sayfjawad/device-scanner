# device-scanner
Scan WiFi and BlueTooth devices nearby 

## requirements
* Linux device (raspberry pi for example)
* Root permissions
* WIFI-device that has monitoring capabilities (example: TP-Link TL-WN823N v2/v3 [Realtek RTL8192EU])
* aircrack-ng 

## setup
* install aircrack-ng
```shell
sudo apt update
sudo apt install aircrack-ng
```
* configure your wifi-adapter to monitor mode
```shell
sudo ifconfig wlan0 down
sudo iwconfig wlan0 mode monitor
sudo ifconfig wlan0 up
```
* verify it is working
```shell
iwconfig
```

## troubleshoot
if you encounter the following error then you probably need a usb-wifi adapter with monitor support because your current wifi-device does not support monitor mode
```shell

pi3:/opt $ sudo iwconfig wlan0 mode monitor
Error for wireless request "Set Mode" (8B06) :
    SET failed on device wlan0 ; Operation not supported.
```