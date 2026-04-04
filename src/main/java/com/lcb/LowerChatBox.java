package com.lcb;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ResizeableChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Lower Chat Box",
	description = "On resizable mode, lowers chat box and hides chat buttons.",
	tags = {"chat", "lower", "hide", "button"}
)
public class LowerChatBox extends Plugin
{
	@Inject
	private Client client;

	@Inject
	ClientThread clientThread;

	public static final int CHAT_ID = 162;
	public static final int CHAT_CHILD_ID = 0;

	public static final int CHAT_LOWER_DISTANCE = 23;

	@Override
	protected void startUp() {
		if(client.isResized()) toggleLowerChatBox(true);
	}

	@Override
	protected void shutDown() {
		if(client.isResized()) toggleLowerChatBox(false);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event) {
		if (client.isResized() && event.getGroupId() == CHAT_ID) toggleLowerChatBox(true);
	}

	@Subscribe
	public void onResizeableChanged(ResizeableChanged event) {
		toggleLowerChatBox(event.isResized());
	}

	private void toggleLowerChatBox(Boolean lower){
		Widget chat = client.getWidget(CHAT_ID, CHAT_CHILD_ID);

		if(chat != null) {
			log.debug("Chat box " + (lower ? "Lowered!" : "Raised!"));

			int y;

			if(lower)	y = CHAT_LOWER_DISTANCE;
			else		y = 0;

			clientThread.invoke(() -> {
				chat.setOriginalY(y);
				chat.setPos(chat.getRelativeX(), y);
				chat.revalidate();
			});
		}
	}
}
