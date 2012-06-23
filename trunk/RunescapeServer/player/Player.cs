using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using RunescapeServer.model;

namespace RunescapeServer.player {
    //Player Attributes
    //This is what gets saved to player save files

    public class PlayerAttributes {
        public LoginDetails LoginDetails {
            set;
            get;
        }

        public int PlayerRights {
            set;
            get;
        }

        public int RunEnergy {
            set;
            get;
        }

        public bool ChatEffects {
            set;
            get;
        }

        public bool PrivateChatSplit {
            set;
            get;
        }

        public bool AcceptAid {
            set;
            get;
        }

        public bool MouseTwoButtons {
            set;
            get;
        }

        public bool AutoRetaliate {
            set;
            get;
        }

        public bool VengeanceActivated {
            set;
            get;
        }

        public long LastVengeanceTime {
            set;
            get;
        }

        public int CurrentMagicInterface {
            set;
            get;
        }

        public int RingOfForgingCharge {
            set;
            get;
        }

        public long LastTeleblockTime {
            set;
            get;
        }

        public int CurrentlyPoisonedAmount {
            set;
            get;
        }

        public double CurrentPrayerPoints {
            set;
            get;
        }

        public int SpecialAttackAmount {
            set;
            get;
        }

        
    }

    class Player : Entity {

    }
}
