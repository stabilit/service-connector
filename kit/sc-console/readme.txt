Service Connector Console

A service connector running on <host> and listening to <port> is required!

usage: java -jar scconsole.jar -h <host> -p <port> <enable|disable|show|sessions=service>

enable - enables the service
disable - disables the service
state - shows the service state enabled or disabled
sessions - shows number of available and allocated sessions 

examples: java -jar scconsole.jar -h localhost -p 8000 enable=abc
          java -jar scconsole.jar -h localhost -p 8000 disable=abc
          java -jar scconsole.jar -h localhost -p 8000 show=abc
          java -jar scconsole.jar -h localhost -p 8000 sessions=abc
         

