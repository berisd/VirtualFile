package at.beris.virtualfile.client;

import at.beris.virtualfile.Attribute;
import com.jcraft.jsch.SftpATTRS;

import java.util.*;

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
    private Map<Integer, Attribute> permissionToAttributeMap;

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
    public Set<Attribute> getAttributes() {
        Set<Attribute> attributeSet = new HashSet<>();
        int permissions = sftpATTRS.getPermissions();

        for (Map.Entry<Integer, Attribute> entry : permissionToAttributeMap.entrySet()) {
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
    public Date getLastModified() {
        return new Date(sftpATTRS.getMTime() * 1000L);
    }

    private HashMap<Integer, Attribute> createPermissionToAttributeMap() {
        HashMap<Integer, Attribute> map = new HashMap<>();
        map.put(S_IRUSR, Attribute.OWNER_READ);
        map.put(S_IWUSR, Attribute.OWNER_WRITE);
        map.put(S_IXUSR, Attribute.OWNER_EXECUTE);
        map.put(S_IRGRP, Attribute.GROUP_READ);
        map.put(S_IWGRP, Attribute.GROUP_WRITE);
        map.put(S_IXGRP, Attribute.GROUP_EXECUTE);
        map.put(S_IROTH, Attribute.OTHERS_READ);
        map.put(S_IWOTH, Attribute.OTHERS_WRITE);
        map.put(S_IXOTH, Attribute.OTHERS_EXECUTE);
        return map;
    }
}
