using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using RunescapeServer.player;
using RunescapeServer.model;
using RunescapeServer.util;
using System.Xml.Serialization;

namespace RunescapeServer.events {
    public class PoisonEvent : Event {
        [XmlIgnore]
        public Entity target {
            set;
            get;
        }

        public int PoisonAmount {
            set;
            get;
        }

        public PoisonEvent() {

        }

        public PoisonEvent(Entity target, int poisonAmount)
            : base(30000 + misc.random(30000)) {
            this.target = target;
            initialize(poisonAmount);
        }

        private void initialize(int poisonAmount) {
            PoisonAmount = poisonAmount;

            if (target is Player) {
                if (!((Player)target).PlayerAttributes.SuperAntiPoisonPotionCycle.isStopped()) {
                    stop();
                    return;
                }

                ((Player)target).getPackets().sendMessage("You have been poisoned!");
            }

            target.setPoisonAmount(poisonAmount);
        }

        public override void runAction() {
            if (!target.isPoisoned() || target.isDead()) {
                stop();
                return;
            }

            if (target is Player) {
                ((Player)target).getPackets().closeInterfaces();
            }

            target.hit(target.getPoisonAmount(), Hits.HitType.POISON_DAMAGE);

            if (misc.random(200) >= 100) {
                PoisonAmount--;
                target.setPoisonAmount(PoisonAmount);
            }

            //If you randomly roll a 0 poison stops?
            //What a fucking retard...
            //
            //if (misc.random(10) == 0) {
            //    PoisonAmount = 0;
            //    target.setPoisonAmount(0);
            //    stop();
            //    return;
            //}

            //Soo...
            //It goes for 30 seconds + random(30 seconds)? -- looks to me like thats too much
            setTick(30000 + misc.random(30000));
        }
    }
}
