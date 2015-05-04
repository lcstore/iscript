package com.lezo.iscript.envjs.dom;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
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
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLCollection;

import com.lezo.iscript.envjs.window.LocationAdapt;
import com.sun.org.apache.xerces.internal.dom.events.EventImpl;
import com.sun.org.apache.xerces.internal.dom.events.MutationEventImpl;

@Deprecated
public class DocumentAdapt implements Document, EventTarget, DocumentEvent {
	private static Logger logger = Logger.getLogger(DocumentAdapt.class);
	private Document document;
	private LocationAdapt location;
	private Map<String, String> cookieMap = new HashMap<String, String>();
	private String domain;
	private String title;
	private String referrer;
//	private HTMLElement body;
	private Element body;
	private Map<String, Element> idElementMap = new HashMap<String, Element>();

	public DocumentAdapt(Document document, LocationAdapt location) {
		super();
		this.document = document;
		this.location = location;
	}

	@Override
	public String getNodeName() {
		return document.getNodeName();
	}

	@Override
	public String getNodeValue() throws DOMException {
		return document.getNodeValue();
	}

	@Override
	public void setNodeValue(String nodeValue) throws DOMException {
		document.setNodeValue(nodeValue);
	}

	@Override
	public short getNodeType() {
		return document.getNodeType();
	}

	@Override
	public Node getParentNode() {
		return document.getParentNode();
	}

	@Override
	public NodeList getChildNodes() {
		return document.getChildNodes();
	}

	@Override
	public Node getFirstChild() {
		return document.getFirstChild();
	}

	@Override
	public Node getLastChild() {
		return document.getLastChild();
	}

	@Override
	public Node getPreviousSibling() {
		return document.getPreviousSibling();
	}

	@Override
	public Node getNextSibling() {
		return document.getNextSibling();
	}

	@Override
	public NamedNodeMap getAttributes() {
		return document.getAttributes();
	}

	@Override
	public Document getOwnerDocument() {
		return document.getOwnerDocument();
	}

	@Override
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return document.insertBefore(newChild, refChild);
	}

	@Override
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return document.replaceChild(newChild, oldChild);
	}

	@Override
	public Node removeChild(Node oldChild) throws DOMException {
		return document.removeChild(oldChild);
	}

	@Override
	public Node appendChild(Node newChild) throws DOMException {
		return document.appendChild(newChild);
	}

	@Override
	public boolean hasChildNodes() {
		return document.hasChildNodes();
	}

	@Override
	public Node cloneNode(boolean deep) {
		return document.cloneNode(deep);
	}

	@Override
	public void normalize() {
		document.normalize();
	}

	@Override
	public boolean isSupported(String feature, String version) {
		return document.isSupported(feature, version);
	}

	@Override
	public String getNamespaceURI() {
		return document.getNamespaceURI();
	}

	@Override
	public String getPrefix() {
		return document.getPrefix();
	}

	@Override
	public void setPrefix(String prefix) throws DOMException {
		document.setPrefix(prefix);
	}

	@Override
	public String getLocalName() {
		return document.getLocalName();
	}

	@Override
	public boolean hasAttributes() {
		return document.hasAttributes();
	}

