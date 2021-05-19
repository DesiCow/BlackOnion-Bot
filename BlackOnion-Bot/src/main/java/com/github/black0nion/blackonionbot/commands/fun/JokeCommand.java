package com.github.black0nion.blackonionbot.commands.fun;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class JokeCommand implements Command {
	
	@Override
	public String[] getCommand() {
		return new String[] { "joke", "jokes" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		try {
			Unirest.setTimeouts(0, 0);
			Language lang = LanguageSystem.getLanguage(author, guild);
			String langString = null;
			if (Utils.equalsOneIgnoreCase(lang.getLanguageCode(), "de", "en", "cs", "es", "fr", "pt"))
				langString = "&lang=" + lang.getLanguageCode().toLowerCase();
			HttpResponse<String> response = Unirest.get("https://v2.jokeapi.dev/joke/Any?blacklistFlags=nsfw,racist,sexist&type=twopart" + (langString != null ? langString : "")).asString();
			JSONObject object = new JSONObject(response.getBody());
			channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).setTitle("Joke", "https://jokeapi.dev").addField(object.getString("setup"), object.getString("delivery"), false).build()).queue();
		} catch (Exception ex) {
			ex.printStackTrace();
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
		}
	}

	@Override
	public Category getCategory() {
		return Category.FUN;
	}
}