# VirtualFile
Simple URL based file management

Note on building project:

I'm developing on Linux, sometimes not all tests pass on Windows

## Examples ##

1) Extract local zipfile
```java
IArchive archive = FileManager.newLocalArchive("/home/user/downloads/mytestapp.zip")
IDirectory targetDirectory = FileManager.newLocalDirectory("extracted");
archive.extract(targetDirectory)
```
2) Extract remote zipfile (with sftp protocol)
```java
IArchive archive = FileManager.newArchive("sftp://sshtest:mypwd@www.exmaple.com:22/home/sshtest/mytestapp.zip")
IDirectory targetDirectory = FileManager.newLocalDirectory("extracted");
archive.extract(targetDirectory)
```
3) Find files in a directory (ending with .txt) with a simple filter
```java
IDirectory file = FileManager.newLocalDirectory();
List<IFile> fileList = file.list(new FileNameFilter().endsWith(".txt"));
```

4) Find files in a directory(ending with .txt and greater than 100K) with a combined filter
```java
IDirectory file = FileManager.newLocalDirectory();
List<IFile> fileList = file.list(new FileNameFilter().endsWith(".txt").and(new FileSizeFilter().greaterThan(100*1024L)));
```
