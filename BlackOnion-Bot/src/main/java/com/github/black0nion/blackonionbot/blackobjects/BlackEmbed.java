package com.github.black0nion.blackonionbot.blackobjects;

import java.awt.Color;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class BlackEmbed extends EmbedBuilder {

    private final Language lang;
    private String title = null;
    private String url = null;

    public BlackEmbed(final Language lang) {
	this.lang = lang;
    }

    public BlackEmbed() {
	this.lang = LanguageSystem.getDefaultLanguage();
    }

    public BlackEmbed(final BlackEmbed embed) {
	super(embed);
	this.lang = embed.getLang();
    }

    public Language getLang() {
	return lang;
    }

    @Override
    public BlackEmbed setFooter(String text) {
	final String tempText = lang.getTranslation(text);
	if (tempText != null) {
	    text = tempText;
	}
	super.setFooter(text);
	return this;
    }

    @Override
    public BlackEmbed setTitle(String title) {
	if (title == null) {
	    title = "NULL";
	}
	final String tempTitle = lang.getTranslation(title);
	if (tempTitle != null) {
	    title = tempTitle;
	}
	super.setTitle(title);
	this.title = title;
	return this;
    }

    @Override
    public BlackEmbed setTitle(String title, final String url) {
	if (title == null) {
	    title = "NULL";
	}
	final String tempTitle = lang.getTranslation(title);
	if (tempTitle != null) {
	    title = tempTitle;
	}
	super.setTitle(title, url);
	this.title = title;
	this.url = url;
	return this;
    }

    @Override
    public BlackEmbed addField(String name, String value, final boolean inline) {
	if (name == null) {
	    name = "NULL";
	}
	if (value == null) {
	    value = "NULL";
	}
	final String tempName = lang.getTranslation(name);
	final String tempValue = lang.getTranslation(value);
	if (tempName != null) {
	    name = tempName;
	}
	if (tempValue != null) {
	    value = tempValue;
	}
	super.addField(name, value, inline);
	return this;
    }

    public BlackEmbed addUntranslatedField(String name, String value, final boolean inline) {
	if (name == null) {
	    name = "NULL";
	}
	if (value == null) {
	    value = "NULL";
	}
	super.addField(name, value, inline);
	return this;
    }

    @Override
    public BlackEmbed addBlankField(final boolean inline) {
	super.addBlankField(inline);
	return this;
    }

    @Override
    public BlackEmbed addField(final Field field) {
	super.addField(field);
	return this;
    }

    @Override
    public BlackEmbed appendDescription(final CharSequence description) {
	super.appendDescription(description);
	return this;
    }

    @Override
    public MessageEmbed build() {
	return super.build();
    }

    @Override
    public BlackEmbed clear() {
	super.clear();
	return this;
    }

    @Override
    public BlackEmbed clearFields() {
	super.clearFields();
	return this;
    }

    @Override
    public StringBuilder getDescriptionBuilder() {
	return super.getDescriptionBuilder();
    }

    @Override
    public List<Field> getFields() {
	return super.getFields();
    }

    @Override
    public boolean isEmpty() {
	return super.isEmpty();
    }

    @Override
    public boolean isValidLength() {
	return super.isValidLength();
    }

    @Override
    @Deprecated
    public boolean isValidLength(final AccountType type) {
	return super.isValidLength(type);
    }

    @Override
    public int length() {
	return super.length();
    }

    @Override
    public BlackEmbed setAuthor(final String name) {
	super.setAuthor(name);
	return this;
    }

    @Override
    public BlackEmbed setAuthor(final String name, final String url) {
	super.setAuthor(name, url);
	return this;
    }

    @Override
    public BlackEmbed setAuthor(final String name, final String url, final String iconUrl) {
	super.setAuthor(name, url, iconUrl);
	return this;
    }

    @Override
    public BlackEmbed setColor(final Color color) {
	super.setColor(color);
	return this;
    }

    @Override
    public BlackEmbed setColor(final int color) {
	super.setColor(color);
	return this;
    }

    @Override
    public BlackEmbed setFooter(String text, final String iconUrl) {
	if (text == null) {
	    text = "NULL";
	}
	super.setFooter(text, iconUrl);
	return this;
    }

    @Override
    public BlackEmbed setImage(final String url) {
	super.setImage(url);
	return this;
    }

    @Override
    public BlackEmbed setThumbnail(final String url) {
	super.setThumbnail(url);
	return this;
    }

    @Override
    public BlackEmbed setTimestamp(final TemporalAccessor temporal) {
	super.setTimestamp(temporal);
	return this;
    }

    /**
     * @return the title
     */
    public String getTitle() {
	return title;
    }

    /**
     * @return the url
     */

    public String getUrl() {
	return url;
    }
}