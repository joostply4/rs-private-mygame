using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace RunescapeServer.player
{
    public class LoginDetails
    {
        public string username;
        private long longName;
        public string password;

        public LoginDetails() {

        }

        public long getLongName()
        {
            return longName;
        }

        public void setLongName(long longName)
        {
            this.longName = longName;
        }

        public string getUsername()
        {
            return username;
        }

        public void setUsername(string username)
        {
            this.username = username;
        }

        public string getPassword()
        {
            return password;
        }

        public void setPassword(string password)
        {
            this.password = password;
        }
    }
}
