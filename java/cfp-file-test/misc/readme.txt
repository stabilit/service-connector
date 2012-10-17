Readme File Service Manager based on SC

argument 1: host to connect (127.1.1.1)<br />
argument 2: port to connect (e.g. 7000)<br />
argument 3: connection type (netty.tcp / netty.http)<br />
argument 4: service name (file-1)
argument 5: function to call (list, upload, download)
optional argument 6: file path of the uploading/downloading file
optional argument 7: name on server of the uploading/downloading file
optional argument 8: maximum allowed operation time in seconds

@throws Exception
		Several processing errors.
@throws UnexpectedException
		Wrong number of arguments.

e.g. programm execution:
java -jar cfpFileService.jar localhost 9000 netty.tcp file-1 list
java -jar cfpFileService.jar localhost 9000 netty.tcp file-1 upload send.txt sent.txt 3600
java -jar cfpFileService.jar localhost 9000 netty.tcp file-1 download testRec.tar.gz eclipse-jee-juno.tar.gz 3600

Pitfalls
PHP Limits - php.ini
;;;;;;;;;;;;;;;;;;;
; Resource Limits ;
;;;;;;;;;;;;;;;;;;;

; Maximum execution time of each script, in seconds
; http://php.net/max-execution-time
; Note: This directive is hardcoded to 0 for the CLI SAPI
max_execution_time = 90