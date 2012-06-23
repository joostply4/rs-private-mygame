using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using RunescapeServer.player;
using System.Xml.Serialization;

namespace RunescapeServer.events {
    public class SkullCycleEvent : Event {

        [XmlIgnore]
        public Player Player {
            set;
            get;
        }

        public int TimeLeft {
            set;
            get;
        }

        public SkullCycleEvent()
            : base(60000) {
            //60 Second tick count
            //Time Left = Time in Minutes
            TimeLeft = 20;
        }

        public override void runAction() {
            if (Player != null) {
                if (Player.isDead()) {
                    stop();
                    return;
                }

                if (TimeLeft - 1 > 0) {
                    TimeLeft--;
                } else {
                    TimeLeft = 0;
                    this.Player.removeSkull();
                    stop();
                    return;
                }
            }
        }
    }
}
