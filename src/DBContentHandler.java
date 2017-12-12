
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class DBContentHandler implements ContentHandler {

    private String currentValue;

    @Override
    // Nichts
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    // Nichts
    public void startDocument() throws SAXException {
    }

    @Override
    // Nichts
    public void endDocument() throws SAXException {
    }

    @Override
    // Nichts
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    @Override
    // Nichts
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    // Nichts
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    }

    @Override
    // Nichts
    public void endElement(String uri, String localName, String qName) throws SAXException {
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        currentValue = new String(ch, start, length);
    }

    @Override
    // Nichts
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override
    // Nichts
    public void skippedEntity(String name) throws SAXException {
    }

}
