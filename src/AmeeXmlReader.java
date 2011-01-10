import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;


public class AmeeXmlReader {
	
	private final XPath xpath;
	
	public AmeeXmlReader() throws Exception {
		super();
		
		XPathFactory factory = XPathFactory.newInstance();
		this.xpath = factory.newXPath();
		this.xpath.setNamespaceContext(new AmeeXmlNamespace());
	}
	
	public String read(Document doc, String query) throws Exception {
		
		XPathExpression expr = xpath.compile(query);
		return (String) expr.evaluate(doc, XPathConstants.STRING);
	}
}
