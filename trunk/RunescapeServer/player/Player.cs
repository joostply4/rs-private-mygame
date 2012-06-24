using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using RunescapeServer.model;
using RunescapeServer.events;
using System.Xml.Serialization;
using RunescapeServer.player.skills.slayer;
using RunescapeServer.minigames.fightcave;
using RunescapeServer.minigames.duelarena;
using RunescapeServer.combat;
using RunescapeServer.net;
using RunescapeServer.clans;
using RunescapeServer.player.skills.prayer;
using RunescapeServer.definitions;
using RunescapeServer.util;
using RunescapeServer.npc;
using RunescapeServer.minigames.barrows;
using RunescapeServer.player.skills.runecrafting;
using RunescapeServer.player.skills.farming;
using RunescapeServer.minigames.agilityarena;

namespace RunescapeServer.player {
    //Player Attributes
    //This is what gets saved to player save files
    [Serializable]
    public class PlayerAttributes {

        public PlayerAttributes() {

        }

        public LoginDetails LoginDetails {
            set;
            get;
        }

        public Location LastKnownLocation {
            set;
            get;
        }

        public int PlayerRights {
            set;
            get;
        }

        public int RunEnergy {
            set;
            get;
        }

        public bool ChatEffects {
            set;
            get;
        }

        public bool PrivateChatSplit {
            set;
            get;
        }

        public bool AcceptAid {
            set;
            get;
        }

        public bool MouseTwoButtons {
            set;
            get;
        }

        public bool AutoRetaliate {
            set;
            get;
        }

        public int CurrentMagicInterface {
            set;
            get;
        }

        public int RingOfForgingCharge {
            set;
            get;
        }

        public double CurrentPrayerPoints {
            set;
            get;
        }

        public int RingOfRecoilCharges {
            set;
            get;
        }

        public PoisonEvent PoisonEvent {
            set;
            get;
        }

        public bool IsPoisoned {
            get {
                if (this.PoisonEvent != null) {
                    return !this.PoisonEvent.isStopped();
                } else {
                    return false;
                }
            }
        }

        public SkullCycleEvent SkullCycleEvent {
            set;
            get;
        }

        public bool IsSkulled {
            get {
                if (this.SkullCycleEvent != null) {
                    return !this.SkullCycleEvent.isStopped();
                } else {
                    return false;
                }
            }
        }

        public AntifirePoitionCycleEvent AntiFirePotionCycle {
            set;
            get;
        }

        public SuperAntiPoisonPotionCycleEvent SuperAntiPoisonPotionCycle {
            set;
            get;
        }

        public SlayerTask SlayerTask {
            set;
            get;
        }

        public Bank Bank {
            set;
            get;
        }

        public Inventory Inventory {
            set;
            get;
        }

        public Equipment Equipment {
            set;
            get;
        }

        public Friends Friends {
            set;
            get;
        }

        public Skills Skills {
            set;
            get;
        }

        public AttackStyle AttackStyle {
            set;
            get;
        }

        public Appearance Appearance {
            set;
            get;
        }

        public SpecialAttack SpecialAttack {
            set;
            get;
        }

        public RunecraftPouchManager RunecraftPouchManager {
            set;
            get;
        }

        public bool Vengeance {
            set;
            get;
        }

        public long TeleportBlockedTime {
            set;
            get;
        }

        public long LastVengeanceCallTime {
            set;
            get;
        }

        public int CurrentDefender {
            set;
            get;
        }

        public BarrowsPlayerInformation BarrowsPlayerInformation {
            set;
            get;
        }

        public AgilityArenaPlayerInformation AgilityArenaPlayerInformation {
            set;
            get;
        }

        public SlayerPointsHandler SlayerPointsHandler {
            set;
            get;
        }
    }

    public class Player : Entity {
        //These items DO NOT get parsed into XML
        //Although I am not sure about the CLAN object yet
        private Clan clan;
        //These are for the server to create on the fly
        private DwarfCannon cannon;
        private GESession grandExchangeSession;
        private TradeSession tradeSession;
        private ShopSession shopSession;
        private FightCaveSession fightCave;
        private DuelSession duelSession;
        private object distanceEvent;
        private List<Player> tradeRequests;
        private List<Player> duelRequests;
        private ForceMovement forceMovement;
        private Prayers prayers;
        private Combat.CombatType lastCombatType;
        private Packets packets;
        private LocalEnvironment localEnvironment;
        private AppearanceUpdateFlags updateFlags;
        private ChatMessage lastChatMessage;
        private WalkingQueue walkingQueue;
        private bool clientActive = false;
        private Connection connection;
        private bool hd; //high definition game.
        private bool disconnected;
        private double lastHit;
        private bool achievementDiaryTab;
        
