package app.view.collection.data;

import app.enums.plant.PlantName;
import game.entity.plant.upgrade.PlantUpgradePreview;
import game.entity.plant.upgrade.PlantUpgradeView;

public final class CollectionData {
    private CollectionData() {
    }

    public enum Tab {
        PLANTS,
        ZOMBIES
    }

    public enum View {
        HUB,
        GRID,
        DETAILS
    }

    public static final class PlantCardModel {
        public final PlantName plantName;
        public final boolean unlocked;
        public final PlantUpgradeView progress;
        public final String categoryIconId;
        public final int level;

        public PlantCardModel(
            PlantName plantName,
            boolean unlocked,
            PlantUpgradeView progress,
            String categoryIconId,
            int level
        ) {
            this.plantName = plantName;
            this.unlocked = unlocked;
            this.progress = progress;
            this.categoryIconId = categoryIconId == null ? "" : categoryIconId;
            this.level = level;
        }
    }

    public static final class PlantDetailsModel {
        public final PlantName plantName;
        public final boolean unlocked;
        public final boolean boosted;
        public final String displayName;
        public final String description;
        public final String plantFoodDescription;
        public final String categoryLabel;
        public final String categoryIconId;
        public final String tagsLabel;
        public final String detailsBackgroundId;
        public final PlantUpgradePreview preview;
        public final PlantUpgradeView progress;
        public final int unlockPrice;
        public final boolean canAffordUnlock;

        public PlantDetailsModel(
            PlantName plantName,
            boolean unlocked,
            boolean boosted,
            String displayName,
            String description,
            String plantFoodDescription,
            String categoryLabel,
            String categoryIconId,
            String tagsLabel,
            String detailsBackgroundId,
            PlantUpgradePreview preview,
            PlantUpgradeView progress,
            int unlockPrice,
            boolean canAffordUnlock
        ) {
            this.plantName = plantName;
            this.unlocked = unlocked;
            this.boosted = boosted;
            this.displayName = displayName;
            this.description = description;
            this.plantFoodDescription = plantFoodDescription;
            this.categoryLabel = categoryLabel;
            this.categoryIconId = categoryIconId;
            this.tagsLabel = tagsLabel;
            this.detailsBackgroundId = detailsBackgroundId;
            this.preview = preview;
            this.progress = progress;
            this.unlockPrice = Math.max(0, unlockPrice);
            this.canAffordUnlock = canAffordUnlock;
        }
    }

    public static final class ZombieCardModel {
        public final String alias;
        public final boolean seen;

        public ZombieCardModel(String alias, boolean seen) {
            this.alias = alias;
            this.seen = seen;
        }
    }

    public static final class ZombieDetailsModel {
        public final String alias;
        public final boolean seen;
        public final String displayName;
        public final String description;
        public final String hp;
        public final String speed;
        public final String detailsBackgroundId;

        public ZombieDetailsModel(
            String alias,
            boolean seen,
            String displayName,
            String description,
            String hp,
            String speed,
            String detailsBackgroundId
        ) {
            this.alias = alias;
            this.seen = seen;
            this.displayName = displayName;
            this.description = description;
            this.hp = hp;
            this.speed = speed;
            this.detailsBackgroundId = detailsBackgroundId;
        }
    }
}