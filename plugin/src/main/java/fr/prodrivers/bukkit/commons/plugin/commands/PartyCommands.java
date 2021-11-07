package fr.prodrivers.bukkit.commons.plugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Optional;
import fr.prodrivers.bukkit.commons.chat.Chat;
import fr.prodrivers.bukkit.commons.Log;
import fr.prodrivers.bukkit.commons.exceptions.PartyCannotInviteYourselfException;
import fr.prodrivers.bukkit.commons.exceptions.PlayerNotInvitedToParty;
import fr.prodrivers.bukkit.commons.parties.Party;
import fr.prodrivers.bukkit.commons.parties.PartyManager;
import fr.prodrivers.bukkit.commons.plugin.EMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
@CommandAlias("party")
public class PartyCommands extends BaseCommand {
	private static final String label = "party";
	private static final String permission = "pcommons.party";

	private final EMessages messages;
	private final Chat chat;
	private final PartyManager partyManager;

	@Inject
	PartyCommands(JavaPlugin plugin, Chat chat, EMessages messages, PartyManager partyManager) {
		this.chat = chat;
		this.messages = messages;
		this.partyManager = partyManager;
	}

	@Default
	@CommandCompletion("@nothing")
	public void partyDefault(final Player player, @Optional final String[] args) {
		if(args != null && args.length > 0) {
			if(args.length == 1 && Bukkit.getPlayer(args[0]) != null) {
				if(player.hasPermission("pcommons.party.invite")) {
					partyInvite(player, Objects.requireNonNull(Bukkit.getPlayer(args[0])));
				} else {
					this.chat.error(player, this.messages.permission_denied);
				}
			} else {
				if(player.hasPermission("pcommons.party.chat")) {
					partyChat(player, args);
				} else {
					this.chat.error(player, this.messages.permission_denied);
				}
			}
		} else {
			if(player.hasPermission("pcommons.party.help")) {
				partyHelp(player);
			}
		}
	}

	@HelpCommand
	@CommandPermission("pcommons.party.help")
	public void partyHelp(final Player player) {
		Map<String, String> cmdpartydesc = this.messages.party_help;
		this.chat.send(player, this.messages.party_help_heading, this.messages.party_prefix);
		for(final String k : cmdpartydesc.keySet()) {
			if(k.length() < 3) {
				this.chat.send(player, "", this.messages.party_prefix);
				continue;
			}
			final String v = cmdpartydesc.get(k);
			this.chat.send(player, ChatColor.YELLOW + "/" + label + " " + k + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + v, this.messages.party_prefix);
		}
	}

	@Subcommand("invite")
	@CommandPermission("pcommons.party.invite")
	@CommandCompletion("@others")
	private void partyInvite(final Player inviter, final OfflinePlayer invited) {
		try {
			this.partyManager.invite(inviter.getUniqueId(), invited.getUniqueId());
		} catch(PartyCannotInviteYourselfException e) {
			this.chat.error(inviter, this.messages.party_cannot_invite_yourself, this.messages.party_prefix);
		} catch(IllegalArgumentException e) {
			this.chat.error(inviter, this.messages.party_player_not_online.formatted(invited.getName()), this.messages.party_prefix);
		}
	}

	@Subcommand("accept")
	@CommandPermission("pcommons.party.accept")
	@CommandCompletion("@partyinviters")
	private void partyAccept(final Player invited, final OfflinePlayer inviter) {
		try {
			this.partyManager.accept(inviter.getUniqueId(), invited.getUniqueId());
		} catch(IllegalArgumentException e) {
			this.chat.error(invited, this.messages.party_player_not_online.formatted(inviter.getName()), this.messages.party_prefix);
		} catch(PlayerNotInvitedToParty e) {
			this.chat.error(invited, this.messages.party_not_invited_to_players_party.formatted(inviter.getName()), this.messages.party_prefix);
		}
	}