        //RSServ Items
        private bool _NewPlayer;

        public PlayerAttributes PlayerAttributes {
            set;
            get;
        }

        public Player(Connection connection) {
            this.connection = connection; //without this, new Packets(this); wouldn't function.
            PlayerAttributes = new PlayerAttributes();

            if (connection != null)
                PlayerAttributes.LoginDetails = connection.getLoginDetails();
            PlayerAttributes.Appearance = new Appearance();
            follow = new Follow(this);
            PlayerAttributes.Bank = new Bank(this);
            PlayerAttributes.Inventory = new Inventory(this);
            PlayerAttributes.Equipment = new Equipment(this);
            PlayerAttributes.Friends = new Friends(this);
            prayers = new Prayers(this);
            PlayerAttributes.Skills = new Skills(this);
            PlayerAttributes.AttackStyle = new AttackStyle();
            packets = new Packets(this);
            localEnvironment = new LocalEnvironment(this);
            updateFlags = new AppearanceUpdateFlags(this);
            walkingQueue = new WalkingQueue(this);
            PlayerAttributes.SpecialAttack = new SpecialAttack(this);
            PlayerAttributes.ChatEffects = true;
            PlayerAttributes.PrivateChatSplit = false;
            PlayerAttributes.MouseTwoButtons = true;
            PlayerAttributes.AcceptAid = false;
            PlayerAttributes.PlayerRights = 0;
            PlayerAttributes.CurrentMagicInterface = 1;
            achievementDiaryTab = false;
            PlayerAttributes.RingOfForgingCharge = 40;
            PlayerAttributes.RunecraftPouchManager = new RunecraftPouchManager();
            PlayerAttributes.CurrentDefender = 0;
            PlayerAttributes.AutoRetaliate = false;
            PlayerAttributes.Vengeance = false;
            PlayerAttributes.LastVengeanceCallTime = 0;
            //PlayerAttributes.PoisonEvent = new PoisonEvent(this, 0);
            //PlayerAttributes.SkullCycleEvent = new SkullCycleEvent();
            PlayerAttributes.CurrentPrayerPoints = 1;
            PlayerAttributes.BarrowsPlayerInformation = new BarrowsPlayerInformation();
            PlayerAttributes.SlayerPointsHandler = new SlayerPointsHandler();
            PlayerAttributes.AgilityArenaPlayerInformation = new AgilityArenaPlayerInformation();
            PlayerAttributes.TeleportBlockedTime = 0;
            lastHit = -1;
            //PlayerAttributes.SuperAntiPoisonPotionCycle = new SuperAntiPoisonPotionCycleEvent();
            //PlayerAttributes.AntiFirePotionCycle = new AntifirePoitionCycleEvent();
            tradeRequests = new List<Player>();
            duelRequests = new List<Player>();
            PlayerAttributes.RunEnergy = 100;

            //RSServ Items
            _NewPlayer = false;
        }

        public bool isNewPlayer() {
            return _NewPlayer;
        }

        public void setNewPlayer(bool b) {
            _NewPlayer = b;
        }

