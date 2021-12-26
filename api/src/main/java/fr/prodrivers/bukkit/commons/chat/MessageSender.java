package fr.prodrivers.bukkit.commons.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * Message sender helper for Prodrivers plugins.
 * <p>
 * Expose simple functions to send messages to players.
 * Handle colors directly. A prefix must be given as parameter.
 * Used as backend for Prodrivers Commons' chat facility.
 *
 * Used implementation can be changed in configuration.
 */
public interface MessageSender {
	/**
	 * Generic message sender
	 *
	 * @param receiver Receiving entity
	 * @param message  Message to send
	 * @param prefix   Prefix to use
	 */
	void send(CommandSender receiver, String message, String prefix);

	/**
	 * Generic message receiver, using a defined prefix
	 *
	 * @param receiver Receiving entity
	 * @param message  Message to send
	 * @param prefix   Prefix to use
	 */
	void send(CommandSender receiver, BaseComponent[] message, String prefix);

	/**
	 * Generic message receiver, that potentially supports non-local players, using a defined prefix
	 *
	 * @param receiverPlayerUniqueId Receiving player
	 * @param message                Message to send
	 * @param prefix                 Prefix to use
	 */
	void send(UUID receiverPlayerUniqueId, String message, String prefix);

	/**
	 * Generic message receiver, that potentially supports non-local players, using a defined prefix
	 *
	 * @param receiverPlayerUniqueId Receiving player
	 * @param message                Message to send
	 * @param prefix                 Prefix to use
	 */
	void send(UUID receiverPlayerUniqueId, BaseComponent[] message, String prefix);
}
