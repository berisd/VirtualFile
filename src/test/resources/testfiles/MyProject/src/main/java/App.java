import org.apache.commons.jxpath.JXPathContext;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;

public class App {

    public App() {
//        marshal();

        Filrec filrec = unmarshal();



//        JXPathContext context = JXPathContext.newContext(filrec);
        // context.getValue("filrec[2]/name")


    }

    private Filrec unmarshal() {
        try {
            File file = new File("file.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Filrec.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Filrec filerec = (Filrec) jaxbUnmarshaller.unmarshal(file);
            System.out.println(filerec.getFilrec().get(3).getFilrec().get(0).getName());
            System.out.println(filerec.getFilrec().get(0).getCustomAttributes().getClass());
            System.out.println(filerec.getFilrec().get(0).getCustomAttributes().get("year"));
            return filerec;

        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void marshal() {
        try {
            Filrec filerec1 = createFileRec();

            File file = new File("file.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Filrec.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(filerec1, file);
            jaxbMarshaller.marshal(filerec1, System.out);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private Filrec createFileRec() {
        Filrec filerec1 = new Filrec("/", 0, true);
        List<Filrec> children1 = filerec1.newChildren();

        Filrec filerec11 = new Filrec("exec.bat", 299466, false);
        filerec11.addCustomAttribute("checksum", "AF67FE12AB");
        filerec11.addCustomAttribute("year", "1992");
        children1.add(filerec11);
        Filrec filerec12 = new Filrec("command.com", 64535, false);
        children1.add(filerec12);
        Filrec filerec13 = new Filrec("wordreport_201456.doc", 512677, false);
        children1.add(filerec13);

        Filrec filerec14 = new Filrec("backup", 0, true);
        children1.add(filerec14);

        List<Filrec> children14 = filerec14.newChildren();
        children14.add(new Filrec("cafe_melange.exe", 1234561, false));
        return filerec1;
    }

    public static void main(String[] args) {
        new App();
    }

}
