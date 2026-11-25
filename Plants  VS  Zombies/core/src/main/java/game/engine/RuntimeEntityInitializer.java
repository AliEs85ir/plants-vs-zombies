package game.engine;

import app.enums.SeasonType;
import app.enums.obstacle.ObstacleType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import common.model.Position;
import game.component.physic.BodyFactory;
import game.component.physic.PhysicService;
import game.entity.lawnmower.LawnMower;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.TakeDamageAbility;
import game.save.SaveService;
import game.world.GameWorld;
import game.world.chapter.Chapter;
import game.world.chapter.rule.ChapterRule;
import game.world.playground.Cell;
import graphics.GraphicsEngine;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.GameplayEffects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuntimeEntityInitializer {
    private RuntimeEntityInitializer() {
    }
    public static void initializeExistingEntities(
        GameWorld world,
        PhysicService physicService,
        GraphicsEngine graphicsEngine
    ) {
        if (SaveService.peekPendingRestore() == null) {
            startChapterEntities(world, graphicsEngine, physicService);
        }

        initializeLawnMowers(world, graphicsEngine);

        // TODO -> Add more runtime initializations.
    }

    public static void startChapterEntities(
        GameWorld world,
        GraphicsEngine graphicsEngine,
        PhysicService physicService
    ) {
        Chapter chapter = world.getCurrentChapter();
        if (chapter == null) {
            return;
        }
        for (ChapterRule rule : chapter.getRules()) {
            rule.onChapterStart(world, graphicsEngine, physicService);
        }
        if (chapter.isMinigame() && chapter.getMinigameLogic() != null) {
            chapter.getMinigameLogic().onMinigameStart(world, graphicsEngine, physicService);
        }
    }

    private static void initializeLawnMowers(
        GameWorld world,
        GraphicsEngine graphicsEngine
    ) {
        BodyFactory bodyFactory = BodyFactory.getInstance();
        boolean spawnAppearEffect = SaveService.peekPendingRestore() == null;

        for (LawnMower lawnMower : world.getLawnMowers()) {
            if (lawnMower.getBody() != null) {
                continue;
            }

            float x = (float) lawnMower.getPosition().getX();
            float y = (float) lawnMower.getPosition().getY();

            Body body = bodyFactory.createLawnmowerBody(
                x,
                y,
                LawnMower.LAWNMOWER_WIDTH,
                LawnMower.LAWNMOWER_HEIGHT
            );

            lawnMower.setBody(body);
            body.setUserData(lawnMower);

            graphicsEngine.registerLawnmower(lawnMower);
            if (spawnAppearEffect) {
                GameplayEffects.spawn("mower spawn", lawnMower.getPosition(), EffectLayer.FRONT);
            }
        }
    }





}
