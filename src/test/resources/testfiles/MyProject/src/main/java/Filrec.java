import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name="file")
@XmlAccessorType(XmlAccessType.FIELD)
public class Filrec {
    @XmlAttribute
    private String name;
    @XmlAttribute
    private long size;
    @XmlAttribute
    private boolean isDirectory;

    private Map<String, String> customAttributes;

    @XmlElement(name = "file")
    private List<Filrec> filrec;

    public Filrec() {
        customAttributes = new HashMap<>();
    }

    public Filrec(String name, long size, boolean isDirectory) {
        this();
        this.name = name;
        this.size = size;
        this.isDirectory = isDirectory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public List<Filrec> newChildren() {
        filrec = new ArrayList<Filrec>();
        return filrec;
    }

    public List<Filrec> getFilrec() {
        return filrec;
    }

    public void setFilrec(List<Filrec> filrec) {
        this.filrec = filrec;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public void addCustomAttribute(String key, String value) {
        customAttributes.put(key, value);
    }

    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(Map<String, String> customAttributes) {
        this.customAttributes = customAttributes;
    }
}