package fr.labri.reparenting.plugin.util;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import fr.labri.reparenting.plugin.core.ErrorHandler;

public class ExtensionLoader {
	public static IConfigurationElement[] getConfigurationElements(String extensionId)
			throws NoExtensionFoundException {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] config = registry
				.getConfigurationElementsFor(extensionId);
		
		if(config.length == 0) {
			throw new NoExtensionFoundException(extensionId);
		}
		return config;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Set<T> getExtensions(String extensionId, String attributeId) throws NoExtensionFoundException {
		IConfigurationElement[] extensions;
		try {
			extensions = ExtensionLoader.getConfigurationElements(extensionId);
		} catch (NoExtensionFoundException e) {
			throw e;
		}

		Set<T> extensionList = new HashSet<>();
		for (IConfigurationElement ext : extensions) {
			try {
				final Object o = ext.createExecutableExtension(attributeId);

				T extension = (T) o;
				extensionList.add(extension);
			} catch (Throwable e) {
				ErrorHandler.handleException(e);
				continue;
			}
		}

		
		return (extensionList.isEmpty()) ? null : extensionList;
	}
}
