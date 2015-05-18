package com.lezo.iscript.crawler.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

public class ScriptText implements Text {
	private String text;
	private String baseUri;
	private Document ownerDocument;

	ScriptText(String text, String baseUri) {
		super();
		this.text = text;
		this.baseUri = baseUri;
	}

	@Override
	public String getData() throws DOMException {
		return this.text;
	}

	@Override
	public void setData(String data) throws DOMException {
		this.text = data;
	}

	@Override
	public int getLength() {
		return this.text.length();
	}

	@Override
	public String substringData(int offset, int count) throws DOMException {
		return this.text.substring(offset, offset + count);
	}

	@Override
	public void appendData(String arg) throws DOMException {
		this.text += arg;
	}

	@Override
	public void insertData(int offset, String arg) throws DOMException {
		StringBuffer buffer = new StringBuffer(this.text);
		StringBuffer result = buffer.insert(offset, arg);
		this.text = result.toString();
	}

	@Override
	public void deleteData(int offset, int count) throws DOMException {
		StringBuffer buffer = new StringBuffer(this.text);
		StringBuffer result = buffer.delete(offset, offset + count);
		this.text = result.toString();
	}

	@Override
	public void replaceData(int offset, int count, String arg) throws DOMException {
		StringBuffer buffer = new StringBuffer(this.text);
		StringBuffer result = buffer.replace(offset, offset + count, arg);
		this.text = result.toString();
	}

	@Override
	public String getNodeName() {
		return "#text";
	}

	@Override
	public String getNodeValue() throws DOMException {
		return this.text;
	}

	@Override
	public void setNodeValue(String nodeValue) throws DOMException {
		this.text = nodeValue;
	}

	@Override
	public short getNodeType() {
		return Node.TEXT_NODE;
	}

	@Override
	public Node getParentNode() {
		return null;
	}

	@Override
	public NodeList getChildNodes() {
		return null;
	}

	@Override
	public Node getFirstChild() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getLastChild() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getPreviousSibling() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getNextSibling() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamedNodeMap getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document getOwnerDocument() {
		return this.ownerDocument;
	}

	@Override
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node removeChild(Node oldChild) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node appendChild(Node newChild) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildNodes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Node cloneNode(boolean deep) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void normalize() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSupported(String feature, String version) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getNamespaceURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPrefix(String prefix) throws DOMException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAttributes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getBaseURI() {
		return this.baseUri;
	}

	@Override
	public short compareDocumentPosition(Node other) throws DOMException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getTextContent() throws DOMException {
		return this.text;
	}

	@Override
	public void setTextContent(String textContent) throws DOMException {
		this.text = textContent;
	}

	@Override
	public boolean isSameNode(Node other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String lookupPrefix(String namespaceURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDefaultNamespace(String namespaceURI) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String lookupNamespaceURI(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEqualNode(Node arg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getFeature(String feature, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getUserData(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Text splitText(int offset) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isElementContentWhitespace() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getWholeText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Text replaceWholeText(String content) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	void setOwnerDocument(Document ownerDocument) {
		this.ownerDocument = ownerDocument;
	}

}
