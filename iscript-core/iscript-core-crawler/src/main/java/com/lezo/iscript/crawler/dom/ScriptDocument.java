package com.lezo.iscript.crawler.dom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Tag;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

import com.lezo.iscript.crawler.dom.browser.ScriptLocation;
import com.lezo.iscript.utils.URLUtils;

//HTMLDocument,DocumentTraversal, DocumentEvent, DocumentRange, DocumentView
public class ScriptDocument extends ScriptElement implements HTMLDocument {
	private ConcurrentHashMap<String, Node> identifiers = new ConcurrentHashMap<String, Node>();
	protected String documentURI;
	private String referrer;
	private ScriptLocation location;
	private org.jsoup.nodes.Document targetDocument;
	private Hashtable<ScriptElement, Vector<LEntry>> eventListeners;
	private BasicCookieStore cookieStore = new BasicCookieStore();

	public ScriptDocument(org.jsoup.nodes.Document targetDocument) {
		super(targetDocument, null);
		this.targetDocument = targetDocument;
		this.location = new ScriptLocation(getBaseURI());
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
		return createElementByNode(targetEle);
	}

	public ScriptElement createElementByNode(org.jsoup.nodes.Node target) throws DOMException {
		ScriptElement element = new ScriptElement(target, this);
		element.setOwnerDocument(this);
		String idValue = target.attr("id");
		if (StringUtils.isNotBlank(idValue)) {
			identifiers.putIfAbsent(idValue, element);
		}
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
		return getBaseURI();
	}

	@Override
	public void setDocumentURI(String documentURI) {
		this.targetDocument.setBaseUri(documentURI);
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
		return this.targetDocument.title();
	}

	@Override
	public void setTitle(String title) {
		this.targetDocument.title(title);
	}

	@Override
	public String getReferrer() {
		return this.referrer;
	}

	@Override
	public String getDomain() {
		return URLUtils.getHost(getURL());
	}

	@Override
	public String getURL() {
		return getBaseURI();
	}

	@Override
	public HTMLElement getBody() {
		NodeList bodyNList = getElementsByTagName("body");
		Node toNode = bodyNList == null || bodyNList.getLength() < 1 ? null : bodyNList.item(0);
		return (HTMLElement) toNode;
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
		List<Cookie> ckList = cookieStore.getCookies();
		String domain = getDomain();
		StringBuilder sb = new StringBuilder();
		String kvPair = "=";
		String vGroup = "; ";
		for (Cookie ck : ckList) {
			if (domain.contains(ck.getDomain())) {
				sb.append(ck.getName());
				sb.append(kvPair);
				sb.append(ck.getValue());
				sb.append(vGroup);

				sb.append(ClientCookie.EXPIRES_ATTR);
				sb.append(kvPair);
				if (ck.getExpiryDate() != null) {
					sb.append(DateUtils.formatDate(ck.getExpiryDate()));
				}
				sb.append(vGroup);

				sb.append(ClientCookie.DOMAIN_ATTR);
				sb.append(kvPair);
				sb.append(ck.getDomain());
				sb.append(vGroup);

				sb.append(ClientCookie.PATH_ATTR);
				sb.append(kvPair);
				sb.append(ck.getPath());
				sb.append(vGroup);
			}
		}
		return sb.toString();
	}

	@Override
	public synchronized void setCookie(String cookie) {
		List<Cookie> ckList = toCookies(cookie);
		for (Cookie ck : ckList) {
			cookieStore.addCookie(ck);
		}

	}

