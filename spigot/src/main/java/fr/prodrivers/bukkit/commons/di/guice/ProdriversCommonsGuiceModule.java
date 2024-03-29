package fr.prodrivers.bukkit.commons.di.guice;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import fr.prodrivers.bukkit.commons.chat.MessageSender;
import fr.prodrivers.bukkit.commons.commands.ACFCommandManagerProvider;
import fr.prodrivers.bukkit.commons.parties.PartyManager;
import fr.prodrivers.bukkit.commons.plugin.DependenciesClassLoaderProvider;
import fr.prodrivers.bukkit.commons.sections.SectionManager;
import fr.prodrivers.bukkit.commons.storage.EbeanPropertiesProvider;
import fr.prodrivers.bukkit.commons.storage.EbeanProvider;
import fr.prodrivers.bukkit.commons.ui.section.SelectionUI;
import io.ebean.Database;

import javax.inject.Inject;
import java.util.Properties;

public class ProdriversCommonsGuiceModule extends AbstractModule {
	@Inject
	private PartyManager partyManager;
	@Inject
	private SectionManager sectionManager;
	@Inject
	private MessageSender messageSender;

	@Inject
	private EbeanPropertiesProvider ebeanPropertiesProvider;

	@Inject
	private DependenciesClassLoaderProvider dependenciesClassLoaderProvider;

	@Inject
	private SelectionUI selectionUI;

	@Override
	protected void configure() {
		bind(PartyManager.class).toInstance(partyManager);
		bind(SectionManager.class).toInstance(sectionManager);
		bind(MessageSender.class).toInstance(messageSender);
		bind(SelectionUI.class).toInstance(selectionUI);
		bind(Database.class).toProvider(EbeanProvider.class);
		bind(Properties.class).annotatedWith(Names.named("ebean")).toProvider(ebeanPropertiesProvider);
		bind(DependenciesClassLoaderProvider.class).toInstance(dependenciesClassLoaderProvider);
		bind(BukkitCommandManager.class).toProvider(ACFCommandManagerProvider.class);
	}
}