	@Subcommand("kick")
	@CommandPermission("pcommons.party.kick")
	@CommandCompletion("@partyothers")
	private void partyKick(final Player player, final OfflinePlayer target) {
		if(!target.isOnline()) {
			this.chat.error(player, this.messages.party_player_not_online.formatted(target.getName()), this.messages.party_prefix);
			return;
		}

		final Party party = this.partyManager.getParty(player.getUniqueId());
		if(party != null && party.isPartyOwner(player.getUniqueId())) {
			if(party.containsPlayer(target.getUniqueId())) {
				partyManager.removeFromParty(target.getUniqueId());
			} else {
				this.chat.error(player, this.messages.party_player_not_in_party.formatted(target.getName()), this.messages.party_prefix);
			}
		} else {
			this.chat.send(player, this.messages.party_you_are_not_a_party_owner, this.messages.party_prefix);
		}
	}

	@Subcommand("list")
	@CommandPermission("pcommons.party.list")
	private void partyList(final Player player) {
		Party party = this.partyManager.getParty(player.getUniqueId());

		if(party != null) {
			this.chat.send(player, this.messages.party_list_heading, this.messages.party_prefix);
			StringBuilder ret = new StringBuilder(ChatColor.DARK_GREEN.toString());
			Player owner = Bukkit.getPlayer(party.getOwnerUniqueId());
			if(owner == null) {
				Log.severe("Owner " + party.getOwnerUniqueId() + "  of party is null on party list command by " + player);
				return;
			}
			ret.append(owner.getName());
			for(final UUID p_ : party.getPlayers()) {
				ret.append(ChatColor.GREEN);
				ret.append(", ");
				Player other = Bukkit.getPlayer(p_);
				if(other == null) {
					Log.severe("Party player " + p_ + "  of party is null on party list command by " + player);
				} else {
					ret.append(other.getName());
				}
			}
			this.chat.send(player, ret.toString(), this.messages.party_prefix);
		} else {
			this.chat.send(player, this.messages.party_you_are_not_in_a_party, this.messages.party_prefix);
		}
	}

	@Subcommand("disband")
	@CommandPermission("pcommons.party.disband")
	private void partyDisband(final Player player) {
		Party party = this.partyManager.getParty(player.getUniqueId());
		if(party != null && party.isPartyOwner(player.getUniqueId())) {
			partyManager.disband(party);
		} else {
			this.chat.send(player, this.messages.party_you_are_not_a_party_owner, this.messages.party_prefix);
		}
	}

	@Subcommand("leave")
	@CommandPermission("pcommons.party.leave")
	private void partyLeave(final Player player) {
		Party party = this.partyManager.getParty(player.getUniqueId());
		if(party == null) {
			this.chat.send(player, this.messages.party_you_are_not_in_a_party, this.messages.party_prefix);
			return;
		}

		partyManager.removeFromParty(player.getUniqueId());
	}

	@Subcommand("owner")
	@CommandPermission("pcommons.party.owner")
	@CommandCompletion("@partyothers")
	private void partySetOwner(final Player player, final Player newOwner) {
		Party party = this.partyManager.getParty(player.getUniqueId());
		if(party == null) {
			this.chat.send(player, this.messages.party_you_are_not_in_a_party, this.messages.party_prefix);
			return;
		}

		if(!party.isPartyOwner(player.getUniqueId())) {
			this.chat.error(player, this.messages.party_player_not_party_owner, this.messages.party_prefix);
			return;
		}

		if(!newOwner.isOnline()) {
			this.chat.error(player, this.messages.party_player_not_online.formatted(newOwner.getName()), this.messages.party_prefix);
			return;
		}

		if(newOwner.getUniqueId() == player.getUniqueId()) {
			this.chat.error(player, this.messages.party_player_cannot_elect_yourself, this.messages.party_prefix);
			return;
		}

		partyManager.assignOwner(party, newOwner.getUniqueId());
	}

	@Subcommand("chat")
	@CommandAlias("p")
	@CommandPermission("pcommons.party.chat")
	@CommandCompletion("@nothing")
	private void partyChat(final Player player, final String[] messages) {
		Party party = this.partyManager.getParty(player.getUniqueId());
		if(party == null) {
			this.chat.send(player, this.messages.party_you_are_not_in_a_party, this.messages.party_prefix);
			return;
		}

		LinkedList<String> msgList = new LinkedList<>(Arrays.asList(messages));
		// Remove two first elements from arguments
		String msg = String.join(" ", msgList);

		party.chatAsPlayer(player, msg);
	}
}
