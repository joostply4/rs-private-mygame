using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using RunescapeServer.player;
using System.Xml.Serialization;

namespace RunescapeServer.events {

    public class AntifirePoitionCycleEvent : LowerPotionCyclesEvent {
        public AntifirePoitionCycleEvent() {
            PotionTimeLength = 20;

            AlmostGone = "Your resistance to dragonfire is about to run out!";
            EffectsAreGone = "Your resistance to dragonfire has run out!";
        }

        public override void runAction() {
            base.runAction();
        }
    }

    public class SuperAntiPoisonPotionCycleEvent : LowerPotionCyclesEvent {
        public SuperAntiPoisonPotionCycleEvent() {
            PotionTimeLength = 20;

            AlmostGone = "Your resistance to poison is about to run out!";
            EffectsAreGone = "Your resistance to poison has run out!";
        }

        public override void runAction() {
            base.runAction();
        }
    }

    public class LowerPotionCyclesEvent : Event {
        [XmlIgnore]
        public Player Player {
            set;
            get;
        }

        protected string AlmostGone {
            set;
            get;
        }

        protected string EffectsAreGone {
            set;
            get;
        }

        public int PotionTimeLength {
            set;
            get;
        }

        public LowerPotionCyclesEvent()
            : base(15000) {
            AlmostGone = "Almost gone!";
            EffectsAreGone = "All gone!";
        }

        public override void runAction() {
            if (this.Player == null || this.Player.isDead()) {
                stop();
                return;
            }

            if (PotionTimeLength > 0) {
                if (PotionTimeLength == 2) {
                    this.Player.getPackets().sendMessage(AlmostGone);
                } else if (PotionTimeLength == 1) {
                    this.Player.getPackets().sendMessage(EffectsAreGone);
                }
            }

            if (PotionTimeLength - 1 > 0) {
                PotionTimeLength--;
            } else {
                PotionTimeLength = 0;
                stop();
                return;
            }

        }

    }
}
