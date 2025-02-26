package com.github.black0nion.blackonionbot.systems.dashboard;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Dashboard {

	public static final HashMap<String, Method> setters = new HashMap<>();
	private static final List<Method> nullableSetters = new ArrayList<>();
	public static final HashMap<String, Method> getters = new HashMap<>();

	public static void init() {
		getters.clear();
		setters.clear();

		final Reflections reflections = new Reflections(BlackWrapper.class.getPackage().getName());
		final Set<Class<? extends BlackWrapper>> annotated = reflections.getSubTypesOf(BlackWrapper.class);

		for (final Class<?> blackobject : annotated) {
			try {
				final Class<?> objectClass = Class.forName(blackobject.getName());

				for (final Method method : objectClass.getDeclaredMethods()) {
					if (method.isAnnotationPresent(DashboardSetter.class)) {
						final DashboardSetter annotation = method.getAnnotation(DashboardSetter.class);
						setters.put(annotation.value(), method);
						if (annotation.nullable()) {
							nullableSetters.add(method);
						}
					}
					if (method.isAnnotationPresent(DashboardGetter.class)) {
						final DashboardGetter annotation = method.getAnnotation(DashboardGetter.class);
						getters.put(annotation.value(), method);
					}
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void tryUpdateValue(final JSONObject message, final DiscordUser user, final Consumer<ResponseCode> callback) {
		if (!(message.has("guildid") && message.has("setting") && message.has("values"))) {
			callback.accept(ResponseCode.WRONG_ARGUMENTS);
			return;
		}
		final Method method = setters.get(message.getString("setting"));
		if (method == null) {
			callback.accept(ResponseCode.WRONG_SETTING);
			return;
		}
		final BlackGuild guild = BlackGuild.from(message.getLong("guildid"));
		if (guild == null) {
			callback.accept(ResponseCode.NO_GUILD);
			return;
		}
		guild.retrieveMemberById(user.getUser().getId()).queue(member -> {
			// TODO advanced permissions
			if (!member.hasPermission(Permission.MANAGE_SERVER)) {
				callback.accept(ResponseCode.NO_PERMISSIONS);
				return;
			}
			final List<Object> list = message.getJSONArray("values").toList();
			if (saveValue(guild, method, list.toArray())) {
				callback.accept(ResponseCode.SUCCESS);
			} else {
				callback.accept(ResponseCode.PARSE_ERROR);
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "RedundantCast" })
	public static boolean saveValue(final Object objectToInvokeMethodIn, final Method method, final Object... args) {
		try {
			final Parameter[] parameters = method.getParameters();
			final Object[] parsed = new Object[parameters.length];
			boolean hasArray = false;
			for (int i = 0; i < args.length; i++) {
				final Class<?> parameterType = parameters[i].getType();
				if (parameterType == String.class) {
					parsed[i] = String.valueOf(args[i]);
				} else if (parameterType == boolean.class || parameterType == Boolean.class) {
					parsed[i] = (boolean) args[i];
				} else if (parameterType == TextChannel.class) {
					final Object arg = args[i];
					if (arg instanceof String) {
						parsed[i] = Bot.getInstance().getJDA().getTextChannelById((String) arg);
					} else if (arg instanceof Long) {
						parsed[i] = Bot.getInstance().getJDA().getTextChannelById((Long) arg);
					} else {
						args[i] = null;
					}
				} else if (parameterType == long.class || parameterType == Long.class) {
					parsed[i] = parseLong(args[i]);
				} else if (parameterType == int.class || parameterType == Integer.class) {
					parsed[i] = parseInt(args[i]);
				} else if (parameterType.isEnum()) {
					try {
						final Method parse = parameterType.getDeclaredMethod("parse", String.class);
						parsed[i] = parse.invoke(parameterType, (String) args[i]);
					} catch (final NoSuchMethodException ignored) {
						parsed[i] = Enum.valueOf((Class<? extends Enum>) parameterType, (String) args[i]);
					}
				} else if (parameterType.isArray()) {
					hasArray = true;
					final Class<?> clazz = parameterType.getComponentType();
					if (clazz == String.class) {
						final String[] obj = new String[args.length - i];
						for (int j = i; j < args.length; j++) {
							obj[j - i] = String.valueOf(args[j]);
						}
						parsed[i] = obj;
					} else if (clazz == Long.class) {
						parsed[i] = parseLong(i, args);
					} else if (clazz == long.class) {
						parsed[i] = parseLongPrimitive(i, args);
					} else if (clazz == Integer.class) {
						parsed[i] = parseInteger(i, args);
					} else if (clazz == int.class) {
						parsed[i] = parseIntegerPrimitive(i, args);
					} else if (clazz == Object.class) {
						final Object[] obj = new Object[args.length - i];
						System.arraycopy(args, i, obj, 0, args.length - i);
						parsed[i] = obj;
					} else {
						LoggerFactory.getLogger(Dashboard.class).error("No array casting way provided for " + clazz);
					}
					break;
				} else {
					parsed[i] = getValue(parameterType, args[i]);
				}
				if ((parsed[i] == null || (Utils.isLong(parsed[i]) && (Long) parsed[i] == -1)) && !nullableSetters.contains(method))
					throw new IllegalArgumentException("args[" + i + "] is null, should be of type " + parameterType.getName() + "!");
			}
			if (hasArray) {
				for (int i = 0; i < parsed.length; i++) {
					if ((parsed[i] == null || (Utils.isLong(parsed[i]) && (Long) parsed[i] == -1)) && !nullableSetters.contains(method))
						throw new IllegalArgumentException("args[" + i + "] is null!");
				}
			}
			method.invoke(objectToInvokeMethodIn, parsed);
			return true;
		} catch (final Exception e) {
			if (!(e instanceof IllegalArgumentException)) {
				e.printStackTrace();
			}
			return false;
		}
	}

	public static Integer[] parseInteger(final int i, final Object... args) {
		final Integer[] obj = new Integer[args.length - i];
		for (int j = i; j < args.length; j++) {
			final int index = j - i;
			final Object arg = args[j];
			if (arg == null) {
				obj[index] = null;
			} else if (arg instanceof Integer in) {
				obj[index] = in;
			} else if (arg instanceof Long l) {
				obj[index] = l.intValue();
			} else if (arg instanceof String) {
				if (Utils.isInteger(String.valueOf(arg))) {
					obj[index] = Integer.valueOf(String.valueOf(arg));
				} else {
					obj[index] = null;
				}
			} else {
				obj[index] = null;
			}
		}
		return obj;
	}

	public static int[] parseIntegerPrimitive(final int i, final Object... args) {
		final int[] obj = new int[args.length - i];
		for (int j = i; j < args.length; j++) {
			final int index = j - i;
			final Object arg = args[j];
			if (arg == null) {
				obj[index] = -1;
			} else if (arg instanceof Integer) {
				obj[index] = (int) arg;
			} else if (arg instanceof Long) {
				obj[index] = (int) arg;
			} else if (arg instanceof String) {
				if (Utils.isInteger(String.valueOf(arg))) {
					obj[index] = Integer.parseInt(String.valueOf(arg));
				} else {
					obj[index] = -1;
				}
			} else {
				obj[index] = -1;
			}
		}
		return obj;
	}

	public static long[] parseLongPrimitive(final int i, final Object... args) {
		final long[] obj = new long[args.length - i];
		for (int j = i; j < args.length; j++) {
			final int index = j - i;
			final Object arg = args[j];
			if (arg == null) {
				obj[index] = -1;
			} else if (arg instanceof Integer) {
				obj[index] = ((Integer) arg).longValue();
			} else if (arg instanceof Long) {
				obj[index] = (long) arg;
			} else if (arg instanceof String) {
				if (Utils.isLong(arg)) {
					obj[index] = Long.parseLong(String.valueOf(arg));
				} else {
					obj[index] = -1;
				}
			} else {
				obj[index] = -1;
			}
		}
		return obj;
	}

	public static Long[] parseLong(final int i, final Object... args) {
		final Long[] obj = new Long[args.length - i];
		for (int j = i; j < args.length; j++) {
			final int index = j - i;
			final Object arg = args[j];
			if (arg == null) {
				obj[index] = null;
			} else if (arg instanceof Integer) {
				obj[index] = ((Integer) arg).longValue();
			} else if (arg instanceof Long) {
				obj[index] = (Long) arg;
			} else if (arg instanceof String) {
				if (Utils.isLong(arg)) {
					obj[index] = Long.parseLong(String.valueOf(arg));
				} else {
					obj[index] = null;
				}
			} else {
				obj[index] = null;
			}
		}
		return obj;
	}

	public static <T> T getValue(final Class<T> desiredType, final Object o) {
		if (o.getClass().isAssignableFrom(desiredType)) return desiredType.cast(o);
		else throw new IllegalArgumentException();
	}

	public static long parseLong(final Object obj) {
		try {
			return Long.parseLong((String) obj);
		} catch (final Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public static int parseInt(final Object obj) {
		try {
			return Integer.parseInt((String) obj);
		} catch (final Exception e) {
			throw new IllegalArgumentException();
		}
	}
}
