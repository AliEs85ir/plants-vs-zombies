package game.entity.zombie.part;

import app.model.App;
import game.component.AssetManager;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import graphics.GraphicsEngine;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectManager;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.KinematicAnchor;
import graphics.general.instance.VisibilityController;
import graphics.zombie.ZombieRenderInstance;
import pvz.libpvz.pam.PamPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Hides a body part on the living zombie and spawns that zombie's
 * {@code particles} clip as a kinematic drop, showing only the matching layers.
 */
public final class ZombieBodyPartDropper {
    private static final String PARTICLES_CLIP = "particles";

    private ZombieBodyPartDropper() {
    }

    public static void drop(Zombie zombie, ZombieBodyPart part) {
        if (zombie == null || part == null) {
            return;
        }
        if (App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        GraphicsEngine graphics = App.getRealTimeEngine().getGraphicsEngine();
        ZombieRenderInstance instance = graphics.getZombieInstance(zombie);
        String pamPath = graphics.resolveZombiePamPath(zombie);
        PamPlayer pamPlayer = AssetManager.player;
        if (pamPath == null || pamPlayer == null) {
            return;
        }

        List<String> layerNames = flatten(toNode(pamPlayer.getParts(pamPath)));
        List<String> dropping = ZombieBodyPartLayers.matching(part, layerNames);
        if (dropping.isEmpty()) {
            return;
        }
        game.audio.Sfx.play(game.audio.AudioId.SFX_LIMBS_POP);

        if (instance != null) {
            hideLayers(instance.getVisibilityController(), dropping);
        }
        spawnParticles(graphics.getEffectManager(), zombie, pamPath, part, layerNames);
    }

    private static void hideLayers(VisibilityController controller, List<String> layers) {
        for (String layer : layers) {
            controller.setInvisible(layer);
        }
    }

    private static void spawnParticles(EffectManager effectManager,
                                       Zombie zombie,
                                       String pamPath,
                                       ZombieBodyPart part,
                                       List<String> layerNames) {
        if (effectManager == null) {
            return;
        }
        Map<String, Boolean> visibility = ZombieBodyPartLayers.effectVisibility(part, layerNames);
        KinematicAnchor anchor = ZombieService.getRandomKinematicAnchor(zombie);
        String alias = zombie.getName();
        if (spawnRegistered(effectManager, alias + "_particles", zombie, anchor, visibility)
            || spawnRegistered(effectManager, alias + " HEAD", zombie, anchor, visibility)) {
            effectManager.resolveClips();
            return;
        }
        effectManager.spawnPamClip(
            pamPath,
            PARTICLES_CLIP,
            zombie,
            anchor,
            EffectLayer.DEFAULT,
            visibility
        );
        effectManager.resolveClips();
    }

    private static boolean spawnRegistered(EffectManager effectManager,
                                           String effectId,
                                           Zombie zombie,
                                           KinematicAnchor anchor,
                                           Map<String, Boolean> visibility) {
        if (!effectManager.hasEffect(effectId)) {
            return false;
        }
        EffectRequest<Zombie> request = new EffectRequest<>(effectId, zombie, anchor, null);
        request.setVisibilityOverrides(visibility);
        return effectManager.spawn(request) != null;
    }

    private static ZombieBodyPartLayers.Node toNode(PamPlayer.AnimationPart part) {
        if (part == null) {
            return null;
        }
        List<ZombieBodyPartLayers.Node> children = new ArrayList<>();
        if (part.children != null) {
            for (PamPlayer.AnimationPart child : part.children) {
                ZombieBodyPartLayers.Node node = toNode(child);
                if (node != null) {
                    children.add(node);
                }
            }
        }
        return new ZombieBodyPartLayers.Node(part.name, children);
    }

    private static List<String> flatten(ZombieBodyPartLayers.Node node) {
        List<String> names = new ArrayList<>();
        flatten(node, names);
        return names;
    }

    private static void flatten(ZombieBodyPartLayers.Node node, List<String> names) {
        if (node == null) {
            return;
        }
        if (node.name != null && !node.name.isBlank()) {
            names.add(node.name);
        }
        for (ZombieBodyPartLayers.Node child : node.children) {
            flatten(child, names);
        }
    }
}
