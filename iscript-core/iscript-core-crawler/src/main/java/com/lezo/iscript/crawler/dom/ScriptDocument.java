package com.lezo.iscript.crawler.dom;

import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.parser.Tag;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

//HTMLDocument,DocumentTraversal, DocumentEvent, DocumentRange, DocumentView
public class ScriptDocument extends ScriptElement implements HTMLDocument {
	private ConcurrentHashMap<String, Node> identifiers = new ConcurrentHashMap<String, Node>();
	protected String documentURI;

	public ScriptDocument(org.jsoup.nodes.Document targetDocument) {
		super(targetDocument, null);
		setOwnerDocument(this);
	}

	@Override
	public DocumentType getDoctype() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DOMImplementation getImplementation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element getDocumentElement() {
		return this;
	}

	@Override
	public Element createElement(String tagName) throws DOMException {
		org.jsoup.nodes.Element targetEle = new org.jsoup.nodes.Element(Tag.valueOf(tagName), getBaseURI());
		return createElement(targetEle);
	}

	public ScriptElement createElement(org.jsoup.nodes.Node target) throws DOMException {
		ScriptElement element = new ScriptElement(target, this);
		element.setOwnerDocument(this);
		return element;
	}

	@Override
	public DocumentFragment createDocumentFragment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Text createTextNode(String data) {
		ScriptText scriptText = new ScriptText(data, getBaseURI());
		scriptText.setOwnerDocument(this);
		return scriptText;
	}

	@Override
	public Comment createComment(String data) {
		return new ScriptComment(data, getBaseURI());
	}

	@Override
	public CDATASection createCDATASection(String data) throws DOMException {
		return new ScriptCDATASection(data, getBaseURI());
	}

	@Override
	public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attr createAttribute(String name) throws DOMException {
		return new ScriptAttr(name, "");

	}

	@Override
	public EntityReference createEntityReference(String name) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node importNode(Node source, boolean deep) throws DOMException {
		int type = source.getNodeType();
		Node destNode = source;
		switch (type) {
		case ELEMENT_NODE: {
			Element newElement = createElement(source.getNodeName());
			newElement.setNodeValue(source.getNodeValue());
			NamedNodeMap nnm = source.getAttributes();
			if (nnm != null) {
				for (int i = 0; i < nnm.getLength(); i++) {
					Attr attr = (Attr) nnm.item(i);
					newElement.setAttributeNode(attr);
				}
			}
			NodeList cnList = source.getChildNodes();
			if (cnList != null) {
				for (int i = 0; i < cnList.getLength(); i++) {
					Node newChild = cnList.item(i);
					newElement.appendChild(newChild);
				}
			}
			destNode = newElement;
			break;
		}
		case ATTRIBUTE_NODE: {

			break;
		}

		case TEXT_NODE: {
			destNode = createTextNode(source.getNodeValue());
			break;
		}

		case CDATA_SECTION_NODE: {
			destNode = createCDATASection(source.getNodeValue());
			break;
		}

		case ENTITY_REFERENCE_NODE: {
			destNode = createEntityReference(source.getNodeName());
			// the subtree is created according to this doc by the method
			// above, so avoid carrying over original subtree
			deep = false;
			break;
		}

		case ENTITY_NODE: {
			break;
		}

		case COMMENT_NODE: {
			destNode = createComment(source.getNodeValue());
			break;
		}

		default: { // Unknown node type
			String msg = "unsupport node type:" + type;
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
		}
		}
		return destNode;
	}

	@Override
	public Element getElementById(String elementId) {
		Node idNode = identifiers.get(elementId);
		if (idNode != null) {
			return (Element) idNode;
		}
		Element idElement = getElementById(elementId, this);
		if (idElement != null) {
			identifiers.put(elementId, idElement);
		}
		return idElement;
	}

	private Element getElementById(String elementId, Node node) {
		Node child;
		Element result;
		child = node.getFirstChild();
		while (child != null) {
			if (child instanceof Element) {
				Element childElement = (Element) child;
				String idString = childElement.getAttribute("id");
				if (elementId.equals(idString)) {
					return childElement;
				}
				result = getElementById(elementId, child);
				if (result != null) {
					return result;
				}
			}
			child = child.getNextSibling();
		}
		return null;
	}

	public ConcurrentHashMap<String, Node> getIdentifiers() {
		return identifiers;
	}

	@Override
	public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInputEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getXmlEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getXmlStandalone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getXmlVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setXmlVersion(String xmlVersion) throws DOMException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getStrictErrorChecking() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setStrictErrorChecking(boolean strictErrorChecking) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDocumentURI() {
		return this.documentURI;
	}

	@Override
	public void setDocumentURI(String documentURI) {
		this.documentURI = documentURI;
	}

	@Override
	public Node adoptNode(Node source) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DOMConfiguration getDomConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void normalizeDocument() {
		// TODO Auto-generated method stub

	}

	@Override
	public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTitle(String title) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getReferrer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HTMLElement getBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBody(HTMLElement body) {
		// TODO Auto-generated method stub

	}

	@Override
	public HTMLCollection getImages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HTMLCollection getApplets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HTMLCollection getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HTMLCollection getForms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HTMLCollection getAnchors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCookie() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCookie(String cookie) {
		// TODO Auto-generated method stub

	}

	@Override
	public void open() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeln(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public NodeList getElementsByName(String elementName) {
		return super.getElementsByTagName(elementName);
	}

}
