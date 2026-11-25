package game.service.lawnmower;

import common.model.Position;
import game.entity.lawnmower.LawnMower;
import game.entity.zombie.Zombie;
import game.service.quest.QuestSignals;
import game.world.GameWorld;

import java.util.ArrayList;

public class LawnMowerService
{
    private final GameWorld gameWorld;
    private final ArrayList<LawnMower> lawnMowers;
    public LawnMowerService(GameWorld gameWorld , ArrayList<LawnMower> lawnMowers) {
        this.gameWorld = gameWorld;
        this.lawnMowers = lawnMowers;
    }

    public void update(long currentTick)
    {
        boolean active = false;
        Zombie targetZombie = null;
        ArrayList<Zombie> zombies = new ArrayList<>();
        if (active)
        {
            int row = 0;
            Position position = targetZombie.getPosition();
            position.setX(2);
            if (gameWorld.getGrid().getCellAt(position) == null) return;
           // row = (int)gameWorld.getGrid().getCellAt(position).getPosition().getY();
            LawnMower lawnMower = getLawnMowerInRow(row);
            if (lawnMower != null)
            {
                destroyZombiesInRow(gameWorld , row);
                lawnMower.trigger();
                gameWorld.addEvent("The lawnmower is activated!");
            }
        }
    }

    private void destroyZombiesInRow(GameWorld gameWorld , int row)
    {
        int killedZombiesCount = 0;
        for (Zombie zombie: gameWorld.getZombiesInRow(row))
        {
            zombie.die();
            killedZombiesCount++;
        }
        QuestSignals.lawnMowerKilled(killedZombiesCount);
    }

    private LawnMower getLawnMowerInRow(int row){
        for (LawnMower lawnMower: lawnMowers)
        {
            if (lawnMower.getRow() == row && !lawnMower.isUsed()) return lawnMower;
        }
        return null;
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }
}