//	@Override
//	public String getBaseURI() {
//		return document.getBaseURI();
//	}
//
//	@Override
//	public short compareDocumentPosition(Node other) throws DOMException {
//		return document.compareDocumentPosition(other);
//	}
//
//	@Override
//	public String getTextContent() throws DOMException {
//		return document.getTextContent();
//	}
//
//	@Override
//	public void setTextContent(String textContent) throws DOMException {
//		document.setTextContent(textContent);
//	}
//
//	@Override
//	public boolean isSameNode(Node other) {
//		return document.isSameNode(other);
//	}
//
//	@Override
//	public String lookupPrefix(String namespaceURI) {
//		return document.lookupPrefix(namespaceURI);
//	}
//
//	@Override
//	public boolean isDefaultNamespace(String namespaceURI) {
//		return document.isDefaultNamespace(namespaceURI);
//	}
//
//	@Override
//	public String lookupNamespaceURI(String prefix) {
//		return document.lookupNamespaceURI(prefix);
//	}
//
//	@Override
//	public boolean isEqualNode(Node arg) {
//		return document.isEqualNode(arg);
//	}
//
//	@Override
//	public Object getFeature(String feature, String version) {
//		return document.getFeature(feature, version);
//	}
//
//	@Override
//	public Object setUserData(String key, Object data, UserDataHandler handler) {
//		return document.setUserData(key, data, handler);
//	}
//
//	@Override
//	public Object getUserData(String key) {
//		return document.getUserData(key);
//	}

	@Override
	public DocumentType getDoctype() {
		return document.getDoctype();
	}

	@Override
	public DOMImplementation getImplementation() {
		return document.getImplementation();
	}

	@Override
	public Element getDocumentElement() {
		return document.getDocumentElement();
	}

	@Override
	public Element createElement(String tagName) throws DOMException {
		return document.createElement(tagName);
	}

	@Override
	public DocumentFragment createDocumentFragment() {
		return document.createDocumentFragment();
	}

	@Override
	public Text createTextNode(String data) {
		return document.createTextNode(data);
	}

	@Override
	public Comment createComment(String data) {
		return document.createComment(data);
	}

	@Override
	public CDATASection createCDATASection(String data) throws DOMException {
		return document.createCDATASection(data);
	}

	@Override
	public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
		return document.createProcessingInstruction(target, data);
	}

	@Override
	public Attr createAttribute(String name) throws DOMException {
		return document.createAttribute(name);
	}

	@Override
	public EntityReference createEntityReference(String name) throws DOMException {
		return document.createEntityReference(name);
	}

	@Override
	public NodeList getElementsByTagName(String tagname) {
		return document.getElementsByTagName(tagname);
	}

	@Override
	public Node importNode(Node importedNode, boolean deep) throws DOMException {
		return document.importNode(importedNode, deep);
	}

	@Override
	public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
		return document.createElementNS(namespaceURI, qualifiedName);
	}

	@Override
	public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
		return document.createAttributeNS(namespaceURI, qualifiedName);
	}

	@Override
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
		return document.getElementsByTagNameNS(namespaceURI, localName);
	}

	@Override
	public Element getElementById(String elementId) {
		Element element = document.getElementById(elementId);
		if (element == null) {
			element = idElementMap.get(elementId);
		}
		return element;
	}

	void setElementById(String id, Element element) {
		synchronized (this) {
			this.idElementMap.put(id, element);
		}
	}

	void removeElementById(String id) {
		synchronized (this) {
			this.idElementMap.remove(id);
		}
	}

