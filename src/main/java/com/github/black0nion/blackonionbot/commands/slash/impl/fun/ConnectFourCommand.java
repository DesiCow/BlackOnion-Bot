package com.github.black0nion.blackonionbot.commands.slash.impl.fun;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.systems.games.connectfour.ConnectFour;
import com.github.black0nion.blackonionbot.systems.games.connectfour.ConnectFourGameManager;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.github.black0nion.blackonionbot.systems.language.LanguageSystem.getTranslation;

public class ConnectFourCommand extends SlashCommand {
	private static final String USER = "user";

	public ConnectFourCommand() {

		super(builder(Commands.slash("connect4", "Use this command to play Connect 4 with another user.")
			.addOption(OptionType.USER, USER, "The user you want to challenge", true)));
	}

	@Override
	public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		var challenged = BlackUser.from(Objects.requireNonNull(e.getOption(USER, OptionMapping::getAsUser)));
		if (challenged.isBot() || challenged.getIdLong() == author.getIdLong()) {
			e.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("errorcantplayagainst", author, guild).replace("%enemy%", (challenged.isBot() ? getTranslation("bot", author, guild) : getTranslation("yourself", author, guild))), getTranslation("nofriends", author, guild), false).build()).queue();
			return;
		}
		if (ConnectFourGameManager.isIngame(author.getId()) || ConnectFourGameManager.isIngame(challenged.getId())) {
			e.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("alreadyingame", author, guild), getTranslation("nomultitasking", author, guild), false).build()).queue();
			return;
		}
		e.reply(getTranslation("c4_askforaccept", author, guild).replace("%challenged%", challenged.getAsMention()).replace("%challenger%", author.getAsMention()) + " " + getTranslation("answerwithyes", author, guild)).queue();
		Bot.getInstance().getEventWaiter().waitForEvent(MessageReceivedEvent.class, event -> event.getChannel().getIdLong() == channel.getIdLong() && event.getAuthor().getIdLong() == challenged.getIdLong(), event -> {
			final BlackUser answerUser = BlackUser.from(event.getAuthor());
			if (!answerUser.isBot() && answerUser.getId().equals(challenged.getId())) {
				if (event.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
					e.replyEmbeds(EmbedUtils.getSuccessEmbed(answerUser, guild).addField(getTranslation("challengeaccepted", answerUser, guild), getTranslation("playingagainst", answerUser, guild).replace("%challenger%", author.getEscapedName()), false).build()).queue();

					final ConnectFour game = ConnectFourGameManager.createGame(channel, author, challenged);
					this.rerun(game, cmde);
				} else {
					isDeclined(e, guild, event, answerUser, getTranslation("declined", answerUser, guild), getTranslation("challengedeclined", answerUser, guild), getTranslation("arentyoubraveenough", answerUser, guild), getTranslation("answerwithyes", answerUser, guild));
				}
			}
		}, 1, TimeUnit.MINUTES, () -> e.getHook().sendMessageEmbeds(EmbedUtils.getErrorEmbed(challenged, guild).addField(getTranslation("timeout", challenged, guild), getTranslation("tooktoolong", author, guild), false).build()).queue());
	}

	static void isDeclined(SlashCommandInteractionEvent e, BlackGuild guild, @NotNull MessageReceivedEvent event, BlackUser answerUser, String declined, String challengedeclined, String arentyoubraveenough, String answerwithyes) {
		if (event.getMessage().getContentRaw().equalsIgnoreCase("no")) {
			e.replyEmbeds(EmbedUtils.getErrorEmbed(answerUser, guild).setTitle(declined).addField(challengedeclined, arentyoubraveenough, false).build()).queue();
		} else {
			e.replyEmbeds(EmbedUtils.getErrorEmbed(answerUser, guild).addField(challengedeclined, answerwithyes, false).build()).queue();
		}
	}

	public void rerun(final ConnectFour game, final SlashCommandEvent cmde) {
		Bot.getInstance().getEventWaiter().waitForEvent(MessageReceivedEvent.class, answerEvent -> game.isPlayer(answerEvent.getAuthor().getId()), answerEvent -> {
			final String msg = answerEvent.getMessage().getContentRaw();
			final BlackUser author = BlackUser.from(answerEvent.getAuthor());
			final BlackGuild guild = BlackGuild.from(answerEvent.getGuild());
			answerEvent.getMessage().delete().queue();
			if (Utils.equalsOneIgnoreCase(msg, "exit", "stop", "cancel", "leave")) {
				game.getMessage().editMessageEmbeds(EmbedUtils.getSuccessEmbed(author, guild).setTitle(cmde.getTranslation("gaveup")).addField(cmde.getTranslation("usergaveup").replace("%user%", author.getAsMention()), cmde.getTranslation("sadloose"), false).build()).queue();
				ConnectFourGameManager.deleteGame(game);
				return;
			} else if (!author.equals(game.currentUser == FieldType.X ? game.getPlayerX() : game.getPlayerY())) {
				game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed(author, guild).setTitle(cmde.getTranslation("connectfour") + " | " + cmde.getTranslation("currentplayer") + " " + Utils.escapeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(cmde.getTranslation("currentstate"), game.getFieldString(), false).setDescription(cmde.getTranslation("wrongturn")).build()).queue();
				this.rerun(game, cmde);
				return;
			} else if (game.isValidInput(msg)) {
				final Point coords = game.getCoordinatesFromString(msg);
				final FieldType[][] temp = new FieldType[ConnectFourGameManager.HEIGHT][ConnectFourGameManager.WIDTH];
				System.arraycopy(game.getField(), 0, temp, 0, game.getField().length);
				if (temp[coords.x][coords.y] != FieldType.EMPTY) {
					game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed(author, guild).setTitle(cmde.getTranslation("connectfour") + " | " + cmde.getTranslation("currentplayer") + " " + Utils.escapeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(cmde.getTranslation("currentstate"), game.getFieldString(), false).setDescription(cmde.getTranslation("fieldoccopied")).build()).queue();
					game.nextUser();
					this.rerun(game, cmde);
					return;
				}

				temp[coords.x][coords.y] = game.currentUser;
				game.setField(temp);
				game.nextUser();
				game.getMessage().editMessageEmbeds(EmbedUtils.getSuccessEmbed(author, guild).setTitle(cmde.getTranslation("connectfour") + " | " + cmde.getTranslation("currentplayer") + " " + Utils.escapeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(cmde.getTranslation("currentstate"), game.getFieldString(), false).build()).queue();

				if (game.getWinner() != FieldType.EMPTY) {
					game.getMessage().editMessageEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField("WE HAVE A WINNER!", "And the winner is....\n" + Utils.escapeMarkdown((game.getWinner() == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName())) + "!", false).build()).queue();
					ConnectFourGameManager.deleteGame(game);
					return;
				}
			} else {
				game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed(author, guild).setTitle(cmde.getTranslation("connectfour") + " | " + cmde.getTranslation("currentplayer") + " " + Utils.escapeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(cmde.getTranslation("currentstate"), game.getFieldString(), false).setDescription(cmde.getTranslation("wronginput")).build()).queue();
			}
			this.rerun(game, cmde);
		}, 1, TimeUnit.MINUTES, () -> {
			Language lang = Optional.ofNullable(cmde.getGuild().getLanguage()).orElseGet(LanguageSystem::getDefaultLanguage);
			game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed(null, cmde.getGuild()).addField(lang.getTranslation("timeout"), lang.getTranslation("tooktoolong"), false).build()).queue();
			ConnectFourGameManager.deleteGame(game);
		});
	}
}
