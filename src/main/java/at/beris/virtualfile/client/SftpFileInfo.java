package at.beris.virtualfile.client;

import at.beris.virtualfile.attribute.IAttribute;
import at.beris.virtualfile.attribute.PosixFilePermission;
import com.jcraft.jsch.SftpATTRS;

import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SftpFileInfo implements IFileInfo {
    static final int S_IRUSR = 00400; // read by owner
    static final int S_IWUSR = 00200; // write by owner
    static final int S_IXUSR = 00100; // execute/search by owner

    static final int S_IRGRP = 00040; // read by group
    static final int S_IWGRP = 00020; // write by group
    static final int S_IXGRP = 00010; // execute/search by group

    static final int S_IROTH = 00004; // read by others
    static final int S_IWOTH = 00002; // write by others
    static final int S_IXOTH = 00001; // execute/search by others

    private SftpATTRS sftpATTRS;
    private String path;
    private Map<Integer, IAttribute> permissionToAttributeMap;

    public SftpFileInfo() {
        permissionToAttributeMap = createPermissionToAttributeMap();
    }

    public void setSftpATTRS(SftpATTRS sftpATTRS) {
        this.sftpATTRS = sftpATTRS;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public long getSize() {
        return sftpATTRS.getSize();
    }

    @Override
    public boolean isDirectory() {
        return sftpATTRS.isDir();
    }

    @Override
    public Set<IAttribute> getAttributes() {
        Set<IAttribute> attributeSet = new HashSet<>();
        int permissions = sftpATTRS.getPermissions();

        for (Map.Entry<Integer, IAttribute> entry : permissionToAttributeMap.entrySet()) {
            if ((permissions & entry.getKey()) != 0) {
                attributeSet.add(entry.getValue());
            }
        }

        return attributeSet;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public FileTime getLastModified() {
        return FileTime.fromMillis(sftpATTRS.getMTime() * 1000L);
    }

    private HashMap<Integer, IAttribute> createPermissionToAttributeMap() {
        HashMap<Integer, IAttribute> map = new HashMap<>();
        map.put(S_IRUSR, PosixFilePermission.OWNER_READ);
        map.put(S_IWUSR, PosixFilePermission.OWNER_WRITE);
        map.put(S_IXUSR, PosixFilePermission.OWNER_EXECUTE);
        map.put(S_IRGRP, PosixFilePermission.GROUP_READ);
        map.put(S_IWGRP, PosixFilePermission.GROUP_WRITE);
        map.put(S_IXGRP, PosixFilePermission.GROUP_EXECUTE);
        map.put(S_IROTH, PosixFilePermission.OTHERS_READ);
        map.put(S_IWOTH, PosixFilePermission.OTHERS_WRITE);
        map.put(S_IXOTH, PosixFilePermission.OTHERS_EXECUTE);
        return map;
    }
}