	public List<Cookie> toCookies(String cookie) {
		if (StringUtils.isBlank(cookie)) {
			return new ArrayList<Cookie>(0);
		}
		List<Cookie> cookieList = new ArrayList<Cookie>();
		String[] unitArr = cookie.split(";");
		BasicClientCookie newCookie = null;
		for (String unit : unitArr) {
			String[] kvArr = unit.split("=");
			if (kvArr == null || kvArr.length != 2) {
				continue;
			}
			String key = kvArr[0].trim();
			String value = kvArr[1].trim();
			if (ClientCookie.EXPIRES_ATTR.equals(key)) {
				try {
					if (StringUtils.isNotBlank(value)) {
						newCookie.setExpiryDate(DateUtils.parseDate(value));
					}
				} catch (DateParseException e) {
					e.printStackTrace();
				}
			} else if (ClientCookie.DOMAIN_ATTR.equals(key)) {
				newCookie.setDomain(value);
			} else if (ClientCookie.PATH_ATTR.equals(key)) {
				newCookie.setPath(value);
			} else {
				if (newCookie != null) {
					cookieList.add(newCookie);
				}
				newCookie = new BasicClientCookie(key, value);
			}
		}
		if (newCookie != null) {
			cookieList.add(newCookie);
		}
		return cookieList;
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
		// XXX head script will be appendChild to body
		// XXX body script will be sibling to current script
		Document newDom = Jsoup.parseBodyFragment(text, getBaseURI());
		NodeList headNList = getElementsByTagName("head");
		Node headNode = headNList.item(0);
		ScriptElement toElement = (ScriptElement) headNode;
		addChild(toElement, newDom.head().childNodesCopy());
		// add body
		toElement = (ScriptElement) getBody();
		addChild(toElement, newDom.body().childNodesCopy());
	}

	private void addChild(ScriptElement toElement, List<org.jsoup.nodes.Node> childList) {
		if (childList == null) {
			return;
		}
		int len = childList.size();
		for (int i = 0; i < len; i++) {
			org.jsoup.nodes.Node ch = childList.get(i);
			org.jsoup.nodes.Element target = (org.jsoup.nodes.Element) toElement.getTarget();
			target.appendChild(ch);
			ScriptHtmlParser.doCopy(ch, toElement, this);
		}
	}