        public void tick() {
            if (this.inCombat()) {
                Combat.combatLoop(this);
            }
            if (getFollow().getFollowing() != null) {
                getFollow().followEntity();
            }

            this.PlayerAttributes.LastKnownLocation = this.getLocation();
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        public Connection getConnection() {
            return connection;
        }

        public void setLoginDetails(LoginDetails loginDetails) {
            this.PlayerAttributes.LoginDetails = loginDetails;
        }

        public LoginDetails getLoginDetails() {
            return this.PlayerAttributes.LoginDetails;
        }

        public bool isActive() {
            return clientActive;
        }

        public void setActive(bool clientActive) {
            this.clientActive = clientActive;
        }

        public int getWorld() {
            return Constants.WORLD;
        }

        public int getRights() {
            return this.PlayerAttributes.PlayerRights;
        }

        public void setRights(int playerRights) {
            this.PlayerAttributes.PlayerRights = playerRights;
        }

        public void setHd(bool b) {
            this.hd = b;
        }

        public bool isHd() {
            return hd;
        }

        public void setRunEnergy(int runEnergy) {
            this.PlayerAttributes.RunEnergy = runEnergy;
            packets.sendEnergy();
        }

        public int getRunEnergy() {
            return this.PlayerAttributes.RunEnergy;
        }

        public bool isDisconnected() {
            return disconnected;
        }

        public void setDisconnected(bool disconnected) {
            this.disconnected = disconnected;
        }

        public List<Player> getTradeRequests() {
            return tradeRequests;
        }

        public bool wantsToTrade(Player p2) {
            foreach (Player p in tradeRequests) {
                if (p != null) {
                    if (p.Equals(p2)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public void newTradeRequest(Player p2) {
            if (wantsToTrade(p2)) {
                return;
            }
            tradeRequests.Add(p2);
        }

        public List<Player> getDuelRequests() {
            return duelRequests;
        }

        public bool wantsToDuel(Player p2) {
            foreach (Player p in duelRequests) {
                if (p != null) {
                    if (p.Equals(p2)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public void newDuelRequest(Player p2) {
            if (wantsToDuel(p2)) {
                return;
            }
            duelRequests.Add(p2);
        }

        public Clan getClan() {
            return clan;
        }

        public void setClan(Clan clan) {
            this.clan = clan;
        }

        public Bank getBank() {
            return this.PlayerAttributes.Bank;
        }

        public Inventory getInventory() {
            return this.PlayerAttributes.Inventory;
        }

        public Equipment getEquipment() {
            return this.PlayerAttributes.Equipment;
        }

        public Friends getFriends() {
            return this.PlayerAttributes.Friends;
        }

        public Prayers getPrayers() {
            return prayers;
        }

        public Skills getSkills() {
            return this.PlayerAttributes.Skills;
        }

        public AttackStyle getAttackStyle() {
            return this.PlayerAttributes.AttackStyle;
        }

        public void setLastCombatType(Combat.CombatType lastCombatType) {
            this.lastCombatType = lastCombatType;
        }

        public Combat.CombatType getLastCombatType() {
            return lastCombatType;
        }


        public Packets getPackets() {
            return packets;
        }

        public LocalEnvironment getLocalEnvironment() {
            return localEnvironment;
        }

        public Appearance getAppearance() {
            return this.PlayerAttributes.Appearance;
        }

        public void setAppearance(Appearance newAppearance) {
            this.PlayerAttributes.Appearance = newAppearance;
            updateFlags.setAppearanceUpdateRequired(true);
        }

        public AppearanceUpdateFlags getUpdateFlags() {
            return updateFlags;
        }

        public ChatMessage getLastChatMessage() {
            return lastChatMessage;
        }

        public void setLastChatMessage(ChatMessage msg) {
            lastChatMessage = msg;
            updateFlags.setChatTextUpdateRequired(true);
        }

        public ForceMovement getForceMovement() {
            return forceMovement;
        }

        public void setForceMovement(ForceMovement movement) {
            this.forceMovement = movement;
            updateFlags.setForceMovementRequired(true);
        }

        public WalkingQueue getWalkingQueue() {
            return walkingQueue;
        }

        public SpecialAttack getSpecialAttack() {
            return this.PlayerAttributes.SpecialAttack;
        }

        public GESession getGESession() {
            return grandExchangeSession;
        }

        public void setGESession(GESession geSession) {
            this.grandExchangeSession = geSession;
        }

        public TradeSession getTrade() {
            return tradeSession;
        }

        public void setTrade(TradeSession tradeSession) {
            this.tradeSession = tradeSession;
        }

        public ShopSession getShopSession() {
            return shopSession;
        }

        public void setShopSession(ShopSession shopSession) {
            this.shopSession = shopSession;
        }

        public FightCaveSession getFightCave() {
            return fightCave;
        }

        public void setFightCave(FightCaveSession fightCave) {
            this.fightCave = fightCave;
        }

        public DuelSession getDuel() {
            return duelSession;
        }

        public void setDuelSession(DuelSession duelSession) {
            this.duelSession = duelSession;
        }

        public override void setHp(int newHp) {
            this.PlayerAttributes.Skills.setCurLevel(Skills.SKILL.HITPOINTS, newHp);
        }

        public override int getHp() {
            return this.PlayerAttributes.Skills.getCurLevel(Skills.SKILL.HITPOINTS);
        }

        public override int getMaxHp() {
            return this.PlayerAttributes.Skills.getMaxLevel(Skills.SKILL.HITPOINTS);
        }

        public override void heal(int amount) {
            if (isDead()) {
                return;
            }
            if ((this.PlayerAttributes.Skills.getCurLevel(Skills.SKILL.HITPOINTS) + amount) > (this.PlayerAttributes.Skills.getMaxLevel(Skills.SKILL.HITPOINTS))) {
                this.PlayerAttributes.Skills.setCurLevel(Skills.SKILL.HITPOINTS, this.PlayerAttributes.Skills.getMaxLevel(Skills.SKILL.HITPOINTS));
                packets.sendSkillLevel(Skills.SKILL.HITPOINTS);
                return;
            }
            this.PlayerAttributes.Skills.setCurLevel(Skills.SKILL.HITPOINTS, this.PlayerAttributes.Skills.getCurLevel(Skills.SKILL.HITPOINTS) + amount);
            packets.sendSkillLevel(Skills.SKILL.HITPOINTS);
        }

        public override bool isDestroyed() {
            return !Server.getPlayerList().Contains(this);
        }

        public override int getMaxHit() {
            return (int)CombatFormula.getPlayerMaxHit(this, 0);
        }


        public double getMaxHit(int strengthModifier) {
            return CombatFormula.getPlayerMaxHit(this, strengthModifier);
        }

        public override void hit(int damage) {
            if (duelSession != null) {
                if (duelSession.getStatus() >= 8) {
                    return;
                }
            }
            hit(damage, damage == 0 ? Hits.HitType.NO_DAMAGE : Hits.HitType.NORMAL_DAMAGE);
        }

        public override void hit(int damage, Hits.HitType type) {
            if (isDead()) {
                damage = 0;
                type = Hits.HitType.NO_DAMAGE;
            }
            bool redemption = prayers.getHeadIcon() == PrayerData.REDEMPTION;
            byte maxHp = (byte)this.PlayerAttributes.Skills.getMaxLevel(Skills.SKILL.HITPOINTS);
            byte newHp = (byte)(this.PlayerAttributes.Skills.getMaxLevel(Skills.SKILL.HITPOINTS) - damage);
            if (redemption) {
                if (newHp >= 1 && newHp <= maxHp * 0.10) {
                    setLastGraphics(new Graphics(436, 0, 0));
                    packets.sendMessage("Using your prayer skill, you heal yourself.");
                    this.PlayerAttributes.Skills.setCurLevel(Skills.SKILL.PRAYER, 0);
                    packets.sendSkillLevel(Skills.SKILL.PRAYER);
                    heal((int)(this.PlayerAttributes.Skills.getMaxLevel(Skills.SKILL.PRAYER) * 0.25));
                }
            }
            if (duelSession != null) {
                if (duelSession.getStatus() >= 8)
                    return;
            } else {
                if (newHp >= 1 && newHp <= maxHp * 0.10 && !redemption) {
                    if (this.PlayerAttributes.Equipment.getItemInSlot(ItemData.EQUIP.RING) == 2570) {
                        teleport(new Location(3221 + misc.random(1), 3217 + misc.random(3), 0));
                        packets.sendMessage("Your ring of life shatters whilst teleporting you to safety.");
                        this.PlayerAttributes.Equipment.getSlot(ItemData.EQUIP.RING).setItemId(-1);
                        this.PlayerAttributes.Equipment.getSlot(ItemData.EQUIP.RING).setItemAmount(0);
                        packets.refreshEquipment();
                        queuedHits.Clear();
                        Combat.resetCombat(this, 1);
                        return;
                    }
                }
            }
            bool damageOverZero = damage > 0;
            if (damage > this.PlayerAttributes.Skills.getCurLevel(Skills.SKILL.HITPOINTS)) {
                damage = this.PlayerAttributes.Skills.getCurLevel(Skills.SKILL.HITPOINTS);
            }
            if (damageOverZero && damage == 0) {
                type = Hits.HitType.NO_DAMAGE;
            }
            if (!updateFlags.isHitUpdateRequired()) {
                getHits().setHit1(new Hits.Hit(damage, type));
                updateFlags.setHitUpdateRequired(true);
            } else {
                if (!updateFlags.isHit2UpdateRequired()) {
                    getHits().setHit2(new Hits.Hit(damage, type));
                    updateFlags.setHit2UpdateRequired(true);
                } else {
                    lock (queuedHits) {
                        queuedHits.Enqueue(new Hits.Hit(damage, type));
                    }
                    return;
                }
            }
            this.PlayerAttributes.Skills.setCurLevel(Skills.SKILL.HITPOINTS, this.PlayerAttributes.Skills.getCurLevel(Skills.SKILL.HITPOINTS) - damage);
            if (this.PlayerAttributes.Skills.getCurLevel(Skills.SKILL.HITPOINTS) <= 0) {
                this.PlayerAttributes.Skills.setCurLevel(Skills.SKILL.HITPOINTS, 0);
                if (!isDead()) {
                    Server.registerEvent(new DeathEvent(this));
                    setDead(true);
                }
            }
            packets.sendSkillLevel(Skills.SKILL.HITPOINTS);
        }

        public override void dropLoot() {
            Entity killer = this.getKiller();
            Player klr = killer is Npc ? null : (Player)killer;
            if (klr == null) {
                klr = this;
            }
            int amountToKeep = isSkulled() ? 0 : 3;
            if (prayers.isProtectItem()) {
                amountToKeep = isSkulled() ? 1 : 4;
            }

            /*
             * Meh wanted to make a much more effient system.
            //price of item, [item, object[]=(inventory/euqipment)]
            //Inventory Items
            SortedDictionary<int, object[]> valueSortedItems = new SortedDictionary<int, object[]>();
            Item item;
            int price = 0;
            for(int i = 0; i < Inventory.MAX_INVENTORY_SLOTS; i++) {
                item = inventory.getSlot(i);
                if(item.getItemId() != -1 && item.getItemAmount() > 0) { //is real item.
                    //price of item stacked
                    price = item.getItemAmount() * item.getDefinition().getPrice().getMaximumPrice();
                    valueSortedItems.Add(price, new object[] {item, 0}); 
                }
            }
            //Equipment Items
            for(int i = 0; i < 14; i++) {
                item = equipment.getSlot(i);
                if(item.getItemId() != -1 && item.getItemAmount() > 0) { //is real item.
                    //price of item stacked
                    price = item.getItemAmount() * item.getDefinition().getPrice().getMaximumPrice();
                    valueSortedItems.Add(price, new object[] {item, 1}); 
                }
            }*/

            int[] protectedItems = new int[amountToKeep];
            bool[] saved = new bool[amountToKeep];
            if (protectedItems.Length > 0) {
                protectedItems[0] = ProtectedItems.getProtectedItem1(this)[0];
            }
            if (protectedItems.Length > 1) {
                protectedItems[1] = ProtectedItems.getProtectedItem2(this)[0];
            }
            if (protectedItems.Length > 2) {
                protectedItems[2] = ProtectedItems.getProtectedItem3(this)[0];
            }
            if (protectedItems.Length > 3) {
                protectedItems[3] = ProtectedItems.getProtectedItem4(this)[0];
            }
            bool save = false;
            for (int i = 0; i < 28; i++) {
                save = false;
                Item item = this.PlayerAttributes.Inventory.getSlot(i);
                if (item.getItemId() > 0) {
                    for (int j = 0; j < protectedItems.Length; j++) {
                        if (amountToKeep > 0 && protectedItems[j] > 0) {
                            if (!saved[j] && !save) {
                                if (item.getItemId() == protectedItems[j] && item.getItemAmount() == 1) {
                                    saved[j] = true;
                                    save = true;
                                }
                                if (item.getItemId() == protectedItems[j] && item.getItemAmount() > 1) {
                                    item.setItemAmount(item.getItemAmount() - 1);
                                    saved[j] = true;
                                    save = true;
                                }
                            }
                        }
                    }
                    if (!save) {
                        int itemId = item.getItemId();
                        if (itemId >= 4708 && itemId <= 4759) {
                            itemId = BrokenBarrows.getBrokenId(itemId);
                        }
                        GroundItem gi = new GroundItem(itemId, item.getItemAmount(), this.getLocation(), item.getDefinition().isPlayerBound() ? this : klr);
                        Server.getGroundItems().newEntityDrop(gi);
                    }
                }
            }
            this.PlayerAttributes.Inventory.clearAll();
            saved = new bool[amountToKeep];
            foreach (ItemData.EQUIP equip in Enum.GetValues(typeof(ItemData.EQUIP))) {
                if (equip == ItemData.EQUIP.NOTHING) continue;
                save = false;
                Item item = this.getEquipment().getSlot(equip);
                if (item.getItemId() > 0) {
                    for (int j = 0; j < protectedItems.Length; j++) {
                        if (amountToKeep > 0 && protectedItems[j] > -1) {
                            if (!saved[j] && !save) {
                                if (item.getItemId() == protectedItems[j] && item.getItemAmount() == 1) {
                                    saved[j] = true;
                                    save = true;
                                }
                                if (item.getItemId() == protectedItems[j] && item.getItemAmount() > 1) {
                                    item.setItemAmount(item.getItemAmount() - 1);
                                    saved[j] = true;
                                    save = true;
                                }
                            }
                        }
                    }
                    if (!save) {
                        int itemId = item.getItemId();
                        if (itemId >= 4708 && itemId <= 4759) {
                            itemId = BrokenBarrows.getBrokenId(itemId);
                        }
                        GroundItem gi = new GroundItem(itemId, item.getItemAmount(), this.getLocation(), item.getDefinition().isPlayerBound() ? this : klr);
                        Server.getGroundItems().newEntityDrop(gi);
                    }
                }
            }
            this.PlayerAttributes.Equipment.clearAll();
            GroundItem bones = new GroundItem(526, 1, this.getLocation(), klr);
            Server.getGroundItems().newEntityDrop(bones);
            this.PlayerAttributes.Inventory.setProtectedItems(protectedItems);
        }

        public override int getAttackAnimation() {
            return !this.PlayerAttributes.Appearance.isNpc() ? Animations.getAttackAnimation(this) : NpcData.forId(this.PlayerAttributes.Appearance.getNpcId()).getAttackAnimation();
        }

        public override int getAttackSpeed() {
            if (getMiasmicEffect() > 0) {
                return Animations.getAttackSpeed(this) * 2;
            }
            return Animations.getAttackSpeed(this);
        }

        public override int getDeathAnimation() {
            return !this.PlayerAttributes.Appearance.isNpc() ? 7185 : NpcData.forId(this.PlayerAttributes.Appearance.getNpcId()).getDeathAnimation();
        }

        public override int getDefenceAnimation() {
            return !this.PlayerAttributes.Appearance.isNpc() ? Animations.getDefenceAnimation(this) : NpcData.forId(this.PlayerAttributes.Appearance.getNpcId()).getDefenceAnimation();
        }

        public override int getHitDelay() {
            return Animations.getPlayerHitDelay(this);
        }

        //BOBBY
        public void refresh() {
            getFriends().login();
            getPackets().sendConfig(171, !this.PlayerAttributes.ChatEffects ? 1 : 0);
            getPackets().sendConfig(287, this.PlayerAttributes.PrivateChatSplit ? 1 : 0);
            if (this.PlayerAttributes.PrivateChatSplit) {
                getPackets().sendBlankClientScript(83, "s");
            }
            getPackets().sendConfig(170, !this.PlayerAttributes.MouseTwoButtons ? 1 : 0);
            getPackets().sendConfig(427, this.PlayerAttributes.AcceptAid ? 1 : 0);
            getPackets().sendConfig(172, !this.PlayerAttributes.AutoRetaliate ? 1 : 0);
            if (this.PlayerAttributes.CurrentMagicInterface != 1) {
                getPackets().sendTab(isHd() ? 99 : 89, this.PlayerAttributes.CurrentMagicInterface == 2 ? 193 : 430);
            }
            if (achievementDiaryTab) {
                getPackets().sendTab(isHd() ? 95 : 85, 259);
            }
            RuneCraft.toggleRuin(this, getEquipment().getItemInSlot(ItemData.EQUIP.HAT), RuneCraft.wearingTiara(this));
            //getSpecialAttack().setSpecialAmount(specialAmount);
            //setPoisonAmount(poisonAmount);
            //if (poisonAmount > 0) {
            //    Server.registerEvent(new PoisonEvent((Entity)this, poisonAmount));
            //}
            //if (teleblockTime > 0) {
            //    if (teleblockTime > Environment.TickCount) {
            //        long delay = teleblockTime - Environment.TickCount;
            //        setTemporaryAttribute("teleblocked", true);
            //        Event removeTeleBlockEvent = new Event(delay);
            //        removeTeleBlockEvent.setAction(() => {
            //            removeTeleBlockEvent.stop();
            //            removeTemporaryAttribute("teleblocked");
            //            teleblockTime = 0;
            //        });
            //        Server.registerEvent(removeTeleBlockEvent);
            //    }
            //}
            Farming.refreshPatches(this);
            getEquipment().refreshBonuses();
            if (fightCave != null) {
                fightCave.setPlayer(this);
                fightCave.resumeGame();
            }
            //setSkullCycles(skullCycles); // This method updates the appearance, so have this last.
        }

        public int getAgilityArenaStatus() {
            return this.PlayerAttributes.AgilityArenaPlayerInformation.AgilityArenaStatus;
        }

        public void setAgilityArenaStatus(int agilityArenaStatus) {
            this.PlayerAttributes.AgilityArenaPlayerInformation.AgilityArenaStatus = agilityArenaStatus;
        }

        public bool isAchievementDiaryTab() {
            return achievementDiaryTab;
        }

        public bool isMouseTwoButtons() {
            return this.PlayerAttributes.MouseTwoButtons;
        }

        public bool isChatEffectsEnabled() {
            return this.PlayerAttributes.ChatEffects;
        }

        public bool isPrivateChatSplit() {
            return this.PlayerAttributes.PrivateChatSplit;
        }

        public bool isAcceptAidEnabled() {
            return this.PlayerAttributes.AcceptAid;
        }

        public void setMouseTwoButtons(bool mouse) {
            this.PlayerAttributes.MouseTwoButtons = mouse;
        }

        public void setChatEffectsEnabled(bool chat) {
            this.PlayerAttributes.ChatEffects = chat;
        }

        public void setPrivateChatSplit(bool split) {
            this.PlayerAttributes.PrivateChatSplit = split;
            if (split) {
                getPackets().sendBlankClientScript(83, "s");
            }
        }

        public void setAcceptAidEnabled(bool aid) {
            this.PlayerAttributes.AcceptAid = aid;
        }

        public int getMagicType() {
            return this.PlayerAttributes.CurrentMagicInterface;
        }

        public void setMagicType(int magicType) {
            this.PlayerAttributes.CurrentMagicInterface = magicType;
        }

        public int getDefenderWave() {
            return this.PlayerAttributes.CurrentDefender;
        }

        public void setDefenderWave(int defenderWave) {
            this.PlayerAttributes.CurrentDefender = defenderWave;
        }

        public void setAchievementDiaryTab(bool b) {
            this.achievementDiaryTab = b;
        }

        public int getSmallPouchAmount() {
            return this.PlayerAttributes.RunecraftPouchManager.SmallPouchAmount;
        }

        public void setSmallPouchAmount(int smallPouchAmount) {
            this.PlayerAttributes.RunecraftPouchManager.SmallPouchAmount = smallPouchAmount;
        }

        public int getMediumPouchAmount() {
            return this.PlayerAttributes.RunecraftPouchManager.MediumPouchAmount;
        }

        public void setMediumPouchAmount(int mediumPouchAmount) {
            this.PlayerAttributes.RunecraftPouchManager.MediumPouchAmount = mediumPouchAmount;
        }

        public int getLargePouchAmount() {
            return this.PlayerAttributes.RunecraftPouchManager.LargePouchAmount;
        }

        public void setLargePouchAmount(int largePouchAmount) {
            this.PlayerAttributes.RunecraftPouchManager.LargePouchAmount = largePouchAmount;
        }

        public int getGiantPouchAmount() {
            return this.PlayerAttributes.RunecraftPouchManager.GiantPouchAmount;
        }

        public void setGiantPouchAmount(int giantPouchAmount) {
            this.PlayerAttributes.RunecraftPouchManager.GiantPouchAmount = giantPouchAmount;
        }

        public bool isSkulled() {
            return this.PlayerAttributes.IsSkulled;
        }

        public void setAutoRetaliate(bool autoRetaliate) {
            this.PlayerAttributes.AutoRetaliate = autoRetaliate;
        }

        public void toggleAutoRetaliate() {
            this.PlayerAttributes.AutoRetaliate = !this.PlayerAttributes.AutoRetaliate;
            getPackets().sendConfig(172, !this.PlayerAttributes.AutoRetaliate ? 1 : 0);
        }

        public override bool isAutoRetaliating() {
            return this.PlayerAttributes.AutoRetaliate;
        }

        public void setSpecialAmount(int specialAmount) {
            this.PlayerAttributes.SpecialAttack.setSpecialAmount(specialAmount);
        }

        public void setBarrowTunnel(int barrowTunnel) {
            this.PlayerAttributes.BarrowsPlayerInformation.CurrentBarrowTunnel = barrowTunnel;
        }

        public int getBarrowTunnel() {
            return this.PlayerAttributes.BarrowsPlayerInformation.CurrentBarrowTunnel;
        }

        public void setBarrowKillCount(int i) {
            this.PlayerAttributes.BarrowsPlayerInformation.CurrentBarrowKillCount = i;
            if (this.PlayerAttributes.BarrowsPlayerInformation.CurrentBarrowKillCount > 9999) {
                this.PlayerAttributes.BarrowsPlayerInformation.CurrentBarrowKillCount = 9999;
            }
        }

        public int getBarrowKillCount() {
            return this.PlayerAttributes.BarrowsPlayerInformation.CurrentBarrowKillCount;
        }

        public void setBarrowBrothersKilled(int i, bool b) {
            this.PlayerAttributes.BarrowsPlayerInformation.BarrowsBrothersKilled[i] = b;
        }

        public bool getBarrowBrothersKilled(int i) {
            return this.PlayerAttributes.BarrowsPlayerInformation.BarrowsBrothersKilled[i];
        }

        public void setRecoilCharges(int i) {
            this.PlayerAttributes.RingOfRecoilCharges = i;
        }

        public int getRecoilCharges() {
            return this.PlayerAttributes.RingOfRecoilCharges;
        }

        public void setVengeance(bool vengeance) {
            this.PlayerAttributes.Vengeance = vengeance;
        }

        public bool hasVengeance() {
            return this.PlayerAttributes.Vengeance;
        }

        public void setCannon(DwarfCannon cannon) {
            this.cannon = cannon;
        }

        public DwarfCannon getCannon() {
            return cannon;
        }

        public void setSlayerTask(SlayerTask task) {
            this.PlayerAttributes.SlayerTask = task;
        }

        public SlayerTask getSlayerTask() {
            return this.PlayerAttributes.SlayerTask;
        }

        public int getSlayerPoints() {
            return this.PlayerAttributes.SlayerPointsHandler.SlayerPoints;
        }

        public void setSlayerPoints(int i) {
            this.PlayerAttributes.SlayerPointsHandler.SlayerPoints = i;
        }

        public void setRemovedSlayerTask(int index, string monster) {
            this.PlayerAttributes.SlayerPointsHandler.RemovedSlayerTasks[index] = monster;
        }

        public string[] getRemovedSlayerTasks() {
            return this.PlayerAttributes.SlayerPointsHandler.RemovedSlayerTasks;
        }

        public void setRemovedSlayerTask(string[] tasks) {
            this.PlayerAttributes.SlayerPointsHandler.RemovedSlayerTasks = tasks;
        }

        public bool isTaggedLastAgilityPillar() {
            return this.PlayerAttributes.AgilityArenaPlayerInformation.TaggedLastAgilityPillar;
        }

        public void setTaggedLastAgilityPillar(bool b) {
            this.PlayerAttributes.AgilityArenaPlayerInformation.TaggedLastAgilityPillar = b;
        }

        public void setPaidAgilityArena(bool paidAgilityArena) {
            this.PlayerAttributes.AgilityArenaPlayerInformation.PaidAgilityArena = paidAgilityArena;
        }

        public bool hasPaidAgilityArena() {
            return this.PlayerAttributes.AgilityArenaPlayerInformation.PaidAgilityArena;
        }

        public double getLastHit() {
            return lastHit;
        }

        public void setLastHit(double hit) {
            this.lastHit = hit;
        }

        public long getTeleblockTime() {
            return this.PlayerAttributes.TeleportBlockedTime;
        }

        public void setTeleblockTime(long l) {
            this.PlayerAttributes.TeleportBlockedTime = l;
        }

        public int getForgeCharge() {
            return this.PlayerAttributes.RingOfForgingCharge;
        }

        public void setForgeCharge(int forgeCharge) {
            this.PlayerAttributes.RingOfForgingCharge = forgeCharge;
        }

        public void setTzhaarSkull() {
            getPrayers().setPkIcon(1);
        }

        public long getLastVengeanceTime() {
            return this.PlayerAttributes.LastVengeanceCallTime;
        }

        public void setLastVengeanceTime(long time) {
            this.PlayerAttributes.LastVengeanceCallTime = time;
        }

        public double getPrayerPoints() {
            return this.PlayerAttributes.CurrentPrayerPoints;
        }

        public void setPrayerPoints(double p) {
            this.PlayerAttributes.CurrentPrayerPoints = p;
        }

        public void decreasePrayerPoints(double modification) {
            int lvlBefore = (int)Math.Ceiling(getPrayerPoints());
            if (getPrayerPoints() > 0) {
                setPrayerPoints((getPrayerPoints() - modification <= 0 ? 0 : getPrayerPoints() - modification));
            }
            int lvlAfter = (int)Math.Ceiling(getPrayerPoints());
            if (lvlBefore - lvlAfter >= 1) {
                getSkills().setCurLevel(Skills.SKILL.PRAYER, lvlAfter);
                getPackets().sendSkillLevel(Skills.SKILL.PRAYER);
            }
        }

        public object getDistanceEvent() {
            return distanceEvent;
        }

        public void setDistanceEvent(object distanceEvent) {
            this.distanceEvent = distanceEvent;
        }

        public void removeSkull() {
            if (this.PlayerAttributes.SkullCycleEvent != null) {
                this.PlayerAttributes.SkullCycleEvent.stop();
                this.PlayerAttributes.SkullCycleEvent = null;
            }

            this.getPrayers().setPkIcon(-1);            
        }

        public void renewSkull() {
            SkullCycleEvent sce = new SkullCycleEvent();
            sce.Player = this;
            sce.TimeLeft = 20;
            this.PlayerAttributes.SkullCycleEvent = sce;

            this.getPrayers().setPkIcon(0);

            Server.registerEvent(this.PlayerAttributes.SkullCycleEvent);
        }
    }
}
