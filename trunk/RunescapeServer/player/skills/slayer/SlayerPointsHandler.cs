using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace RunescapeServer.player.skills.slayer {
    public class SlayerPointsHandler {
        public int SlayerPoints {
            set;
            get;
        }

        public string[] RemovedSlayerTasks {
            set;
            get;
        }

        public SlayerPointsHandler() {
            SlayerPoints = 0;
            RemovedSlayerTasks = new string[4];

            for (int i = 0; i < RemovedSlayerTasks.Length; i++) {
                RemovedSlayerTasks[i] = "-";
            }
        }
    }
}