//	@Override
//	public String getInputEncoding() {
//		return document.getInputEncoding();
//	}
//
//	@Override
//	public String getXmlEncoding() {
//		return document.getXmlEncoding();
//	}
//
//	@Override
//	public boolean getXmlStandalone() {
//		return document.getXmlStandalone();
//	}
//
//	@Override
//	public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
//		document.setXmlStandalone(xmlStandalone);
//	}
//
//	@Override
//	public String getXmlVersion() {
//		return document.getXmlVersion();
//	}
//
//	@Override
//	public void setXmlVersion(String xmlVersion) throws DOMException {
//		document.setXmlVersion(xmlVersion);
//	}
//
//	@Override
//	public boolean getStrictErrorChecking() {
//		return document.getStrictErrorChecking();
//	}
//
//	@Override
//	public void setStrictErrorChecking(boolean strictErrorChecking) {
//		document.setStrictErrorChecking(strictErrorChecking);
//	}
//
//	@Override
//	public String getDocumentURI() {
//		return document.getDocumentURI();
//	}
//
//	@Override
//	public void setDocumentURI(String documentURI) {
//		document.setDocumentURI(documentURI);
//	}
//
//	@Override
//	public Node adoptNode(Node source) throws DOMException {
//		return document.adoptNode(source);
//	}
//
//	@Override
//	public DOMConfiguration getDomConfig() {
//		return document.getDomConfig();
//	}
//
//	@Override
//	public void normalizeDocument() {
//		document.normalizeDocument();
//	}
//
//	@Override
//	public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
//		return document.renameNode(n, namespaceURI, qualifiedName);
//	}

	public LocationAdapt getLocation() {
		return location;
	}

	public void setLocation(LocationAdapt location) {
		this.location = location;
	}

	public void setCookie(final String cookie) throws DOMException {
		LinkedHashMap<String, String> keyMap = getKeyMap(cookie);
		String domain = keyMap.remove("domain");
		for (Entry<String, String> entry : keyMap.entrySet()) {
			String name = entry.getKey();
			String key = name + "." + domain;
			cookieMap.remove(key);
			cookieMap.put(key, cookie);
			break;
		}
	}

	private LinkedHashMap<String, String> getKeyMap(String cookie) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		String[] pairArray = cookie.split(";");
		if (pairArray != null) {
			for (String pair : pairArray) {
				String[] keyValue = pair.trim().split("=");
				if (keyValue != null && keyValue.length == 2) {
					map.put(keyValue[0].trim(), keyValue[1].trim());
				}
			}
		}
		return map;
	}

	public String getCookie() throws DOMException {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : cookieMap.entrySet()) {
			if (sb.length() > 0) {
				sb.append("; ");
			}
			sb.append(entry.getValue());
		}
		return sb.toString();
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReferrer() {
		return this.referrer;
	}

	public String getDomain() {
		return this.domain;
	}

	public String getURL() {
		return location.getHref();
	}

	public Element getBody() {
		if (this.body != null) {
			return this.body;
		}
		synchronized (this) {
			if (this.body == null) {
				NodeList nodeList = document.getElementsByTagName("body");
				this.body = (Element) nodeList.item(0);
			}
		}
		return this.body;
	}

	//
	// @Override
	// public void setBody(HTMLElement body) {
	// synchronized (this) {
	// this.body = body;
	// }
	// }

	public HTMLCollection getImages() {
		// TODO Auto-generated method stub
		return null;
	}

	public HTMLCollection getApplets() {
		// TODO Auto-generated method stub
		return null;
	}

	public HTMLCollection getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	public HTMLCollection getForms() {
		// TODO Auto-generated method stub
		return null;
	}

	public HTMLCollection getAnchors() {
		// TODO Auto-generated method stub
		return null;
	}

	public void open() {
		// TODO Auto-generated method stub

	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public void writeln(String text) {
		write(text + "\r\n");
	}

	public NodeList getElementsByName(String elementName) {
		return document.getElementsByTagName(elementName);
	}

	public void write(String text) {
		System.out.println("<write>:" + text);
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	@Override
	public Event createEvent(String type) throws DOMException {
		if (type.equalsIgnoreCase("Events") || "Event".equals(type)) {
			return new EventImpl();
		} else if (type.equalsIgnoreCase("MutationEvents") || "MutationEvent".equals(type)) {
			return new MutationEventImpl();
		}
		return new EventImpl();
	}

	@Override
	public void addEventListener(String type, EventListener listener, boolean useCapture) {
		EventTarget eventTarget = (EventTarget) document;
		eventTarget.addEventListener(type, listener, useCapture);
	}

	public void removeEventListener(String type, EventListener listener, boolean useCapture) {
		EventTarget eventTarget = (EventTarget) document;
		eventTarget.removeEventListener(type, listener, useCapture);
	}

	public boolean dispatchEvent(Event event) throws EventException {
		EventTarget eventTarget = (EventTarget) document;
		return eventTarget.dispatchEvent(event);
	}

}
