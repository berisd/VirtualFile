# VirtualFile
Userfriendly URL based file and archive management library for Java 8.<br/>
Supported protocols: local, http, https, ftp, sftp.<br/>
Supported archives: zip, tar, 7zip.<br/>
### Required libraries ###
Apache Commons Lang v3.4 (String and Array functions)<br/>

### Optional libraries ###
Apache Commons Compress v1.12 (Archivers and Compression)<br/>
Apache Commons Net v3.4 (FTP support)<br/>
Apache Tika Core v1.14 (Content Type detection)<br/>
Apache Tika Parsers v1.14 (Content Encoding detection)<br/>
JSch - Java Secure Channel v0.1.53 (SFTP support)<br/>
Simple Logging Facade for Java API v1.7.12 (Logging)<br/>
XZ Utils v1.5 (LZMA Compression)<br/>

## Building the project ##
VirtualFile is built using Apache Maven.<br/>
Change to the VirtualFile directory (containing the pom.xml) and type:<br/>
```mvn clean install```<br/>

You can generate the Java API docs with:<br/>
```mvn javadoc:jar```<br/>

Note on building project:<br/>
I'm developing on Linux, sometimes not all tests pass on Windows.<br/>
The switch ```-Drunintegrationtests=true``` will run IntegrationTests which reqire a special setup that currently only exists on my PC.

## Examples ##

*) Extract local zip file
```java
VirtualFileManager fileManager = VirtualFileManager.createManager()
VirtualArchive archive = fileManager.resolveLocalFile("/home/user/downloads/mytestapp.zip").asArchive();
VirtualFile targetDirectory = fileManager.resolveLocalDirectory("extracted");
archive.extract(targetDirectory)
```
*) Extract local 7zip file
```java
VirtualFileManager fileManager = VirtualFileManager.createManager()
VirtualArchive archive = fileManager.resolveLocalArchive("/home/user/downloads/coolstuff.7z");
VirtualFile directory = fileManager.resolveLocalDirectory("extracted");
archive.extract(targetDirectory)
```
*) Extract remote zipfile (with sftp protocol)
```java
VirtualFileManager fileManager = VirtualFileManager.createManager()
VirtualArchive archive = fileManager.resolveArchive("sftp://sshtest:mypwd@www.exmaple.com:22/home/sshtest/mytestapp.zip")
VirtualFile targetDirectory = fileManager.resolveLocalDirectory("extracted");
archive.extract(targetDirectory)
```
*) Find files in a directory (ending with .txt) with a simple filter
```java
VirtualFileManager fileManager = VirtualFileManager.createManager()
VirtualFile file = fileManager.resolveLocalDirectory("documents");
List<VirtualFile> fileList = file.list(new FileNameFilter().endsWith(".txt"));
```

*) Find files in a directory(ending with .txt and greater than 100K) with a combined filter
```java
VirtualFileManager fileManager = VirtualFileManager.createManager()
VirtualFile file = fileManager.resolveLocalDirectory("documents");
List<VirtualFile> fileList = file.list(new FileNameFilter().endsWith(".txt").and(new FileSizeFilter().greaterThan(100*1024L)));
```

*) Transfer a file with the sftp protocol and public key authentication (stricthostchecking is on by default, so there must be an entry for the host in the known_hosts file (Under Linux that's usually ~/.ssh/known_hosts). You can set the location of the known_hosts file with FileConfig.setKnownHostsFile().
```java
VirtualFileManager fileManager = VirtualFileManager.createManager()
fileManager.getConfiguration().setAuthenticationType(AuthenticationType.PUBLIC_KEY)
  .setPrivateKeyFile("/home/myuser/.ssh/id_dsa");
VirtualFile file = fileManager.resolveFile("sftp://myuser:mypassword@www.example.com:22/home/myuser/mydocuments.zip");
file.copy(FileMananger.newLocalDirectory("."));
```

*) Transfer a file with the sftp protocol without stricthostchecking and password authentication.
```java
VirtualFileManager fileManager = VirtualFileManager.createManager()
fileManager.getConfiguration().setStrictHostKeyChecking(false);
VirtualFile file = fileManager.resolveFile("sftp://myuser:mypassword@www.example.com:22/home/myuser/mydocuments.zip", configurator);
file.copy(fileManager.resolveLocalDirectory("."));
```

## Shell ##

You can play around with the shell to see the VirtualFile library in action.
Run the class at.beris.virtualfile.shell.Shell to start it. type "help" for Help.

e.g. Those commands will connect to a remote site, download a file, delete the downloaded file and quit the shell

* con ftp://lanet.lv
* ls
* cd pub
* get idx.html
* lls
* lrm idx.html
* quit
