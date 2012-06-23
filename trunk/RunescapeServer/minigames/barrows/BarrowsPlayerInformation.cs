using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace RunescapeServer.minigames.barrows {
    public class BarrowsPlayerInformation {
        public int CurrentBarrowTunnel {
            set;
            get;
        }

        public int CurrentBarrowKillCount {
            set;
            get;
        }

        public bool[] BarrowsBrothersKilled {
            set;
            get;
        }

        public BarrowsPlayerInformation() {
            CurrentBarrowTunnel = -1;
            CurrentBarrowKillCount = 0;
            BarrowsBrothersKilled = new bool[6];
        }
    }
}
