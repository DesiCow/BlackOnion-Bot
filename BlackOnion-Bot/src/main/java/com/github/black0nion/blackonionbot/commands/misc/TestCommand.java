package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.blackobjects.BlackEmbed;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.CustomCommand;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TestCommand extends Command {

    public TestCommand() {
	this.setCommand("test", "tet");
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	guild.addCustomCommand(new CustomCommand(guild, "hi", new BlackEmbed().setTitle("hi").addField("servus", "nne", false)));
    }
}