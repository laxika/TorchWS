package io.torch.file;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * A mime detection class until we drop support for Java7. (It's addresses the
 * mime detection problems on mac and linux under Java7.)
 */
public class MimeTypeDetector {

    private HashMap<String, String> mimes = new HashMap<>();

    public MimeTypeDetector() {
        try {
            File file = new File("config/mimes.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList mimeList = doc.getElementsByTagName("mime");

            for (int temp = 0; temp < mimeList.getLength(); temp++) {
                Node nNode = mimeList.item(temp);
                Element eElement = (Element) nNode;

                mimes.put(eElement.getElementsByTagName("extension").item(0).getTextContent(), eElement.getElementsByTagName("type").item(0).getTextContent());
            }
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(MimeTypeDetector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getMimeByExtension(String extension) {
        String mime = mimes.get(extension);

        return mime == null ? "application/text" : mime;
    }
}
