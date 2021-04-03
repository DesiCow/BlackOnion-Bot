package com.github.black0nion.blackonionbot.systems.youtubesubscriptions;

import java.net.URI;

import org.diretto.util.push.NotificationCallback;
import org.diretto.util.push.Subscriber;
import org.diretto.util.push.Subscription;
import org.diretto.util.push.impl.SubscriberImpl;

import com.sun.syndication.feed.synd.SyndFeed;

public class YTSubscriptions {
	public static void main(String[] args) {
		Subscriber subscriber = new SubscriberImpl("subscriber-host", 8888);
		Subscription subscription = subscriber.subscribe(URI.create("https://www.youtube.com/xml/feeds/videos.xml?channel_id=UCdpQ3DkuzQIUrPgC9vdpn4A"));

		subscription.setNotificationCallback(new NotificationCallback()
		{

			@Override
			public void handle(SyndFeed feed)
			{
				System.out.println(feed);
			}
		} );
	}
}
