# VirtualFile
Simple URL based file management

Note on building project:

I'm developing on Linux, sometimes not all tests pass on Windows

## Examples ##

1) Extract local zipfile
```java
Archive archive = FileManager.newLocalArchive("/home/user/downloads/mytestapp.zip")
Directory targetDirectory = FileManager.newLocalDirectory("extracted");
archive.extract(targetDirectory)
```
2) Extract remote zipfile (with sftp protocol)
```java
Archive archive = FileManager.newArchive("sftp://sshtest:mypwd@www.exmaple.com:22/home/sshtest/mytestapp.zip")
Directory targetDirectory = FileManager.newLocalDirectory("extracted");
archive.extract(targetDirectory)
```
3) Find files in a directory (ending with .txt) with a simple filter
```java
Directory file = FileManager.newLocalDirectory("documents");
List<IFile> fileList = file.list(new FileNameFilter().endsWith(".txt"));
```

4) Find files in a directory(ending with .txt and greater than 100K) with a combined filter
```java
Directory file = FileManager.newLocalDirectory("documents");
List<IFile> fileList = file.list(new FileNameFilter().endsWith(".txt").and(new FileSizeFilter().greaterThan(100*1024L)));
```

5) Transfer a file with the sftp protocol and public key authentication (stricthostchecking is on by default, so there must be an entry for the host in the known_hosts file (Under Linux that's usually ~/.ssh/known_hosts). You can set the location of the known_hosts file with FileConfig.setKnownHostsFile().
```java
FileConfig config = new FileConfig().setClientAuthenticationType(AuthenticationType.PUBLIC_KEY)
  .setPrivateKeyFile("/home/myuser/.ssh/id_dsa");
File file = FileManager.newFile("sftp://myuser:mypassword@www.example.com:22/home/myuser/mydocuments.zip", config);
file.copy(FileMananger.newLocalDirectory("."));
```

6) Transfer a file with the sftp protocol without stricthostchecking and password authentication.
```java
FileConfig config = new FileConfig().setClientStrictHostKeyChecking(false);
File file = FileManager.newFile("sftp://myuser:mypassword@www.example.com:22/home/myuser/mydocuments.zip", config);
file.copy(FileMananger.newLocalDirectory("."));
```
