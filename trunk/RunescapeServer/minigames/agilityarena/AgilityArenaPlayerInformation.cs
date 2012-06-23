using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace RunescapeServer.minigames.agilityarena {
    public class AgilityArenaPlayerInformation {
        public int AgilityArenaStatus {
            set;
            get;
        }

        public bool TaggedLastAgilityPillar {
            set;
            get;
        }

        public bool PaidAgilityArena {
            set;
            get;
        }

        public AgilityArenaPlayerInformation() {
            AgilityArenaStatus = 0;
            TaggedLastAgilityPillar = false;
            PaidAgilityArena = false;
        }
    }
}
