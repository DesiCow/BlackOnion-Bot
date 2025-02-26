package com.github.black0nion.blackonionbot.wrappers.jda.impls;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackWrapper;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.utils.ImageProxy;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class MemberImpl extends BlackWrapper implements Member {

	protected final Member member;

	protected MemberImpl(Member member) {
		this.member = member;
	}

	@Override
	public String getDebugMessage() {
		return member.getNickname() + "#" + member.getUser().getDiscriminator() + "(" + member.getUser().getName() + ")(M:" + member.getId() + ")";
	}

	@Override
	@Nonnull
	public User getUser() {
		return this.member.getUser();
	}

	@Override
	@Nonnull
	public Guild getGuild() {
		return this.member.getGuild();
	}

	@Nonnull
	@Override
	public EnumSet<Permission> getPermissions() {
		return this.member.getPermissions();
	}

	@Nonnull
	@Override
	public EnumSet<Permission> getPermissions(@Nonnull GuildChannel channel) {
		return this.member.getPermissions(channel);
	}

	@Nonnull
	@Override
	public EnumSet<Permission> getPermissionsExplicit() {
		return this.member.getPermissionsExplicit();
	}

	@Nonnull
	@Override
	public EnumSet<Permission> getPermissionsExplicit(@Nonnull GuildChannel channel) {
		return this.member.getPermissionsExplicit(channel);
	}

	@Override
	public boolean hasPermission(@Nonnull Permission... permissions) {
		return this.member.hasPermission(permissions);
	}

	@Override
	public boolean hasPermission(@Nonnull Collection<Permission> permissions) {
		return this.member.hasPermission(permissions);
	}

	@Override
	public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Permission... permissions) {
		return this.member.hasPermission(channel, permissions);
	}

	@Override
	public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Collection<Permission> permissions) {
		return this.member.hasPermission(channel, permissions);
	}

	@Override
	public boolean canSync(@Nonnull IPermissionContainer targetChannel, @Nonnull IPermissionContainer syncSource) {
		return this.member.canSync(targetChannel, syncSource);
	}

	@Override
	public boolean canSync(@Nonnull IPermissionContainer channel) {
		return this.member.canSync(channel);
	}

	@Override
	@Nonnull
	public JDA getJDA() {
		return this.member.getJDA();
	}

	@Override
	@Nonnull
	public OffsetDateTime getTimeJoined() {
		return this.member.getTimeJoined();
	}

	@Override
	public boolean hasTimeJoined() {
		return this.member.hasTimeJoined();
	}

	@Override
	@Nullable
	public OffsetDateTime getTimeBoosted() {
		return this.member.getTimeBoosted();
	}

	@Override
	public boolean isBoosting() {
		return this.member.isBoosting();
	}

	@Override
	@Nullable
	public OffsetDateTime getTimeOutEnd() {
		return this.member.getTimeOutEnd();
	}

	@Override
	public boolean isTimedOut() {
		return this.member.isTimedOut();
	}

	@Override
	@Nullable
	public GuildVoiceState getVoiceState() {
		return this.member.getVoiceState();
	}

	@Override
	@Nonnull
	public List<Activity> getActivities() {
		return this.member.getActivities();
	}

	@Override
	@Nonnull
	public OnlineStatus getOnlineStatus() {
		return this.member.getOnlineStatus();
	}

	@Override
	@Nonnull
	public OnlineStatus getOnlineStatus(@Nonnull ClientType type) {
		return this.member.getOnlineStatus(type);
	}

	@Override
	@Nonnull
	public EnumSet<ClientType> getActiveClients() {
		return this.member.getActiveClients();
	}

	@Override
	@Nullable
	public String getNickname() {
		return this.member.getNickname();
	}

	@Override
	@Nonnull
	public String getEffectiveName() {
		return this.member.getEffectiveName();
	}

	@Override
	@Nullable
	public String getAvatarId() {
		return this.member.getAvatarId();
	}

	@Override
	@Nullable
	public String getAvatarUrl() {
		return this.member.getAvatarUrl();
	}

	@Override
	@Nullable
	public ImageProxy getAvatar() {
		return this.member.getAvatar();
	}

	@Override
	@Nonnull
	public String getEffectiveAvatarUrl() {
		return this.member.getEffectiveAvatarUrl();
	}

	@Override
	@Nonnull
	public ImageProxy getEffectiveAvatar() {
		return this.member.getEffectiveAvatar();
	}

	@Override
	@Nonnull
	public List<Role> getRoles() {
		return this.member.getRoles();
	}

	@Override
	@Nullable
	public Color getColor() {
		return this.member.getColor();
	}

	@Override
	public int getColorRaw() {
		return this.member.getColorRaw();
	}

	@Override
	public boolean canInteract(@Nonnull Member member) {
		return this.member.canInteract(member);
	}

	@Override
	public boolean canInteract(@Nonnull Role role) {
		return this.member.canInteract(role);
	}

	@Override
	public boolean canInteract(@Nonnull RichCustomEmoji emoji) {
		return this.member.canInteract(emoji);
	}

	@Override
	public boolean isOwner() {
		return this.member.isOwner();
	}

	@Override
	@Incubating
	public boolean isPending() {
		return this.member.isPending();
	}

	@Override
	@Nullable
	public DefaultGuildChannelUnion getDefaultChannel() {
		return this.member.getDefaultChannel();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> kick() {
		return this.member.kick();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	@Deprecated
	public AuditableRestAction<Void> kick(@Nullable String reason) {
		return this.member.kick(reason);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutFor(long amount, @Nonnull TimeUnit unit) {
		return this.member.timeoutFor(amount, unit);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutFor(@Nonnull Duration duration) {
		return this.member.timeoutFor(duration);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> timeoutUntil(@Nullable TemporalAccessor temporal) {
		return this.member.timeoutUntil(temporal);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> removeTimeout() {
		return this.member.removeTimeout();
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> mute(boolean mute) {
		return this.member.mute(mute);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> deafen(boolean deafen) {
		return this.member.deafen(deafen);
	}

	@Override
	@CheckReturnValue
	@Nonnull
	public AuditableRestAction<Void> modifyNickname(@Nullable String nickname) {
		return this.member.modifyNickname(nickname);
	}

	@Nonnull
	@Override
	public String getAsMention() {
		return this.member.getAsMention();
	}

	@Override
	public long getIdLong() {
		return this.member.getIdLong();
	}
}
