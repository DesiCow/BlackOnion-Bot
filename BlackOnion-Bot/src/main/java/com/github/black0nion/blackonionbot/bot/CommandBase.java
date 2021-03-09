package com.github.black0nion.blackonionbot.bot;

import java.util.HashMap;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.bot.ActivityCommand;
import com.github.black0nion.blackonionbot.commands.bot.AdminHelpCommand;
import com.github.black0nion.blackonionbot.commands.bot.AntiSwearCommand;
import com.github.black0nion.blackonionbot.commands.bot.BanUsageCommand;
import com.github.black0nion.blackonionbot.commands.bot.BugReportCommand;
import com.github.black0nion.blackonionbot.commands.bot.GuildLanguageCommand;
import com.github.black0nion.blackonionbot.commands.bot.HelpCommand;
import com.github.black0nion.blackonionbot.commands.bot.LanguageCommand;
import com.github.black0nion.blackonionbot.commands.bot.NotifyCommand;
import com.github.black0nion.blackonionbot.commands.bot.PingCommand;
import com.github.black0nion.blackonionbot.commands.bot.PrefixCommand;
import com.github.black0nion.blackonionbot.commands.bot.ReloadCommand;
import com.github.black0nion.blackonionbot.commands.bot.ShutdownDBCommand;
import com.github.black0nion.blackonionbot.commands.bot.StatsCommand;
import com.github.black0nion.blackonionbot.commands.bot.StatusCommand;
import com.github.black0nion.blackonionbot.commands.bot.SupportCommand;
import com.github.black0nion.blackonionbot.commands.bot.SwearWhitelistCommand;
import com.github.black0nion.blackonionbot.commands.fun.AvatarCommand;
import com.github.black0nion.blackonionbot.commands.fun.BigbrainMemeCommand;
import com.github.black0nion.blackonionbot.commands.fun.ConnectFourCommand;
import com.github.black0nion.blackonionbot.commands.fun.GiveawayCommand;
import com.github.black0nion.blackonionbot.commands.information.GuildInfoCommand;
import com.github.black0nion.blackonionbot.commands.information.UserInfoCommand;
import com.github.black0nion.blackonionbot.commands.information.WeatherCommand;
import com.github.black0nion.blackonionbot.commands.misc.InstagramCommand;
import com.github.black0nion.blackonionbot.commands.misc.PastebinCommand;
import com.github.black0nion.blackonionbot.commands.misc.TestCommand;
import com.github.black0nion.blackonionbot.commands.misc.VirusCommand;
import com.github.black0nion.blackonionbot.commands.moderation.AutoRolesCommand;
import com.github.black0nion.blackonionbot.commands.moderation.BanCommand;
import com.github.black0nion.blackonionbot.commands.moderation.ClearCommand;
import com.github.black0nion.blackonionbot.commands.moderation.KickCommand;
import com.github.black0nion.blackonionbot.commands.moderation.ReactionRolesSetupCommand;
import com.github.black0nion.blackonionbot.commands.moderation.RenameCommand;
import com.github.black0nion.blackonionbot.commands.moderation.UnbanCommand;
import com.github.black0nion.blackonionbot.commands.moderation.joinleave.SetLeaveChannelCommand;
import com.github.black0nion.blackonionbot.commands.moderation.joinleave.SetWelcomeChannelCommand;
import com.github.black0nion.blackonionbot.commands.music.PlayCommand;
import com.github.black0nion.blackonionbot.commands.music.QueueCommand;
import com.github.black0nion.blackonionbot.commands.music.SkipCommand;
import com.github.black0nion.blackonionbot.commands.music.StopCommand;
import com.github.black0nion.blackonionbot.commands.old.HypixelCommand;
import com.github.black0nion.blackonionbot.enums.CommandVisibility;
import com.github.black0nion.blackonionbot.enums.LogMode;
import com.github.black0nion.blackonionbot.enums.LogOrigin;
import com.github.black0nion.blackonionbot.systems.ContentModeratorSystem;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.FileUtils;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.vdurmont.emoji.EmojiParser;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

public class CommandBase extends ListenerAdapter {
	
	public static HashMap<String[], Command> commands = new HashMap<>();
	
	public static EventWaiter waiter;

	public static int commandsLastTenSecs = 0;
	
	private static final SimilarityStrategy strategy = new JaroWinklerStrategy();
	private static final StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
	
