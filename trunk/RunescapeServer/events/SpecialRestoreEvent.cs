using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using RunescapeServer.player;

namespace RunescapeServer.events {
    public class SpecialRestoreEvent : Event {
        private int MaximumSpecial {
            set;
            get;
        }

        public SpecialRestoreEvent()
            : base(50000) { }

        public override void runAction() {
            foreach (Player p in Server.getPlayerList()) {
                if (p == null) {
                    continue;
                }
                if (p.getSpecialAttack().getSpecialAmount() < 100) {
                    p.getSpecialAttack().setSpecialAmount(p.getSpecialAttack().getSpecialAmount() + 20);

                    if (p.getSpecialAttack().getSpecialAmount() > 100) {
                        p.getSpecialAttack().setSpecialAmount(100);
                    }

                    p.setSpecialAmount(p.getSpecialAttack().getSpecialAmount());
                }
            }
        }
    }
}
