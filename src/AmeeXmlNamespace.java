import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;


public class AmeeXmlNamespace implements NamespaceContext {

	public String getNamespaceURI(String prefix) {
		if (prefix == null) throw new NullPointerException("Null prefix");
		else if ("amee".equals(prefix)) return "http://schemas.amee.cc/2.0";
		else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
		return XMLConstants.NULL_NS_URI;
	}
	
	public String getPrefix(String uri) {
		throw new UnsupportedOperationException();
	}
	
	public Iterator<String> getPrefixes(String uri) {
		throw new UnsupportedOperationException();
	}
}
