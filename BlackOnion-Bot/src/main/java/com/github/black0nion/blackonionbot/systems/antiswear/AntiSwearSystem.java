package com.github.black0nion.blackonionbot.systems.antiswear;

import static com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearType.DELETE;
import static com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearType.OFF;
import static com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearType.REMOVE;

import java.io.File;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;

public class AntiSwearSystem {
	
	public static int profanityFilteredLastTenSecs = 0;
	
	public static final File file = new File("resources/logo.png");
	
	public static boolean check(BlackGuild guild, BlackMember author, BlackMessage message, TextChannel channel) {
		final String messageContent = message.getContentRaw();
		final BlackUser user = author.getBlackUser();
		if (user.isBot()) return false;
		final AntiSwearType type = guild.getAntiSwearType();
		if (guild.isPremium()) {
			if (type == OFF)
				return false;
		} else {
			if (type != OFF)
				guild.setAntiSwearType(OFF);
			return false;
		}
		
		if (Utils.handleRights(guild, user, channel, Permission.MESSAGE_MANAGE)) return false;
			
		try {
			if (messageContent.equalsIgnoreCase("")) return false;
			// check for whitelist
			final List<String> whitelist = guild.getList("whitelist", String.class);
			if (whitelist != null && (whitelist.contains(channel.getAsMention()) || author.getRoles().stream().anyMatch(role -> whitelist.contains(role.getAsMention())))) return false;
			//Message messageRaw = event.getMessage();
			Unirest.setTimeouts(0, 0);
			HttpResponse<String> response = Unirest.post("https://westeurope.api.cognitive.microsoft.com/contentmoderator/moderate/v1.0/ProcessText/Screen?autocorrect=false&classify=True")
			  .header("Content-Type", "text/plain")
			  .header("Ocp-Apim-Subscription-Key", Bot.getCredentialsManager().getString("content_moderator_key"))
			  .body(messageContent)
			  .asString();
	
			JSONObject responseJson = new JSONObject(response.getBody());
			// check for profanity
			if (responseJson.has("Terms")) {
				// this will happen if it doesn't contain any profanity
				if (!(responseJson.get("Terms") instanceof JSONArray)) return false;
				profanityFilteredLastTenSecs++;
				try {
					message.delete().queue();
					
					// if shit fuck it here
					if (type == DELETE) return true;
					
					if (type == REMOVE) {
						
						if (Utils.handleRights(guild, user, channel, Permission.MANAGE_WEBHOOKS)) return true;
						
						WebhookMessageBuilder builder = new WebhookMessageBuilder();
						final JSONArray terms = responseJson.getJSONArray("Terms");
						
						String newMessage = messageContent;
						
						for (int i = 0; i < terms.length(); i++) {
							final String term = terms.getJSONObject(i).getString("Term");
							newMessage = newMessage.replaceAll("(?i)" + term, term.replaceAll(".", "*"));
						}
						
						newMessage = Utils.removeMarkdown(newMessage);
						builder.setContent(newMessage);
						builder.setUsername(author.getEffectiveName() + "#" + user.getDiscriminator());
						builder.setAvatarUrl(user.getEffectiveAvatarUrl());
						channel.retrieveWebhooks().queue(webhooks -> {
							try {
								Webhook webhook;
								
								if (webhooks.stream().anyMatch(tempWebhook -> {if (tempWebhook == null) return false; else return (tempWebhook.getOwner().getIdLong() == BotInformation.botId);})) {
									webhook = webhooks.stream().filter(tempWebhook -> {return tempWebhook.getOwner().getIdLong() == BotInformation.botId;}).findFirst().get();
								} else {
									webhook = channel.createWebhook("BlackOnion-Bot ContentModerator").setAvatar(Icon.from(file)).submit().join();
								}
								
								WebhookClientBuilder clientBuilder = new WebhookClientBuilder(webhook.getUrl());
								clientBuilder.setThreadFactory((job) -> {
									Thread thread = new Thread(job);
									thread.setName("ContentModerator");
									thread.setDaemon(true);
									return thread;
								});
								
								WebhookClient client = clientBuilder.build();
								client.send(builder.build());
								client.close();
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						});
					} else {
						channel.sendMessage(EmbedUtils.getErrorEmbed(user, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			} else throw new RuntimeException("Some error happened while contacting the Microsoft API. Response: \n" + response.getBody());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}