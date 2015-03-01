package com.lezo.iscript.yeam.defend.loader;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.util.Enumeration;

import sun.misc.Resource;

public class CloseableClassLoader extends SecureClassLoader implements Closeable{
	private AccessControlContext acc = AccessController.getContext();
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		try {
		    return 
			AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
			    public Class<?> run() throws ClassNotFoundException {
				String path = name.replace('.', '/').concat(".class");
				Resource res = null;
				if (res != null) {
				    try {
				    	URL url = res.getCodeSourceURL();
				    	ByteBuffer bb = res.getByteBuffer();
				        byte[] bytes = ((bb == null)? res.getBytes() : null);
				        // NOTE: Must read certificates AFTER reading bytes above.
				        CodeSigner[] signers = res.getCodeSigners();
				        CodeSource cs = new CodeSource(url, signers);
				        return (bb != null)? defineClass(name, bb, cs) : 
		                    defineClass(name, bytes, 0, bytes.length, cs);
				    } catch (IOException e) {
					throw new ClassNotFoundException(name, e);
				    }
				} else {
				    throw new ClassNotFoundException(name);
				}
			    }
			}, acc);
		} catch (java.security.PrivilegedActionException pae) {
		    throw (ClassNotFoundException) pae.getException();
		}
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.loadClass(name, resolve);
	}

	@Override
	public URL getResource(String name) {
		// TODO Auto-generated method stub
		return super.getResource(name);
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		// TODO Auto-generated method stub
		return super.getResources(name);
	}

	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		// TODO Auto-generated method stub
		return super.findResources(name);
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		// TODO Auto-generated method stub
		return super.getResourceAsStream(name);
	}

}
