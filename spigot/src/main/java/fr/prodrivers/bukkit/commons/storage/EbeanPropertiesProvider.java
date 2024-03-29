package fr.prodrivers.bukkit.commons.storage;

import fr.prodrivers.bukkit.commons.plugin.EConfiguration;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Properties;

@Singleton
public class EbeanPropertiesProvider implements Provider<Properties> {
	private final EConfiguration configuration;

	@Inject
	public EbeanPropertiesProvider(EConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public Properties get() {
		return this.configuration.storage_ebean;
	}
}
