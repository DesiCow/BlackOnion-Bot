package com.github.black0nion.blackonionbot.commands.bot;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.enums.CommandVisibility;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AdminHelpCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] {"adminhelp"};
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		message.delete().complete();
		EmbedBuilder builder = EmbedUtils.getErrorEmbed(author, guild)
				.setTitle("Adminhilfe")
				.setColor(Color.getHSBColor(0.8F, 1, 0.5F));
		
		for (Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
			if (entry.getValue().getVisisbility() == CommandVisibility.HIDDEN && entry.getValue().getCommand()[0] != getCommand()[0]) {
				builder.addField(BotInformation.getPrefix(guild) + entry.getKey()[0] + (entry.getValue().getSyntax() != null && !entry.getValue().getSyntax().equalsIgnoreCase("") ? " " + entry.getValue().getSyntax() : ""), "help" + entry.getValue().getCommand()[0].toLowerCase()	, false);
			}
		}
		
		channel.sendMessage(builder.build()).submit().join().delete().queueAfter(10, TimeUnit.SECONDS);
	}
	
	@Override
	public boolean requiresBotAdmin() {
		return true;
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
	
	@Override
	public CommandVisibility getVisisbility() {
		return CommandVisibility.HIDDEN;
	}

}
