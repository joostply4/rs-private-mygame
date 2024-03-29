package com.rs.net.encoders;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.DynamicRegion;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Region;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.item.ItemsContainer;
import com.rs.game.npc.NPC;
import com.rs.game.player.HintIcon;
import com.rs.game.player.Player;
import com.rs.game.player.PublicChatMessage;
import com.rs.game.player.QuickChatMessage;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.io.OutputStream;
import com.rs.net.Session;
import com.rs.utils.Logger;
import com.rs.utils.MapContainersXteas;
import com.rs.utils.Utils;
import com.rs.utils.huffman.Huffman;

public class WorldPacketsEncoder extends Encoder {

	private Player player;

	public WorldPacketsEncoder(Session session, Player player) {
		super(session);
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}
	
	

	public void sendPlayerUnderNPCPriority(boolean priority) {
		OutputStream stream = new OutputStream(2);
		stream.writePacket(123);
		stream.write128Byte(priority ? 1 : 0);
		session.write(stream);
	}

	public void sendHintIcon(HintIcon icon) {
		OutputStream stream = new OutputStream(13);
		stream.writePacket(81);
		stream.writeByte((icon.getTargetType() & 0x1f) | (icon.getIndex() << 5));
		if (icon.getTargetType() == 0)
			stream.skip(11);
		else {
			stream.writeByte(icon.getArrowType());
			if (icon.getTargetType() == 1 || icon.getTargetType() == 10) {
				stream.writeShort(icon.getTargetIndex());
				stream.writeShort(0); // unknown
				stream.skip(4);
			} else if ((icon.getTargetType() >= 2 && icon.getTargetType() <= 6)) { // directions
				stream.writeByte(0); // unknown
				stream.writeShort(icon.getCoordX());
				stream.writeShort(icon.getCoordY());
				stream.writeByte(icon.getDistanceFromFloor() * 4 >> 2);
				stream.writeShort(0); // unknown
			}
			stream.writeShort(icon.getModelId());
		}
		session.write(stream);

	}

