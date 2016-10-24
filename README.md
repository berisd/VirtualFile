# VirtualFile
Simple URL based file management library.<br/>
Supported file systems: local, ftp, sftp.
Supported archives: zip, tar, 7zip.

Note on building project:<br/>
I'm developing on Linux, sometimes not all tests pass on Windows.

## Examples ##

*) Extract local zip file
```java
VirtualArchive archive = FileManager.newLocalFile("/home/user/downloads/mytestapp.zip").asArchive();
VirtualFile targetDirectory = FileManager.newLocalDirectory("extracted");
archive.extract(targetDirectory)
```
*) Extract local 7zip file
```java
VirtualArchive archive = FileManager.newLocalArchive("/home/user/downloads/coolstuff.7z");
VirtualFile directory = FileManager.newLocalDirectory("extracted");
archive.extract(targetDirectory)
```
*) Extract remote zipfile (with sftp protocol)
```java
VirtualArchive archive = FileManager.newArchive("sftp://sshtest:mypwd@www.exmaple.com:22/home/sshtest/mytestapp.zip")
VirtualFile targetDirectory = FileManager.newLocalDirectory("extracted");
archive.extract(targetDirectory)
```
*) Find files in a directory (ending with .txt) with a simple filter
```java
VirtualFile file = FileManager.newLocalDirectory("documents");
List<VirtualFile> fileList = file.list(new FileNameFilter().endsWith(".txt"));
```

*) Find files in a directory(ending with .txt and greater than 100K) with a combined filter
```java
VirtualFile file = FileManager.newLocalDirectory("documents");
List<VirtualFile> fileList = file.list(new FileNameFilter().endsWith(".txt").and(new FileSizeFilter().greaterThan(100*1024L)));
```

*) Transfer a file with the sftp protocol and public key authentication (stricthostchecking is on by default, so there must be an entry for the host in the known_hosts file (Under Linux that's usually ~/.ssh/known_hosts). You can set the location of the known_hosts file with FileConfig.setKnownHostsFile().
```java
FileManager.getConfiguration().setAuthenticationType(AuthenticationType.PUBLIC_KEY)
  .setPrivateKeyFile("/home/myuser/.ssh/id_dsa");
VirtualFile file = FileManager.newFile("sftp://myuser:mypassword@www.example.com:22/home/myuser/mydocuments.zip");
file.copy(FileMananger.newLocalDirectory("."));
```

*) Transfer a file with the sftp protocol without stricthostchecking and password authentication.
```java
FileManager.getConfiguration().setStrictHostKeyChecking(false);
VirtualFile file = FileManager.newFile("sftp://myuser:mypassword@www.example.com:22/home/myuser/mydocuments.zip", configurator);
file.copy(FileMananger.newLocalDirectory("."));
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
