using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using RunescapeServer.player;
using RunescapeServer.npc;
using RunescapeServer.model;
using RunescapeServer.events;
using RunescapeServer.util;

namespace RunescapeServer.minigames.godwars
{
    class GodwarsTeleport
    {
        public void teleportToGodwars()
        {
            Location[] teleport = godwarsTeleport();
        }

        private Location[] godwarsTeleport()
        {
            Location[] location = new Location[1];
            int x = 2000;
            int y = 2000;

            location[1] = new Location(x, y, 0);
            return location;
        }
    }
}