	public static void addCommands(EventWaiter newWaiter) {
		waiter = newWaiter;
		addCommand(new ActivityCommand());
		addCommand(new AvatarCommand());
		addCommand(new ClearCommand());
		addCommand(new HelpCommand());
		addCommand(new NotifyCommand());
		addCommand(new PingCommand());
		addCommand(new ReloadCommand());
		addCommand(new StatusCommand());
		addCommand(new PlayCommand());
		addCommand(new StopCommand());
		addCommand(new QueueCommand());
		addCommand(new ShutdownDBCommand());
		addCommand(new ReactionRolesSetupCommand());
		addCommand(new PastebinCommand());
		addCommand(new HypixelCommand());
		addCommand(new RenameCommand());
		addCommand(new StatsCommand());
		addCommand(new WeatherCommand());
		addCommand(new InstagramCommand());
		addCommand(new AdminHelpCommand());
		addCommand(new TestCommand());
		addCommand(new ConnectFourCommand(waiter));
		addCommand(new SupportCommand());
		addCommand(new LanguageCommand());
		addCommand(new KickCommand());
		addCommand(new BanCommand());
		addCommand(new UnbanCommand());
		addCommand(new GuildLanguageCommand());
		addCommand(new BigbrainMemeCommand());
		addCommand(new GuildInfoCommand());
		addCommand(new UserInfoCommand());
		addCommand(new VirusCommand());
		addCommand(new SkipCommand());
		addCommand(new AutoRolesCommand());
		addCommand(new SetWelcomeChannelCommand());
		addCommand(new SetLeaveChannelCommand());
		addCommand(new PrefixCommand());
		addCommand(new GiveawayCommand());
		addCommand(new AntiSwearCommand());
		addCommand(new BugReportCommand());
		addCommand(new SwearWhitelistCommand());
		addCommand(new BanUsageCommand());
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		final User author = event.getAuthor();
		if (author.isBot()) return;
		
		final Guild guild = event.getGuild();
		final String prefix = BotInformation.getPrefix(guild);
		final TextChannel channel = event.getChannel();
		final Member member = event.getMember();
		final Message message = event.getMessage();
		final String msgContent = message.getContentRaw();
		final String log = EmojiParser.parseToAliases(guild.getName() + "(G:" + guild.getId() + ") > " + channel.getName() + "(C:" + channel.getId() + ") | " + author.getName() + "#" + author.getDiscriminator() + "(U:" + author.getId() + "): " + msgContent.replace("\n", "\\n"));
		final String[] args = msgContent.split(" ");
		
		Logger.log(LogMode.INFORMATION, LogOrigin.BOT, log);
		
		if (!args[0].startsWith(BotInformation.getPrefix(guild))) return;

		final boolean containsProfanity = ContentModeratorSystem.checkMessageForProfanity(event);
		
		String possibleCommand = null;
		double lastScore = 0;
		
		for (String[] c : commands.keySet()) {
			for (String str : c) {
				final double tempScore = service.score(args[0], prefix + str);
				if (tempScore > 0.70 && lastScore < tempScore) {
					lastScore = tempScore;
					possibleCommand = prefix + str;
				}
				if (args[0].equalsIgnoreCase(prefix + str) || tempScore > 0.8) {
					args[0] = possibleCommand;
					FileUtils.appendToFile("commandLog", log);
					ValueManager.save("commandsExecuted", ValueManager.getInt("commandsExecuted") + 1);
					commandsLastTenSecs++;
					Command cmd = commands.get(c);
					if (cmd.requiresBotAdmin() && !BotSecrets.isAdmin(author.getIdLong())) {
						continue;
					} else if (cmd.getRequiredPermissions() != null && !member.hasPermission(cmd.getRequiredPermissions())) {
						if (cmd.getVisisbility() != CommandVisibility.SHOWN)
							continue;
						channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild)
								.addField(LanguageSystem.getTranslatedString("missingpermissions", author, guild), LanguageSystem.getTranslatedString("requiredpermissions", author, guild) + "\n" + getPermissionString(cmd.getRequiredPermissions()), false).build()).queue();
						continue;
					} else if (cmd.getRequiredArgumentCount() + 1 > args.length) {
						channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild)
								.addField(LanguageSystem.getTranslatedString("wrongargumentcount", author, guild), "Syntax: " + prefix + str + (cmd.getSyntax().equals("") ? "" : " " + cmd.getSyntax()), false).build()).queue();
						continue;
					}
					
					if (containsProfanity) {
						channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("dontexecuteprofanitycommands", "pleaseremoveprofanity", false).build()).queue();
						continue;
					}
					Bot.executor.submit(() -> {
						cmd.execute(args, event, message, member, author, guild, channel);
					});
					return;
				}
			}
		}
		
		channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("commandnotfound", possibleCommand != null ? LanguageSystem.getTranslatedString("didyoumean", author, guild).replace("%command%", possibleCommand) : LanguageSystem.getTranslatedString("thecommandnotfound", author, guild).replace("%command%", args[0]), false).build()).queue();
	}
	
	public static void addCommand(Command c, String... command) {
		if (!commands.containsKey(command))
			commands.put(command, c);
	}
	
	public static void addCommand(Command c) {
		if (!commands.containsKey(c.getCommand()))
			commands.put(c.getCommand(), c);
	}
	
	public static String getPermissionString(Permission[] permissions) {
		String output = "```";
		for (int i = 0; i  < permissions.length; i++) {
			output += "- " + permissions[i].getName() + (i == permissions.length-1 ? "```" : "\n");
		}
		return output;
	}
}
