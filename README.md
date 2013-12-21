
Simpella
=======================================================

A peer-to-peer application with file share, search and download capabilities. It is based on Gnutella protocol version 0.6.
The language used is java.

1) What our program does ?
   - Listens to incoming tcp connections from other servents
   - user (one who executes this program) can connect to other servents using TCP, can query the application
     to enumerate the existing outgoing/incoming connections 
   - user can search the Simpella network for files
   - user can query the information of the host(the one on which the application is being executed)
   - user can query the simpella network statistics like
       1) No of bytes received and sent
       2) Messages received and sent
       3) Queries received and sent
   
      Note : The messages sent/received can be viewed for all the connections or individually
    - user can download files from other servents
   - 
2) The commands implemented
     - info 
         all the options specified were implemented       
    - share
    - scan
    - open
    - update
    - find
    - clear
    - list
    - download
    - autoconnect

Note : 1) An extra command "autoconnect" is implemented. This command forces the servent to search for new Simpella hosts in the 
          network and to establish a simpella connection to them. 
       2) Command has to be invoked explicitly by the user.
       3) After invocation of this command makes sure the servent has two outgoing connections.
        


3) Instructions to compile
    - make all 
    - make clean : removes all the class files
   Instructions to execute
    - java Simpella <simpella_port> <download_port>

4) Java version 1.7.0_o9
   Tested on "timberlake.cse.buffalo.edu" 

     HarishKumar Gudelly  harishku@buffalo.edu
     Ravikanth Reddy G rgudipat@buffalo.edu
     
    
Feel free to contact us with questions.
