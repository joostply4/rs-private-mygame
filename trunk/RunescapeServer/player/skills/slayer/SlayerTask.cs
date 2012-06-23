﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace RunescapeServer.player.skills.slayer {
    public class SlayerTask {
        public int amount;
        public int masterIndex;
        public int monsterIndex;

        public SlayerTask(int masterIndex, int monsterIndex, int amount) {
            this.masterIndex = masterIndex;
            this.amount = amount;
            this.monsterIndex = monsterIndex;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getMasterIndex() {
            return masterIndex;
        }

        public int getMonsterIndex() {
            return monsterIndex;
        }
    }
}
