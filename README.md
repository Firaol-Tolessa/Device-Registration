
# Futronic device registration

The device management software was developed to aid the processes of registering the already existing devices into the system. The software uses a GUI interface for registration and a MYSQL database for storage of device id and the device's public key.

The software will store an RSA key pair of MOSIP public key + device private key in the devices EEPROM, while also storing the devices serial number + device public key in a table in the database.

