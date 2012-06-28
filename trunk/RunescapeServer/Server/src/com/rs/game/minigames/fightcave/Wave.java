package com.rs.game.minigames.fightcave;

import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.fightcave.FightCaveNPC;
import com.rs.utils.Misc;
import java.io.Serializable;
import java.util.Random;

public class Wave
    implements Serializable
{

    public Wave()
    {
    }

    public NPC[] getSpawns()
    {
        int npcs[] = new int[6];
        int index = 0;
        int id = stage;
        for(int i = 6; i >= 1; i--)
        {
            int threshold = (1 << i) - 1;
            if(id >= threshold)
            {
                for(int j = 0; j <= id / threshold; j++)
                {
                    npcs[index++] = BASE_NPCS[i - 1] + (i == 6 ? 0 : RANDOM.nextInt(2));
                    id -= threshold;
                }

            }
        }

        FightCaveNPC enemies[] = new FightCaveNPC[index];
        for(int i = 0; i < enemies.length; i++)
        {
            int random = Misc.random(3);
            switch(random)
            {
            case 0: // '\0'
                enemies[i] = new FightCaveNPC(npcs[i], new WorldTile(2399 - Misc.random(3), 5086 - Misc.random(3), 0));
                break;

            case 1: // '\001'
                enemies[i] = new FightCaveNPC(npcs[i], new WorldTile(2399 + Misc.random(3), 5086 + Misc.random(3), 0));
                break;

            case 2: // '\002'
                enemies[i] = new FightCaveNPC(npcs[i], new WorldTile(2399 - Misc.random(3), 5086 + Misc.random(3), 0));
                break;

            case 3: // '\003'
                enemies[i] = new FightCaveNPC(npcs[i], new WorldTile(2399 + Misc.random(3), 5086 - Misc.random(3), 0));
                break;
            }
        }

        return enemies;
    }

    public void setStage(int stage)
    {
        this.stage = stage;
    }

    public int getStage()
    {
        return stage;
    }

    private static final Random RANDOM = new Random();
    private static final int BASE_NPCS[] = {
        2734, 2736, 2739, 2741, 2743, 2745
    };
    private int stage;

}