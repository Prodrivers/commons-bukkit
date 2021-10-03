package fr.prodrivers.bukkit.commons;

import fr.prodrivers.bukkit.commons.configuration.Messages;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Chat {
	private String prefix = "[<name>]";
	private final String name;

	public Chat(String name) {
		this.name = name;
	}

	public void load(Messages messages) {
		if(messages != null && messages.prefix != null) {
			this.prefix = messages.prefix;
		}
		this.prefix = this.prefix.replaceAll("<name>", this.name);
	}

	public void send(CommandSender sender, String message, String prefix) {
		sender.sendMessage(prefix + ChatColor.RESET + " " + message);
	}

	public void send(CommandSender sender, String message) {
		send(sender, message, this.prefix);
	}

	public void send(CommandSender sender, BaseComponent[] components, String prefix) {
		ComponentBuilder builder = new ComponentBuilder("");
		builder.append(TextComponent.fromLegacyText(prefix + ChatColor.RESET + " "));
		builder.append(components);
		sender.spigot().sendMessage(builder.create());
	}

	public void send(CommandSender sender, BaseComponent[] components) {
		send(sender, components, this.prefix);
	}

	public void success(CommandSender sender, String message) {
		send(sender, ChatColor.GREEN + message);
	}

	public void success(CommandSender sender, String message, String prefix) {
		send(sender, ChatColor.GREEN + message, prefix);
	}

	public void success(CommandSender sender, BaseComponent[] components) {
		success(sender, components, this.prefix);
	}

	public void success(CommandSender sender, BaseComponent[] components, String prefix) {
		send(sender, (new ComponentBuilder("")).color(net.md_5.bungee.api.ChatColor.GREEN).append(components).create(), prefix);
	}

	public void error(CommandSender sender, String message) {
		send(sender, ChatColor.RED + message);
	}

	public void error(CommandSender sender, String message, String prefix) {
		send(sender, ChatColor.RED + message, prefix);
	}

	public void error(CommandSender sender, BaseComponent[] components) {
		error(sender, components, this.prefix);
	}

	public void error(CommandSender sender, BaseComponent[] components, String prefix) {
		send(sender, (new ComponentBuilder("")).color(net.md_5.bungee.api.ChatColor.RED).append(components).create(), prefix);
	}

	public String getPrefix() {
		return prefix;
	}
}