	/*
	 * 
	 * 
	 * */
	public void sendCameraShake(int slotId, int b, int c, int d, int e) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(34);
		stream.write128Byte(b);
		stream.writeByte128(slotId);
		stream.writeShortLE128(e);
		stream.write128Byte(c);
		stream.write128Byte(d);
		session.write(stream);
	}

	public void sendStopCameraShake() {
		OutputStream stream = new OutputStream(1);
		stream.writePacket(15);
		session.write(stream);
	}

	public void sendIComponentModel(int interfaceId, int componentId,
			int modelId) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(58);
		stream.writeIntV1(interfaceId << 16 | componentId);
		stream.writeShort128(modelId);
		session.write(stream);
	}

	public void sendScrollIComponent(int interfaceId, int componentId, int value) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(8);
		stream.writeShort128(value);
		stream.writeIntLE(interfaceId << 16 | componentId);
		session.write(stream);
	}

	public void sendHideIComponent(int interfaceId, int componentId,
			boolean hidden) {
		OutputStream stream = new OutputStream(6);
		stream.writePacket(117);
		stream.writeIntV1(interfaceId << 16 | componentId);
		stream.writeByte128(hidden ? 1 : 0);
		session.write(stream);
	}

	public void sendRemoveGroundItem(FloorItem item) {
		sendWorldTile(item.getTile());
		int localX = item.getTile().getLocalX(
				player.getLastLoadedMapRegionTile(), player.getMapSize());
		int localY = item.getTile().getLocalY(
				player.getLastLoadedMapRegionTile(), player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		OutputStream stream = new OutputStream(4);
		stream.writePacket(16);
		stream.writeShort(item.getId());
		stream.writeByte((offsetX << 4) | offsetY);
		session.write(stream);

	}

	public void sendGroundItem(FloorItem item) {
		sendWorldTile(item.getTile());
		int localX = item.getTile().getLocalX(
				player.getLastLoadedMapRegionTile(), player.getMapSize());
		int localY = item.getTile().getLocalY(
				player.getLastLoadedMapRegionTile(), player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		OutputStream stream = new OutputStream(6);
		stream.writePacket(48);
		stream.writeByteC((offsetX << 4) | offsetY);
		stream.writeShort128(item.getId());
		stream.writeShort(item.getAmount());
		session.write(stream);
	}

	public void sendProjectile(Entity receiver, WorldTile startTile,
			WorldTile endTile, int gfxId, int startHeight, int endHeight,
			int speed, int delay, int curve, int startDistanceOffset,
			int creatorSize) {
		sendWorldTile(startTile);
		OutputStream stream = new OutputStream(17);
		stream.writePacket(62);
		int localX = startTile.getLocalX(player.getLastLoadedMapRegionTile(),
				player.getMapSize());
		int localY = startTile.getLocalY(player.getLastLoadedMapRegionTile(),
				player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		stream.writeByte((offsetX << 3) | offsetY);
		stream.writeByte(endTile.getX() - startTile.getX());
		stream.writeByte(endTile.getY() - startTile.getY());
		stream.writeShort(receiver == null ? 0
				: (receiver instanceof Player ? -(receiver.getIndex() + 1)
						: receiver.getIndex() + 1));
		stream.writeShort(gfxId);
		stream.writeByte(startHeight);
		stream.writeByte(endHeight);
		stream.writeShort(delay);
		int duration = (Utils.getDistance(startTile.getX(), startTile.getY(),
				endTile.getX(), endTile.getY()) * 30 / ((speed / 10) < 1 ? 1
				: (speed / 10)))
				+ delay;
		stream.writeShort(duration);
		stream.writeByte(curve);
		stream.writeShort(creatorSize * 64 + startDistanceOffset * 64);
		session.write(stream);

	}

	public void sendUnlockIComponentOptionSlots(int interfaceId,
			int componentId, int fromSlot, int toSlot, int... optionsSlots) {
		int settingsHash = 0;
		for (int slot : optionsSlots)
			settingsHash |= 2 << slot;
		sendIComponentSettings(interfaceId, componentId, fromSlot, toSlot,
				settingsHash);
	}

	public void sendIComponentSettings(int interfaceId, int componentId,
			int fromSlot, int toSlot, int settingsHash) {
		OutputStream stream = new OutputStream(13);
		stream.writePacket(3);
		stream.writeShortLE(fromSlot);
		stream.writeIntV2(interfaceId << 16 | componentId);
		stream.writeShort128(toSlot);
		stream.writeIntLE(settingsHash);
		session.write(stream);
	}

	public void sendInterSetItemsOptionsScript(int interfaceId,
			int componentId, int key, int width, int height, String... options) {
		Object[] parameters = new Object[6 + options.length];
		int index = 0;
		for (int count = options.length - 1; count >= 0; count--)
			parameters[index++] = options[count];
		parameters[index++] = -1; // dunno but always this
		parameters[index++] = 0;// dunno but always this
		parameters[index++] = height;
		parameters[index++] = width;
		parameters[index++] = key;
		parameters[index++] = interfaceId << 16 | componentId;
		sendRunScript(150, parameters); // scriptid 150 does that the method
										// name says*/
	}

	public void sendRunScript(int scriptId, Object... params) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(50);
		String parameterTypes = "";
		if (params != null) {
			for (int count = params.length - 1; count >= 0; count--) {
				if (params[count] instanceof String)
					parameterTypes += "s"; // string
				else
					parameterTypes += "i"; // integer
			}
		}
		stream.writeString(parameterTypes);
		if (params != null) {
			int index = 0;
			for (int count = parameterTypes.length() - 1; count >= 0; count--) {
				if (parameterTypes.charAt(count) == 's')
					stream.writeString((String) params[index++]);
				else
					stream.writeInt((Integer) params[index++]);
			}
		}
		stream.writeInt(scriptId);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendGlobalConfig(int id, int value) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE)
			sendGlobalConfig2(id, value);
		else
			sendGlobalConfig1(id, value);
	}

	public void sendGlobalConfig1(int id, int value) {
		OutputStream stream = new OutputStream(4);
		stream.writePacket(111);
		stream.writeShortLE128(id);
		stream.write128Byte(value);
		session.write(stream);
	}

	public void sendGlobalConfig2(int id, int value) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(112);
		stream.writeShortLE(id);
		stream.writeInt(value);
		session.write(stream);
	}

	public void sendConfig(int id, int value) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE)
			sendConfig2(id, value);
		else
			sendConfig1(id, value);
	}

	public void sendConfigByFile(int fileId, int value) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE)
			sendConfigByFile2(fileId, value);
		else
			sendConfigByFile1(fileId, value);
	}

	public void sendConfig1(int id, int value) {
		OutputStream stream = new OutputStream(4);
		stream.writePacket(101);
		stream.writeShort(id);
		stream.writeByte128(value);
		session.write(stream);
	}

	public void sendConfig2(int id, int value) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(39);
		stream.writeIntV2(value);
		stream.writeShort128(id);
		session.write(stream);
	}

	public void sendConfigByFile1(int fileId, int value) {
		OutputStream stream = new OutputStream(4);
		stream.writePacket(14);
		stream.write128Byte(value);
		stream.writeShort128(fileId);
		session.write(stream);
	}

	public void sendConfigByFile2(int fileId, int value) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(84);
		stream.writeInt(value);
		stream.writeShort(fileId);
		session.write(stream);
	}

	public void sendRunEnergy() {
		OutputStream stream = new OutputStream(2);
		stream.writePacket(13);
		stream.writeByte(player.getRunEnergy());
		session.write(stream);
	}

	public void sendIComponentText(int interfaceId, int componentId, String text) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(33);
		stream.writeInt(interfaceId << 16 | componentId);
		stream.writeString(text);
		stream.endPacketVarShort();
		session.write(stream);

	}

	public void sendIComponentAnimation(int emoteId, int interfaceId,
			int componentId) {

		OutputStream stream = new OutputStream(7);
		stream.writePacket(23);
		stream.writeShortLE128(emoteId);
		stream.writeIntV1(interfaceId << 16 | componentId);
		session.write(stream);

	}

	public void sendItemOnIComponent(int interfaceid, int componentId, int id,
			int amount) {

		OutputStream stream = new OutputStream(11);
		stream.writePacket(9);
		stream.writeShortLE(id);
		stream.writeInt(amount);
		stream.writeIntV2(interfaceid << 16 | componentId);
		session.write(stream);

	}

	public void sendEntityOnIComponent(boolean isPlayer, int entityId,
			int interfaceId, int componentId) {
		if (isPlayer)
			sendPlayerOnIComponent(interfaceId, componentId);
		else
			sendNPCOnIComponent(interfaceId, componentId, entityId);
	}

	public void sendWorldTile(WorldTile tile) {
		OutputStream stream = new OutputStream(3);
		stream.writePacket(46);
		stream.writeByte128(tile.getLocalX(player.getLastLoadedMapRegionTile(),
				player.getMapSize()) >> 3);
		stream.writeByte(tile.getLocalY(player.getLastLoadedMapRegionTile(),
				player.getMapSize()) >> 3);
		stream.write128Byte(tile.getPlane());
		session.write(stream);
	}

	public void sendObjectAnimation(WorldObject object, Animation animation) {
		OutputStream stream = new OutputStream(8);
		stream.writePacket(96);
		stream.writeIntV2(object.get30BitsLocationHash());
		stream.writeShort128(animation.getIds()[0]);
		stream.write128Byte((object.getType() << 2)
				+ (object.getRotation() & 0x3));
		session.write(stream);
	}

	public void sendTileMessage(String message, WorldTile tile, int color) {
		sendTileMessage(message, tile, 5000, 255, color);
	}

	public void sendTileMessage(String message, WorldTile tile, int delay,
			int height, int color) {
		sendWorldTile(tile);
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(32);
		stream.skip(1);
		int localX = tile.getLocalX(player.getLastLoadedMapRegionTile(),
				player.getMapSize());
		int localY = tile.getLocalY(player.getLastLoadedMapRegionTile(),
				player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		stream.writeByte((offsetX << 4) | offsetY);
		stream.writeShort(delay / 30);
		stream.writeByte(height);
		stream.write24BitInteger(color);
		stream.writeString(message);
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void sendSpawnedObject(WorldObject object) {
		sendWorldTile(object);
		int localX = object.getLocalX(player.getLastLoadedMapRegionTile(),
				player.getMapSize());
		int localY = object.getLocalY(player.getLastLoadedMapRegionTile(),
				player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		OutputStream stream = new OutputStream(5);
		stream.writePacket(28);
		stream.writeByte((offsetX << 4) | offsetY); // the hash
													// for
													// coords,
													// useless
		stream.writeByte((object.getType() << 2) + (object.getRotation() & 0x3));
		stream.writeShort128(object.getId());
		session.write(stream);

	}

	public void sendDestroyObject(WorldObject object) {
		sendWorldTile(object);
		int localX = object.getLocalX(player.getLastLoadedMapRegionTile(),
				player.getMapSize());
		int localY = object.getLocalY(player.getLastLoadedMapRegionTile(),
				player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		OutputStream stream = new OutputStream(3);
		stream.writePacket(45);
		stream.writeByteC((offsetX << 4) | offsetY); // the hash for coords,
														// useless
		stream.writeByte((object.getType() << 2) + (object.getRotation() & 0x3));
		session.write(stream);

	}

	public void sendPlayerOnIComponent(int interfaceId, int componentId) {
		OutputStream stream = new OutputStream(5);
		stream.writePacket(114);
		stream.writeIntLE(interfaceId << 16 | componentId);
		session.write(stream);

	}

	public void sendNPCOnIComponent(int interfaceId, int componentId, int npcId) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(98);
		stream.writeInt(interfaceId << 16 | componentId);
		stream.writeShortLE(npcId);
		session.write(stream);

	}

	public void sendRandomOnIComponent(int interfaceId, int componentId, int id) {
		/*
		 * OutputStream stream = new OutputStream(); stream.writePacket(235);
		 * stream.writeShort(id); stream.writeIntV1(interfaceId << 16 |
		 * componentId); stream.writeShort(interPacketsCount++);
		 * session.write(stream);
		 */
	}

	public void sendFaceOnIComponent(int interfaceId, int componentId,
			int look1, int look2, int look3) {
		/*
		 * OutputStream stream = new OutputStream(); stream.writePacket(192);
		 * stream.writeIntV2(interfaceId << 16 | componentId);
		 * stream.writeShortLE128(interPacketsCount++);
		 * stream.writeShortLE128(look1); stream.writeShortLE128(look2);
		 * stream.writeShort128(look2); session.write(stream);
		 */
	}

	public void sendFriendsChatChannel() {
		FriendChatsManager manager = player.getCurrentFriendChat();
		OutputStream stream = new OutputStream(manager == null ? 3
				: manager.getDataBlock().length + 3);
		stream.writePacketVarShort(12);
		if (manager != null)
			stream.writeBytes(manager.getDataBlock());
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendFriend(String username, String displayName, int world,
			boolean putOnline, boolean warnMessage) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(85);
		stream.writeByte(warnMessage ? 0 : 1);
		stream.writeString(displayName);
		stream.writeString(displayName.equals(username) ? "" : username);
		stream.writeShort(putOnline ? world : 0);
		stream.writeByte(player.getFriendsIgnores().getRank(
				Utils.formatPlayerNameForProtocol(username)));
		stream.writeByte(0);
		if (putOnline) {
			stream.writeString(Settings.SERVER_NAME);
			stream.writeByte(0);
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendIgnore(String name, String display) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(75);
		stream.writeByte(0);
		if (display == name)
			name = "";
		stream.writeString(display);
		stream.writeString(name);
		stream.writeString(name);
		stream.writeString(display);
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void sendPrivateMessage(String username, String message) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(77);
		stream.writeString(username);
		Huffman.sendEncryptMessage(stream, message);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendGameBarStages() {
		sendConfig(1054, 0); // clan on
		sendConfig(1055, 0); // assist on
		sendConfig(1056, player.isFilterGame() ? 1 : 0);
		sendConfig(2159, 0); // friends chat on
		OutputStream stream = new OutputStream(3);
		stream.writePacket(72);
		stream.writeByte(0); // public on
		stream.writeByte(0); // trade on
		session.write(stream);
		sendPrivateGameBarStage();
	}

	public void sendPrivateGameBarStage() {
		OutputStream stream2 = new OutputStream(2);
		stream2.writePacket(134);
		stream2.writeByte(player.getFriendsIgnores().getPrivateStatus());
		session.write(stream2);
	}

	public void receivePrivateMessage(String name, String display, int rights,
			String message) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(120);
		stream.writeByte(name.equals(display) ? 0 : 1);
		stream.writeString(display);
		if (!name.equals(display))
			stream.writeString(name);
		for (int i = 0; i < 5; i++)
			stream.writeByte(Utils.getRandom(255));
		stream.writeByte(rights);
		Huffman.sendEncryptMessage(stream, message);
		stream.endPacketVarShort();
		session.write(stream);
	}

	// 131 clan chat quick message

	public void receivePrivateChatQuickMessage(String name, String display,
			int rights, QuickChatMessage message) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(42);
		stream.writeByte(name.equals(display) ? 0 : 1);
		stream.writeString(display);
		if (!name.equals(display))
			stream.writeString(name);
		for (int i = 0; i < 5; i++)
			stream.writeByte(Utils.getRandom(255));
		stream.writeByte(rights);
		stream.writeShort(message.getFileId());
		if (message.getMessage() != null)
			stream.writeBytes(message.getMessage().getBytes());
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void sendPrivateQuickMessageMessage(String username,
			QuickChatMessage message) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(97);
		stream.writeString(username);
		stream.writeShort(message.getFileId());
		if (message.getMessage() != null)
			stream.writeBytes(message.getMessage().getBytes());
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void receiveFriendChatMessage(String name, String display,
			int rights, String chatName, String message) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(40);
		stream.writeByte(name.equals(display) ? 0 : 1);
		stream.writeString(display);
		if (!name.equals(display))
			stream.writeString(name);
		stream.writeLong(Utils.stringToLong(chatName));
		for (int i = 0; i < 5; i++)
			stream.writeByte(Utils.getRandom(255));
		stream.writeByte(rights);
		Huffman.sendEncryptMessage(stream, message);
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void receiveFriendChatQuickMessage(String name, String display,
			int rights, String chatName, QuickChatMessage message) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(20);
		stream.writeByte(name.equals(display) ? 0 : 1);
		stream.writeString(display);
		if (!name.equals(display))
			stream.writeString(name);
		stream.writeLong(Utils.stringToLong(chatName));
		for (int i = 0; i < 5; i++)
			stream.writeByte(Utils.getRandom(255));
		stream.writeByte(rights);
		stream.writeShort(message.getFileId());
		if (message.getMessage() != null)
			stream.writeBytes(message.getMessage().getBytes());
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void sendUnlockIgnoreList() {
		OutputStream stream = new OutputStream(1);
		stream.writePacket(135);
		session.write(stream);
	}

	public void sendUnlockFriendList() {
		OutputStream stream = new OutputStream(1);
		stream.writePacketVarShort(85);
		session.write(stream);
	}

	/*
	 * dynamic map region
	 */
	public void sendDynamicMapRegion(boolean wasAtDynamicRegion) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(128);
		int regionX = player.getChunkX();
		int regionY = player.getChunkY();
		stream.writeShort(regionY);
		stream.writeByte(player.getMapSize());
		stream.write128Byte(player.isForceNextMapLoadRefresh() ? 1 : 0);
		stream.write128Byte(wasAtDynamicRegion ? 5 : 3); // 5 or 3 else doesnt
															// load.
		stream.writeShortLE(regionX);
		stream.initBitAccess();
		int mapHash = Settings.MAP_SIZES[player.getMapSize()] >> 4;
		int[] realRegionIds = new int[4 * mapHash * mapHash];
		int realRegionIdsCount = 0;
		for (int plane = 0; plane < 4; plane++) {
			for (int thisRegionX = (regionX - mapHash); thisRegionX <= ((regionX + mapHash)); thisRegionX++) { // real
																												// x
																												// calcs
				for (int thisRegionY = (regionY - mapHash); thisRegionY <= ((regionY + mapHash)); thisRegionY++) { // real
																													// y
																													// calcs
					int regionId = (((thisRegionX / 8) << 8) + (thisRegionY / 8));
					Region region = World.getRegion(regionId);
					int realRegionX;
					int realRegionY;
					int realPlane;
					int rotation;
					if (region instanceof DynamicRegion) { // generated map
						DynamicRegion dynamicRegion = (DynamicRegion) region;
						int[] regionCoords = dynamicRegion.getRegionCoords()[plane][thisRegionX
								- ((thisRegionX / 8) * 8)][thisRegionY
								- ((thisRegionY / 8) * 8)];
						realRegionX = regionCoords[0];
						realRegionY = regionCoords[1];
						realPlane = regionCoords[2];
						rotation = regionCoords[3];
					} else { // real map
						// base region + difference * 8 so gets real region
						// coords
						realRegionX = thisRegionX;
						realRegionY = thisRegionY;
						realPlane = plane;
						rotation = 0;// no rotation
					}
					// invalid region, not built region
					if (realRegionX == 0 || realRegionY == 0)
						stream.writeBits(1, 0);
					else {
						stream.writeBits(1, 1);
						stream.writeBits(26, (rotation << 1)
								| (realPlane << 24) | (realRegionX << 14)
								| (realRegionY << 3));
						int realRegionId = (((realRegionX / 8) << 8) + (realRegionY / 8));
						boolean found = false;
						for (int index = 0; index < realRegionIdsCount; index++)
							if (realRegionIds[index] == realRegionId) {
								found = true;
								break;
							}
						if (!found)
							realRegionIds[realRegionIdsCount++] = realRegionId;
					}

				}
			}
		}
		stream.finishBitAccess();
		for (int index = 0; index < realRegionIdsCount; index++) {
			int[] xteas = MapContainersXteas
					.getMapContainerXteas(realRegionIds[index]);
			if (xteas == null)
				xteas = new int[4];
			for (int keyIndex = 0; keyIndex < 4; keyIndex++)
				stream.writeInt(xteas[keyIndex]);
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	/*
	 * normal map region
	 */
	public void sendMapRegion(boolean sendLswp) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(43);
		if (sendLswp)
			player.getLocalPlayerUpdate().init(stream);
		stream.writeByteC(player.getMapSize());
		stream.writeByte(player.isForceNextMapLoadRefresh() ? 1 : 0);
		stream.writeShortLE(player.getChunkX());
		stream.writeShort(player.getChunkY());
		for (int regionId : player.getMapRegionsIds()) {
			int[] xteas = MapContainersXteas.getMapContainerXteas(regionId);
			if (xteas == null)
				xteas = new int[4];
			for (int index = 0; index < 4; index++)
				stream.writeInt(xteas[index]);
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendCutscene(int id) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(132);
		stream.writeShort(id);
		stream.writeShort(20); // xteas count
		for (int count = 0; count < 20; count++)
			// xteas
			for (int i = 0; i < 4; i++)
				stream.writeInt(0);
		byte[] appearence = player.getAppearence().getAppeareanceData();
		stream.writeByte(appearence.length);
		stream.writeBytes(appearence);
		stream.endPacketVarShort();
		session.write(stream);
	}

	/*
	 * sets the pane interface
	 */
	public void sendWindowsPane(int id, int type) {
		player.getInterfaceManager().setWindowsPane(id);
		OutputStream stream = new OutputStream(4);
		stream.writePacket(67);
		stream.writeShortLE128(id);
		stream.write128Byte(type);
		session.write(stream);
	}

	public void sendPlayerOption(String option, int slot, boolean top) {
		sendPlayerOption(option, slot, top, -1);
	}

	public void sendPublicMessage(Player p, PublicChatMessage message) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(91);
		stream.writeShort(p.getIndex());
		stream.writeShort(message.getEffects());
		stream.writeByte(p.getRights());
		if (message instanceof QuickChatMessage) {
			QuickChatMessage qcMessage = (QuickChatMessage) message;
			stream.writeShort(qcMessage.getFileId());
			if (qcMessage.getMessage() != null)
				stream.writeBytes(message.getMessage().getBytes());
		} else {
			byte[] chatStr = new byte[250];
			chatStr[0] = (byte) message.getMessage().length();
			int offset = 1 + Huffman.encryptMessage(1, message.getMessage()
					.length(), chatStr, 0, message.getMessage().getBytes());
			stream.writeBytes(chatStr, 0, offset);
		}
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void sendPlayerOption(String option, int slot, boolean top,
			int cursor) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(1);
		stream.writeByte128(top ? 1 : 0);
		stream.writeShortLE(cursor);
		stream.writeString(option);
		stream.writeByteC(slot);
		stream.endPacketVarByte();
		session.write(stream);
	}

	/*
	 * sends local players update
	 */
	public void sendLocalPlayersUpdate() {
		session.write(player.getLocalPlayerUpdate().createPacketAndProcess());
	}

	/*
	 * sends local npcs update
	 */
	public void sendLocalNPCsUpdate() {
		session.write(player.getLocalNPCUpdate().createPacketAndProcess());
	}

	public void sendGraphics(Graphics graphics, Object target) {
		OutputStream stream = new OutputStream(13);
		int hash = 0;
		if (target instanceof WorldTile) {
			WorldTile tile = (WorldTile) target;
			hash = tile.getPlane() << 28 | tile.getX() << 14 | tile.getY()
					& 0x3fff | 1 << 30;
		} else if (target instanceof Player) {
			Player p = (Player) target;
			hash = p.getIndex() & 0xffff | 1 << 28;
		} else {
			NPC n = (NPC) target;
			hash = n.getIndex() & 0xffff;
		}
		stream.writePacket(108);
		stream.writeShort128(graphics.getSpeed());
		stream.writeIntV2(hash);
		stream.writeByte128(0); // slot id used for entitys
		stream.writeByte128(graphics.getSettings2Hash());
		stream.writeShort(graphics.getHeight());
		stream.writeShortLE(graphics.getId());
		session.write(stream);

	}

	public void sendDelayedGraphics(Graphics graphics, int delay, WorldTile tile) {

	}

	public void closeInterface(int windowComponentId) {
		closeInterface(
				player.getInterfaceManager().getTabWindow(windowComponentId),
				windowComponentId);
		player.getInterfaceManager().removeTab(windowComponentId);
	}

	public void closeInterface(int windowId, int windowComponentId) {
		OutputStream stream = new OutputStream(5);
		stream.writePacket(73);
		stream.writeIntLE(windowId << 16 | windowComponentId);
		session.write(stream);
	}

	public void sendInterface(boolean nocliped, int windowId,
			int windowComponentId, int interfaceId) {
		// currently fixes the inter engine.. not ready for same component
		// ids(tabs), different inters
		if (!(windowId == 752 && (windowComponentId == 9 || windowComponentId == 12))) { // if
																							// chatbox
			if (player.getInterfaceManager().containsInterface(
					windowComponentId, interfaceId))
				closeInterface(windowComponentId);
			if (!player.getInterfaceManager().addInterface(windowId,
					windowComponentId, interfaceId)) {
				Logger.log(this, "Error adding interface: " + windowId + " , "
						+ windowComponentId + " , " + interfaceId);
				return;
			}
		}
		OutputStream stream = new OutputStream(8);
		stream.writePacket(5);
		stream.writeShortLE128(interfaceId);
		stream.writeIntLE(windowId << 16 | windowComponentId);
		stream.writeByte(nocliped ? 1 : 0);
		session.write(stream);
	}

	public void sendSystemUpdate(int delay) {

		OutputStream stream = new OutputStream(3);
		stream.writePacket(125);
		stream.writeShort((int) (delay * 1.6));
		session.write(stream);

	}

	public void sendUpdateItems(int key, ItemsContainer<Item> items,
			int... slots) {
		sendUpdateItems(key, items.getItems(), slots);
	}

	public void sendUpdateItems(int key, Item[] items, int... slots) {
		sendUpdateItems(key, key < 0, items, slots);
	}

	public void sendUpdateItems(int key, boolean negativeKey, Item[] items,
			int... slots) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(80);
		stream.writeShort(negativeKey ? -key : key);
		stream.writeByte(negativeKey ? 1 : 0);
		for (int slotId : slots) {
			if (slotId >= items.length)
				continue;
			stream.writeSmart(slotId);
			int id = -1;
			int amount = 0;
			Item item = items[slotId];
			if (item != null) {
				id = item.getId();
				amount = item.getAmount();
			}
			stream.writeShort(id + 1);
			if (id != -1) {
				stream.writeByte(amount >= 255 ? 255 : amount);
				if (amount >= 255)
					stream.writeInt(amount);
			}
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendGlobalString(int id, String string) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(54);
		stream.writeShortLE128(id);
		stream.writeString(string);
		stream.endPacketVarByte();
		session.write(stream);

	}

	public void sendItems(int key, ItemsContainer<Item> items) {
		sendItems(key, key < 0, items);
	}

	public void sendItems(int key, boolean keyLessIntegerSize,
			ItemsContainer<Item> items) {
		sendItems(key, keyLessIntegerSize, items.getItems());
	}

	public void sendItems(int key, Item[] items) {
		sendItems(key, key < 0, items);
	}

	public void sendItems(int key, boolean negativeKey, Item[] items) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(37);
		stream.writeShort(negativeKey ? key : key);
		stream.writeByte(negativeKey ? 1 : 0);
		stream.writeShort(items.length);
		for (int index = 0; index < items.length; index++) {
			Item item = items[index];
			int id = -1;
			int amount = 0;
			if (item != null) {
				id = item.getId();
				amount = item.getAmount();
			}
			stream.writeByte(amount >= 255 ? 255 : amount);
			if (amount >= 255)
				stream.writeInt(amount);
			stream.writeShortLE(id + 1);
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendExecMessage(String command) {
		sendMessage(1337, command, null);
	}

	public void sendLogout() {
		OutputStream stream = new OutputStream();
		stream.writePacket(51);
		ChannelFuture future = session.write(stream);
		if (future != null)
			future.addListener(ChannelFutureListener.CLOSE);
		else
			session.getChannel().close();
	}

	public void sendGameMessage(String text) {
		sendGameMessage(text, false);
	}

	public void sendGameMessage(String text, boolean filter) {
		sendMessage(filter ? 109 : 0, text, null);
	}

	public void sendPanelBoxMessage(String text) {
		sendMessage(99, text, null);
	}

	public void sendTradeRequestMessage(Player p) {
		sendMessage(100, "wishes to trade with you.", p);
	}

	public void sendClanWarsRequestMessage(Player p) {
		sendMessage(101, "wishes to challenge your clan to a clan war.", p);
	}

	public void sendDuelChallengeRequestMessage(Player p, boolean friendly) {
		sendMessage(101, "wishes to duel with you("
				+ (friendly ? "friendly" : "stake") + ").", p);
	}

	public void sendMessage(int type, String text, Player p) {
		int maskData = 0;
		if (p != null) {
			maskData |= 0x1;
			if (p.hasDisplayName())
				maskData |= 0x2;
		}
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(102);
		stream.writeSmart(type);
		stream.writeInt(0); // junk, not used by client
		stream.writeByte(maskData);
		if ((maskData & 0x1) != 0) {
			stream.writeString(p.getDisplayName());
			if (p.hasDisplayName())
				stream.writeString(Utils.formatPlayerNameForDisplay(p
						.getUsername()));
		}
		stream.writeString(text);
		stream.endPacketVarByte();
		session.write(stream);
	}

	// effect type 1 or 2(index4 or index14 format, index15 format unusused by
	// jagex for now)
	public void sendSound(int id, int delay, int effectType) {
		if (effectType == 1)
			sendIndex14Sound(id, delay);
		else if (effectType == 2)
			sendIndex15Sound(id, delay);
	}

	public void sendVoice(int id) {
		resetSounds();
		sendSound(id, 0, 2);
	}

	public void resetSounds() {
		OutputStream stream = new OutputStream(1);
		stream.writePacket(142);
		session.write(stream);
	}

	public void sendIndex14Sound(int id, int delay) {
		OutputStream stream = new OutputStream(9);
		stream.writePacket(106);
		stream.writeShort(id);
		stream.writeByte(1);
		stream.writeShort(delay);
		stream.writeByte(255);
		stream.writeShort(256);
		session.write(stream);
	}

	public void sendIndex15Sound(int id, int delay) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(121);
		stream.writeShort(id);
		stream.writeByte(1); // amt of times it repeats
		stream.writeShort(delay);
		stream.writeByte(255); // volume
		session.write(stream);
	}

	public void sendMusicEffect(int id) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(0);
		stream.writeShort128(id);
		stream.write24BitInteger(0);
		stream.writeByteC(255); // volume
		session.write(stream);
	}

	public void sendMusic(int id) {
		sendMusic(id, 100, 255);
	}

	public void sendMusic(int id, int delay, int volume) {
		OutputStream stream = new OutputStream(5);
		stream.writePacket(31);
		stream.write128Byte(delay);
		stream.writeShortLE(id);
		stream.writeByteC(volume);
		session.write(stream);
	}

	public void sendSkillLevel(int skill) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(93);
		stream.write128Byte(player.getSkills().getLevel(skill));
		stream.writeByte128(skill);
		stream.writeIntLE((int) player.getSkills().getXp(skill));
		session.write(stream);
	}

	// CUTSCENE PACKETS START

	/**
	 * This will blackout specified area.
	 * 
	 * @param byte area = area which will be blackout (0 = unblackout; 1 =
	 *        blackout orb; 2 = blackout map; 5 = blackout orb and map)
	 */
	public void sendBlackOut(int area) {
		OutputStream out = new OutputStream(2);
		out.writePacket(68);
		out.writeByte(area);
		session.write(out);
	}

	// instant
	public void sendCameraLook(int viewLocalX, int viewLocalY, int viewZ) {
		sendCameraLook(viewLocalX, viewLocalY, viewZ, -1, -1);
	}

	public void sendCameraLook(int viewLocalX, int viewLocalY, int viewZ,
			int speed1, int speed2) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(127);
		stream.writeByteC(viewLocalY);
		stream.writeShortLE128(viewZ >> 2);
		stream.write128Byte(viewLocalX);
		stream.writeByte(speed1);
		stream.write128Byte(speed2);
		session.write(stream);
	}

	public void sendResetCamera() {
		OutputStream stream = new OutputStream(1);
		stream.writePacket(10);
		session.write(stream);
	}

	public void sendCameraRotation(int unknown1, int unknown2) {
		OutputStream stream = new OutputStream(5);
		stream.writeShortLE128(unknown1);
		stream.writeShort128(unknown1);
		stream.writePacket(107);
		session.write(stream);
	}

	public void sendCameraPos(int moveLocalX, int moveLocalY, int moveZ) {
		sendCameraPos(moveLocalX, moveLocalY, moveZ, -1, -1);
	}

	public void sendCameraPos(int moveLocalX, int moveLocalY, int moveZ,
			int speed1, int speed2) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(29);
		stream.write128Byte(speed1);
		stream.writeByteC(moveLocalY);
		stream.writeByteC(moveLocalX);
		stream.writeByteC(speed2);
		stream.writeShortLE128(moveZ >> 2);
		session.write(stream);
	}

}
