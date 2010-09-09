Stabilit Service Connector Console

usage    : java -jar scconsole.jar -filename <properties file> <enable|disable|show=service>

samples: java -jar scconsole.jar -filename sc.properties enable=abc
         java -jar scconsole.jar disable=abc
         java -jar scconsole.jar show=abc
         
- the scconsole.jar file is located in the target folder
- a working sc service is required
- this console tool reads only the first found sc service in the properties file.

