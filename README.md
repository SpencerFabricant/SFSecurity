SFSecurity
==========
SFSecurity was created by Spencer Fabricant.  It is an application that allows the user to have a room or building monitored in his/her absence.  The software tries to interface with the user's phone (whose IP address is provided by the user) and if it cannot reach the user, it assumes that the user is not present.  If it cannot find the user and it detects motion, it assumes an intruder is present and sends an email notifying the user with two pictures, and then begins taking video footage.


If you are using an iPhone, please update the settings of your phone to check for email at least once every fifteen minutes so that the ping doesn't produce false negatives.

There will later be a .properties file to edit.  In the meantime, to run the program, valid email credentials are required in called ./data/email_creds.  The user's email addresses should also be stored in user_addresses.  PLEASE CHANGE THE EMAIL ADDRESS BEFORE YOU USE THEM.


All third-party libraries are included in third_party.

Spencer Fabricant
Spencer.Fabricant@gmail.com