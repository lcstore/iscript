package com.lezo.iscript.crawler.dom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.html.HTMLElement;

public class ScriptElement implements HTMLElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ScriptElement> childList;
	private org.jsoup.nodes.Node target;
	private ScriptElement parent;
	private short nodeType = Node.ELEMENT_NODE;
	protected volatile Object treeLock = this;
	private volatile String prefix;
	private Document ownerDocument;

	ScriptElement(Scriptable scope, org.jsoup.nodes.Node target, ScriptElement parent, Class<?> destClass) {
		// super(scope, null, destClass, true);
		this.target = target;
		this.parent = parent;
		this.childList = new ArrayList<ScriptElement>(4);
		// super.initMembers();
	}

	@Override
	public String getNodeName() {
		return target.nodeName();
	}

	@Override
	public String getNodeValue() throws DOMException {
		if (this.target instanceof org.jsoup.nodes.Element) {
			org.jsoup.nodes.Element element = (org.jsoup.nodes.Element) this.target;
			return element.val();
		} else if (this.target instanceof org.jsoup.nodes.TextNode) {
			org.jsoup.nodes.TextNode textNode = (org.jsoup.nodes.TextNode) this.target;
			return textNode.text();
		} else if (this.target instanceof org.jsoup.nodes.DataNode) {
			org.jsoup.nodes.DataNode dataNode = (org.jsoup.nodes.DataNode) this.target;
			return dataNode.getWholeData();
		}
		return this.target.attr("value");
	}

	@Override
	public void setNodeValue(String nodeValue) throws DOMException {
		if (this.target instanceof org.jsoup.nodes.Element) {
			org.jsoup.nodes.Element element = (org.jsoup.nodes.Element) this.target;
			element.val(nodeValue);
		}
		this.target.attr("value", nodeValue);
	}

	@Override
	public short getNodeType() {
		return this.nodeType;
	}

	@Override
	public Node getParentNode() {
		return parent;
	}

	@Override
	public NodeList getChildNodes() {

		return new NodeList() {

			@Override
			public Node item(int index) {
				return childList.get(index);
			}

			@Override
			public int getLength() {
				return childList.size();
			}
		};
	}

	@Override
	public Node getFirstChild() {
		return childList.isEmpty() ? null : childList.get(0);
	}

	@Override
	public Node getLastChild() {
		int size = childList.size();
		return childList.isEmpty() ? null : childList.get(size - 1);
	}

	@Override
	public Node getPreviousSibling() {
		if (parent == null) {
			return null;
		}
		NodeList childList = parent.getChildNodes();
		if (childList == null) {
			return null;
		}
		int index = target.siblingIndex() - 1;
		return index < 0 || index >= childList.getLength() ? null : childList.item(index);
	}

	@Override
	public Node getNextSibling() {
		if (parent == null) {
			return null;
		}
		NodeList childList = parent.getChildNodes();
		if (childList == null) {
			return null;
		}
		int index = target.siblingIndex() + 1;
		return index < 0 || index >= childList.getLength() ? null : childList.item(index);
	}

	@Override
	public NamedNodeMap getAttributes() {
		Attributes attrs = this.target.attributes();
		if (attrs == null) {
			return null;
		}
		List<Attribute> attrList = attrs.asList();
		List<Node> destAttrList = new ArrayList<Node>(attrList.size());
		for (Attribute attr : attrList) {
			destAttrList.add(new ScriptAttr(attr.getKey(), attr.getValue()));
		}
		return new ScriptNamedNodeMap(destAttrList);
	}

	@Override
	public Document getOwnerDocument() {
		return this.ownerDocument;
	}

	@Override
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		synchronized (this.treeLock) {
			List<ScriptElement> nl = this.childList;
			int idx = nl == null ? -1 : nl.indexOf(refChild);
			if (idx == -1) {
				throw new DOMException(DOMException.NOT_FOUND_ERR, "refChild not found");
			}

			ScriptElement newElement = (ScriptElement) newChild;
			ScriptElement refElement = (ScriptElement) refChild;
			
			refElement.getTarget().before(newElement.getTarget());
			nl.add(idx, newElement);
		}
		return newChild;
	}

	@Override
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		synchronized (this.treeLock) {
			List<ScriptElement> nl = this.childList;
			int idx = nl == null ? -1 : nl.indexOf(oldChild);
			if (idx == -1) {
				throw new DOMException(DOMException.NOT_FOUND_ERR, "oldChild not found");
			}
			ScriptElement newElement = null;
			if (newChild instanceof ScriptElement) {
				newElement = (ScriptElement) newChild;
			} else {
				newElement = ScriptElementUtils.toElement(newChild, this);
			}
			nl.set(idx, newElement);
		}
		return newChild;
	}

	@Override
	public Node removeChild(Node oldChild) throws DOMException {
		synchronized (this.treeLock) {
			List<ScriptElement> nl = this.childList;
			if (nl == null || !nl.remove(oldChild)) {
				throw new DOMException(DOMException.NOT_FOUND_ERR, "oldChild not found");
			}
		}
		return oldChild;
	}

	@Override
	public Node appendChild(Node newChild) throws DOMException {
		synchronized (this.treeLock) {
			List<ScriptElement> nl = this.childList;
			if (nl == null) {
				nl = new ArrayList<ScriptElement>(3);
				this.childList = nl;
			}
			ScriptElement newElement = null;
			if (newChild instanceof ScriptElement) {
				newElement = (ScriptElement) newChild;
			} else {
				newElement = ScriptElementUtils.toElement(newChild, this);
			}
			newElement.setParent(this);
			nl.add(newElement);
		}
		return newChild;
	}

	@Override
	public boolean hasChildNodes() {
		return !this.childList.isEmpty();
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
		return true;
	}

	@Override
	public String getNamespaceURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPrefix() {
		return this.prefix;
	}

	@Override
	public void setPrefix(String prefix) throws DOMException {
		this.prefix = prefix;
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
		return this.target.baseUri();
	}

	@Override
	public short compareDocumentPosition(Node other) throws DOMException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getTextContent() throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTextContent(String textContent) throws DOMException {
		// TODO Auto-generated method stub

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
	public String getTagName() {
		if (this.target instanceof org.jsoup.nodes.Element) {
			org.jsoup.nodes.Element element = (org.jsoup.nodes.Element) this.target;
			return element.tagName();
		}
		return this.target.nodeName();
	}

	@Override
	public String getAttribute(String name) {
		String normalName = this.normalizeAttributeName(name);
		return this.target.attr(normalName);
	}

	@Override
	public void setAttribute(String name, String value) throws DOMException {
		String attributeKey = normalizeAttributeName(name);
		this.target.attr(attributeKey, value);
	}

	protected final String normalizeAttributeName(String name) {
		return name.toLowerCase();
	}

	@Override
	public void removeAttribute(String name) throws DOMException {
		String attributeKey = normalizeAttributeName(name);
		this.target.removeAttr(attributeKey);
	}

	@Override
	public Attr getAttributeNode(String name) {
		String key = this.normalizeAttributeName(name);
		String value = getAttribute(key);
		return value == null ? null : new ScriptAttr(key, value);
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		setAttribute(newAttr.getName(), newAttr.getValue());
		return newAttr;
	}

	@Override
	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		removeAttribute(oldAttr.getName());
		return oldAttr;
	}

	@Override
	public NodeList getElementsByTagName(String name) {
		boolean matchesAll = "*".equals(name);
		List<Node> descendents = new LinkedList<Node>();
		synchronized (this.treeLock) {
			List<ScriptElement> nl = this.childList;
			if (nl != null) {
				Iterator<ScriptElement> i = nl.iterator();
				while (i.hasNext()) {
					ScriptElement child = i.next();
					if (child instanceof Element) {
						Element childElement = (Element) child;
						if (matchesAll || isSameTag(childElement, name)) {
							descendents.add(child);
						}
						NodeList sublist = childElement.getElementsByTagName(name);
						int length = sublist.getLength();
						for (int idx = 0; idx < length; idx++) {
							descendents.add(sublist.item(idx));
						}
					}
				}
			}
		}
		return new ScriptNodeList(descendents);
	}

	protected static boolean isSameTag(Node node, String name) {
		return node.getNodeName().equalsIgnoreCase(name);
	}

	@Override
	public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
		// TODO Auto-generated method stub

	}

	@Override
	public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAttribute(String name) {
		return getAttribute(name) != null;
	}

	@Override
	public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TypeInfo getSchemaTypeInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdAttribute(String name, boolean isId) throws DOMException {
		Attr idAttr = getAttributeNode(name);
		if (idAttr == null) {
			String msg = "NOT FOUN ATTR by name:" + name;
			throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
		}
		setIdAttributeNode(idAttr, isId);
	}

	@Override
	public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
		if (idAttr == null) {
			throw new IllegalArgumentException("idAttr can not be null.");
		}
		ScriptDocument doc = (ScriptDocument) getOwnerDocument();
		ConcurrentHashMap<String, Node> identifiers = doc.getIdentifiers();
		if (!isId) {
			identifiers.remove(idAttr.getValue());
		} else {
			identifiers.put(idAttr.getValue(), idAttr);
		}
	}

	public void setNodeType(short nodeType) {
		this.nodeType = nodeType;
	}

	public void setParent(ScriptElement parent) {
		this.parent = parent;
	}

	void setOwnerDocument(Document ownerDocument) {
		this.ownerDocument = ownerDocument;
	}

	public org.jsoup.nodes.Node getTarget() {
		return target;
	}

	public void setTarget(org.jsoup.nodes.Node target) {
		this.target = target;
	}

	public ScriptElement getParent() {
		return parent;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.target.nodeName()).append(getNodeType()).append(getParentNode())
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScriptElement other = (ScriptElement) obj;
		return new EqualsBuilder().append(this.target.nodeName(), other.getTarget().nodeName())
				.append(getNodeType(), other.getNodeType()).append(getParentNode(), other.getParentNode()).isEquals();
	}

	// /////////////////////

	@Override
	public String getId() {
		return getAttribute("id");
	}

	@Override
	public void setId(String id) {
		setAttribute("id", id);
	}

	@Override
	public String getTitle() {
		return getAttribute("title");
	}

	@Override
	public void setTitle(String title) {
		setAttribute("title", title);
	}

	@Override
	public String getLang() {
		return getAttribute("lang");
	}

	@Override
	public void setLang(String lang) {
		setAttribute("lang", lang);
	}

	@Override
	public String getDir() {
		return getAttribute("dir");
	}

	@Override
	public void setDir(String dir) {
		setAttribute("dir", dir);
	}

	@Override
	public String getClassName() {
		return getAttribute("class");
	}

	@Override
	public void setClassName(String className) {
		setAttribute("class", className);
	}

	@Override
	public String toString() {
		return "ScriptElement";
	}

}
