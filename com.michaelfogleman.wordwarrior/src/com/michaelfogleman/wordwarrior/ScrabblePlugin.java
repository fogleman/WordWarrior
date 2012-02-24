package com.michaelfogleman.wordwarrior;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.michaelfogleman.wordwarrior.engine.Dictionary;
import com.michaelfogleman.wordwarrior.model.DefaultEventManager;
import com.michaelfogleman.wordwarrior.model.DictionaryType;
import com.michaelfogleman.wordwarrior.model.IEventManager;
import com.michaelfogleman.wordwarrior.model.Session;
import com.michaelfogleman.wordwarrior.protocol.isc.IscConnection;

/**
 * The main plugin class to be used in the desktop.
 */
public class ScrabblePlugin extends Plugin {
	
	private Session session;
	private IEventManager eventManager;
		
	public ScrabblePlugin() {
		plugin = this;
	}
	
	public Session getSession() {
		return session;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		eventManager = new DefaultEventManager();
		session = new Session(new IscConnection());
	}
	
	public IEventManager getEventManager() {
		return eventManager;
	}
	
	public void connect(String username, String password) {
		session.login(username, password);
	}

	public void stop(BundleContext context) throws Exception {
		session.close();
		super.stop(context);
		plugin = null;
	}
	
	public void preloadDictionary() {
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				Dictionary.getInstance(DictionaryType.TWL06);
			}
		};
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	
	private static ScrabblePlugin plugin;

	public static ScrabblePlugin getDefault() {
		return plugin;
	}

}
