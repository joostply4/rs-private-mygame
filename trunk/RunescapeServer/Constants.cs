using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using RunescapeServer.model;

namespace RunescapeServer
{
public class Constants
    {
        public const int WORLD = 25;
        public const int MAX_PLAYERS = 2000;
        public const int MAX_NPCS = 4000;
        public const bool WRITE_LOG = true;
        public const int SERVER_PORT = 43594;  //default 43594
        public const int MAX_ITEMS = 14630; //maximum item.
        public static Location HOME_SPAWN_LOCATION = new Location(2964, 3381, 0);
    }
}
