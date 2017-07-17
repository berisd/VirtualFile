# VirtualFile
Userfriendly URL based file and archive management library for Java 8.<br/>
Supported protocols: local, http, https, ftp, sftp.<br/>
Supported archives: zip, tar, 7zip.<br/>
### Required libraries ###
Simple Logging Facade for Java API v1.7.12 (Logging)<br/>

### Optional libraries ###
Apache Commons Compress v1.12 (Archivers and Compression)<br/>
Apache Commons Net v3.4 (FTP support)<br/>
JSch - Java Secure Channel v0.1.53 (SFTP support)<br/>
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

*) Create a VirtualFileManager instance. This is the entry point to the library functions. You can change the Default Configuration with setter methods.
```java
VirtualFileManager fileManager = VirtualFileManager.createManager();
fileManager.setAuthenticationType(AuthenticationType.PUBLIC_KEY);
fileManager.setTimeout(60);

// To change the default configuration for a specififc protocol
fileManager.getClientDefaultConfigurationSftp().setPort(1212);
```

*) Create a VirtualFileManager instance. You can pass a Configuration instance to the createManager() method. Here the default home and masterpassword is set. All data is saved to the home directory (e.g. the configuration, keystore data and sites). The masterpassword is used to protect all sensitive data.
```java
Configuration configuration = Configuration.create("/home/test/newvirtualfilehome")
        .setMasterPassword(new char[] {'p', 'w', 'd'})
        .setTimeout(60)
        .setAuthenticationType(AuthenticationType.PUBLIC_KEY);

VirtualFileManager fileManager = VirtualFileManager.createManager(configuration);
```

*) Transfer a file with the sftp protocol and public key authentication (stricthostchecking is on by default, so there must be an entry for the host in the known_hosts file (Under Linux that's usually ~/.ssh/known_hosts). You can set the location of the known_hosts file with FileConfig.setKnownHostsFile().
```java
VirtualFileManager fileManager = VirtualFileManager.createManager()
fileManager.setAuthenticationType(AuthenticationType.PUBLIC_KEY)
  .setPrivateKeyFile("/home/myuser/.ssh/id_dsa");
VirtualFile file = fileManager.resolveFile("sftp://myuser:mypassword@www.example.com:22/home/myuser/mydocuments.zip");
file.copy(fileManager.newLocalDirectory("."));
```

*) Transfer a file with the sftp protocol without stricthostchecking and password authentication.
```java
VirtualFileManager fileManager = VirtualFileManager.createManager();
fileManager.setStrictHostKeyChecking(false);
VirtualFile file = fileManager.resolveFile("sftp://myuser:mypassword@www.example.com:22/home/myuser/mydocuments.zip");
file.copy(fileManager.resolveLocalDirectory("."));
```

*) Transfer a directory with the sftp protocol using a site. A site defines a place in a network and how to connect there (take for example a website http://www.example.com; you connect there with the http protocol on port 80 (default) to the host www.example.com). Sites can be saved with fileManager.save() and will be available next time you create a filemanager instance.
```java
Site site = Site.create().setHostname("www.beris.at").setProtocol(Protocol.SFTP).setUsername("sshtest")
        .setPassword(TestHelper.readSftpPassword().toCharArray());

VirtualFileManager fileManager = VirtualFileManager.createManager();
fileManager.addSite(site);

VirtualFile localdirectory = fileManager.resolveLocalDirectory("mybackup");
fileManager.resolveDirectory(site, "backup").copy(localdirectory);
```

*) Test file attributes (local and remote)
```java
import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.VirtualFileManager;
import at.beris.virtualfile.attribute.PosixFilePermission;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    @Test
    public void testFileAttributes() {
        VirtualFileManager fileManager = VirtualFileManager.createManager();

        VirtualFile file = fileManager.resolveLocalFile("/home/bernd/Downloads/ideaIC-2017.1.2.tar.gz");
        assertThat(file.getAttributes()).contains(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OTHERS_READ);
        assertThat(file.getSize()).isEqualTo(427932328);
        assertThat(file.getContentType().toString()).isEqualTo("application/gzip");
        assertThat(file.getContentEncoding()).isEqualTo("ISO-8859-1");
        assertThat(file.getLastModifiedTime().toString()).isEqualTo("2017-05-30T16:49:54Z");
        assertThat(file.getOwner().getName()).isEqualTo("bernd");
        assertThat(file.getGroup().getName()).isEqualTo("adm");
        assertThat(file.getName()).isEqualTo("ideaIC-2017.1.2.tar.gz");
        assertThat(file.getPath()).isEqualTo("/home/bernd/Downloads/ideaIC-2017.1.2.tar.gz");

        file = fileManager.resolveFile("https://en.wikipedia.org/wiki/Main_Page");
        assertThat(file.getSize()).isEqualTo(73089);
        assertThat(file.getContentType().toString()).isEqualTo("text/html");
        assertThat(file.getContentEncoding()).isEqualTo("ISO-8859-1");
        assertThat(file.getLastModifiedTime().toString()).isEqualTo("2017-05-30T16:46:43Z");
    }
}
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