	@Override
	public void writeln(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public NodeList getElementsByName(String elementName) {
		return super.getElementsByTagName(elementName);
	}

	public org.jsoup.nodes.Document getTargetDocument() {
		return targetDocument;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	@Override
	public String toString() {
		return "ScriptDocument";
	}

	public ScriptLocation getLocation() {
		return location;
	}

	public void setLocation(ScriptLocation location) {
		this.location = location;
	}

	public void setLocation(String location) {
		this.location.setHref(location);
	}

	protected void addEventListener(ScriptElement node, String type, EventListener listener, boolean useCapture) {
		// We can't dispatch to blank type-name, and of course we need
		// a listener to dispatch to
		if (type == null || type.length() == 0 || listener == null)
			return;

		// Each listener may be registered only once per type per phase.
		// Simplest way to code that is to zap the previous entry, if any.
		removeEventListener(node, type, listener, useCapture);

		Vector<LEntry> nodeListeners = getEventListeners(node);
		if (nodeListeners == null) {
			nodeListeners = new Vector<LEntry>();
			setEventListeners(node, nodeListeners);
		}
		nodeListeners.addElement(new LEntry(type, listener, useCapture));

		// Record active listener
		LCount lc = LCount.lookup(type);
		if (useCapture) {
			++lc.captures;
			++lc.total;
		} else {
			++lc.bubbles;
			++lc.total;
		}

	}

	protected void removeEventListener(ScriptElement node, String type, EventListener listener, boolean useCapture) {
		// If this couldn't be a valid listener registration, ignore request
		if (type == null || type.length() == 0 || listener == null)
			return;
		Vector<LEntry> nodeListeners = getEventListeners(node);
		if (nodeListeners == null)
			return;

		// Note that addListener has previously ensured that
		// each listener may be registered only once per type per phase.
		// count-down is OK for deletions!
		for (int i = nodeListeners.size() - 1; i >= 0; --i) {
			LEntry le = (LEntry) nodeListeners.elementAt(i);
			if (le.useCapture == useCapture && le.listener == listener && le.type.equals(type)) {
				nodeListeners.removeElementAt(i);
				// Storage management: Discard empty listener lists
				if (nodeListeners.size() == 0)
					setEventListeners(node, null);

				// Remove active listener
				LCount lc = LCount.lookup(type);
				if (useCapture) {
					--lc.captures;
					--lc.total;
				} else {
					--lc.bubbles;
					--lc.total;
				}

				break; // Found it; no need to loop farther.
			}
		}
	}

	protected boolean dispatchEvent(ScriptElement node, Event event) {
		if (event == null)
			return false;

		// Can't use anyone else's implementation, since there's no public
		// API for setting the event's processing-state fields.
		ScriptEvent evt = (ScriptEvent) event;

		// VALIDATE -- must have been initialized at least once, must have
		// a non-null non-blank name.
		if (!evt.initialized || evt.type == null || evt.type.length() == 0) {
			String msg = "element:" + node.getNodeName();
			throw new EventException(EventException.UNSPECIFIED_EVENT_TYPE_ERR, msg);
		}

		// If nobody is listening for this event, discard immediately
		LCount lc = LCount.lookup(evt.getType());
		if (lc.total == 0)
			return evt.preventDefault;

		// INITIALIZE THE EVENT'S DISPATCH STATUS
		// (Note that Event objects are reusable in our implementation;
		// that doesn't seem to be explicitly guaranteed in the DOM, but
		// I believe it is the intent.)
		evt.target = node;
		evt.stopPropagation = false;
		evt.preventDefault = false;

		// Capture pre-event parentage chain, not including target;
		// use pre-event-dispatch ancestors even if event handlers mutate
		// document and change the target's context.
		// Note that this is parents ONLY; events do not
		// cross the Attr/Element "blood/brain barrier".
		// DOMAttrModified. which looks like an exception,
		// is issued to the Element rather than the Attr
		// and causes a _second_ DOMSubtreeModified in the Element's
		// tree.
		ArrayList<Node> pv = new ArrayList<Node>(10);
		Node p = node;
		Node n = p.getParentNode();
		while (n != null) {
			pv.add(n);
			p = n;
			n = n.getParentNode();
		}

		// CAPTURING_PHASE:
		if (lc.captures > 0) {
			evt.eventPhase = Event.CAPTURING_PHASE;
			// Ancestors are scanned, root to target, for
			// Capturing listeners.
			for (int j = pv.size() - 1; j >= 0; --j) {
				if (evt.stopPropagation)
					break; // Someone set the flag. Phase ends.

				// Handle all capturing listeners on this node
				ScriptElement nn = (ScriptElement) pv.get(j);
				evt.currentTarget = nn;
				Vector<LEntry> nodeListeners = getEventListeners(nn);
				if (nodeListeners != null) {
					Vector<LEntry> nl = (Vector<LEntry>) nodeListeners.clone();
					// call listeners in the order in which they got registered
					int nlsize = nl.size();
					for (int i = 0; i < nlsize; i++) {
						LEntry le = (LEntry) nl.elementAt(i);
						if (le.useCapture && le.type.equals(evt.type) && nodeListeners.contains(le)) {
							try {
								le.listener.handleEvent(evt);
							} catch (Exception e) {
								// All exceptions are ignored.
							}
						}
					}
				}
			}
		}

		// Both AT_TARGET and BUBBLE use non-capturing listeners.
		if (lc.bubbles > 0) {
			// AT_TARGET PHASE: Event is dispatched to NON-CAPTURING listeners
			// on the target node. Note that capturing listeners on the target
			// node are _not_ invoked, even during the capture phase.
			evt.eventPhase = Event.AT_TARGET;
			evt.currentTarget = node;
			Vector nodeListeners = getEventListeners(node);
			if (!evt.stopPropagation && nodeListeners != null) {
				Vector nl = (Vector) nodeListeners.clone();
				// call listeners in the order in which they got registered
				int nlsize = nl.size();
				for (int i = 0; i < nlsize; i++) {
					LEntry le = (LEntry) nl.elementAt(i);
					if (!le.useCapture && le.type.equals(evt.type) && nodeListeners.contains(le)) {
						try {
							le.listener.handleEvent(evt);
						} catch (Exception e) {
							// All exceptions are ignored.
						}
					}
				}
			}
			// BUBBLING_PHASE: Ancestors are scanned, target to root, for
			// non-capturing listeners. If the event's preventBubbling flag
			// has been set before processing of a node commences, we
			// instead immediately advance to the default phase.
			// Note that not all events bubble.
			if (evt.bubbles) {
				evt.eventPhase = Event.BUBBLING_PHASE;
				int pvsize = pv.size();
				for (int j = 0; j < pvsize; j++) {
					if (evt.stopPropagation)
						break; // Someone set the flag. Phase ends.

					// Handle all bubbling listeners on this node
					ScriptElement nn = (ScriptElement) pv.get(j);
					evt.currentTarget = nn;
					nodeListeners = getEventListeners(nn);
					if (nodeListeners != null) {
						Vector<LEntry> nl = (Vector<LEntry>) nodeListeners.clone();
						// call listeners in the order in which they got
						// registered
						int nlsize = nl.size();
						for (int i = 0; i < nlsize; i++) {
							LEntry le = (LEntry) nl.elementAt(i);
							if (!le.useCapture && le.type.equals(evt.type) && nodeListeners.contains(le)) {
								try {
									le.listener.handleEvent(evt);
								} catch (Exception e) {
									// All exceptions are ignored.
								}
							}
						}
					}
				}
			}
		}

		// DEFAULT PHASE: Some DOMs have default behaviors bound to specific
		// nodes. If this DOM does, and if the event's preventDefault flag has
		// not been set, we now return to the target node and process its
		// default handler for this event, if any.
		// No specific phase value defined, since this is DOM-internal
		if (lc.defaults > 0 && (!evt.cancelable || !evt.preventDefault)) {
			// evt.eventPhase = Event.DEFAULT_PHASE;
			// evt.currentTarget = node;
			// DO_DEFAULT_OPERATION
		}

		return evt.preventDefault;
	}

	/**
	 * Store event listener registered on a given node This is another place
	 * where we could use weak references! Indeed, the node here won't be GC'ed
	 * as long as some listener is registered on it, since the eventsListeners
	 * table will have a reference to the node.
	 */
	protected void setEventListeners(ScriptElement n, Vector<LEntry> listeners) {
		if (eventListeners == null) {
			eventListeners = new Hashtable<ScriptElement, Vector<LEntry>>();
		}
		if (listeners == null) {
			eventListeners.remove(n);
			if (eventListeners.isEmpty()) {
				// stop firing events when there isn't any listener
				// mutationEvents = false;
			}
		} else {
			eventListeners.put(n, listeners);
			// turn mutation events on
			// mutationEvents = true;
		}
	}

	/**
	 * Retreive event listener registered on a given node
	 */
	protected Vector<LEntry> getEventListeners(ScriptElement n) {
		if (eventListeners == null) {
			return null;
		}
		return eventListeners.get(n);
	}

	//
	// EventTarget support (public and internal)
	//

	//
	// Constants
	//

	/*
	 * NON-DOM INTERNAL: Class LEntry is just a struct used to represent event
	 * listeners registered with this node. Copies of this object are hung from
	 * the nodeListeners Vector. <p> I considered using two vectors -- one for
	 * capture, one for bubble -- but decided that since the list of listeners
	 * is probably short in most cases, it might not be worth spending the
	 * space. ***** REVISIT WHEN WE HAVE MORE EXPERIENCE.
	 */
	class LEntry implements Serializable {

		private static final long serialVersionUID = -8426757059492421631L;
		String type;
		EventListener listener;
		boolean useCapture;

		/**
		 * NON-DOM INTERNAL: Constructor for Listener list Entry
		 * 
		 * @param type
		 *            Event name (NOT event group!) to listen for.
		 * @param listener
		 *            Who gets called when event is dispatched
		 * @param useCaptue
		 *            True iff listener is registered on capturing phase rather
		 *            than at-target or bubbling
		 */
		LEntry(String type, EventListener listener, boolean useCapture) {
			this.type = type;
			this.listener = listener;
			this.useCapture = useCapture;
		}

	} // LEntry

	static class LCount {
		static java.util.Hashtable lCounts = new java.util.Hashtable();
		public int captures = 0, bubbles = 0, defaults, total = 0;

		static LCount lookup(String evtName) {
			LCount lc = (LCount) lCounts.get(evtName);
			if (lc == null)
				lCounts.put(evtName, (lc = new LCount()));
			return lc;
		}
	} // class LCount

}
