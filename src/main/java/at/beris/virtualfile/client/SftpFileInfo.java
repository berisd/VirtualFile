package at.beris.virtualfile.client;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.UnixGroupPrincipal;
import at.beris.virtualfile.UnixUserPrincipal;
import at.beris.virtualfile.attribute.IAttribute;
import com.jcraft.jsch.SftpATTRS;

import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SftpFileInfo implements IFileInfo {

    private SftpATTRS sftpATTRS;
    private String path;

    public void setSftpATTRS(SftpATTRS sftpATTRS) {
        this.sftpATTRS = sftpATTRS;
    }

    public String getPath() {
        return this.path;
    }

    @Override
    public void fillModel(FileModel model) {
        model.setSize(sftpATTRS.getSize());
        model.setCreationTime(null);
        model.setLastModifiedTime(FileTime.fromMillis(sftpATTRS.getMTime() * 1000L));
        model.setLastAccessTime(FileTime.fromMillis(sftpATTRS.getATime() * 1000L));
        model.setAttributes(createAttributes());
        model.setOwner(new UnixUserPrincipal(sftpATTRS.getUId(), sftpATTRS.getGId()));
        model.setGroup(new UnixGroupPrincipal(sftpATTRS.getGId()));
    }

    public void setPath(String path) {
        this.path = path;
    }

    private Set<IAttribute> createAttributes() {
        Set<IAttribute> attributeSet = new HashSet<>();
        int permissions = sftpATTRS.getPermissions();

        for (Map.Entry<Integer, IAttribute> entry : Sftp.permissionToAttributeMap.entrySet()) {
            if ((permissions & entry.getKey()) != 0) {
                attributeSet.add(entry.getValue());
            }
        }

        return attributeSet;
    }
}
