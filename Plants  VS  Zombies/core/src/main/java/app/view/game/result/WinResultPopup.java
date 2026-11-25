package app.view.game.result;

import app.enums.plant.PlantName;
import app.model.App;
import app.model.user.User;
import app.view.mainmenu.MenuTextures;
import app.view.quest.QuestCardRow;
import app.view.quest.QuestMenuConfig;
import app.view.quest.QuestMenuConfigLoader;
import app.view.ui.SkinPanel;
import app.view.ui.button.ButtonRow;
import app.view.ui.button.ButtonSpec;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.entity.quest.Quest;
import game.world.levelConditions.result.StageResult;
import game.world.levelConditions.result.StageRewardGrant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WinResultPopup extends StageResultPopup {
    public interface Callbacks {
        void onReplay();
        void onBackToStageSelect();
        void onNextStage();
    }

    private final Callbacks callbacks;
    private final List<ResultDropdown> sections = new ArrayList<>();
    private final Map<String, Texture> textures = new HashMap<>();

    public WinResultPopup(StageResult result, Callbacks callbacks) {
        super(result, ResultLayout.win());
        this.callbacks = callbacks != null ? callbacks : new Callbacks() {
            @Override public void onReplay() {}
            @Override public void onBackToStageSelect() {}
            @Override public void onNextStage() {}
        };
        assemble();
    }

    @Override
    protected String titleText() {
        return layout.texts.title;
    }

    @Override
    protected String messageText() {
        if (result.isFirstClear()) {
            return layout.texts.messageFirstClear;
        }
        if (result.isBoughtWin()) {
            return layout.texts.messageBoughtWin;
        }
        return layout.texts.message;
    }

    @Override
    protected void addBody(SkinPanel root) {
        Table dropdownHost = new Table();
        dropdownHost.top().left();
        dropdownHost.defaults().growX().padBottom(layout.dropdownGap);

        addDropdown(dropdownHost, layout.texts.dropdownPlants,
            result.getNewlyUnlockedPlants().size(),
            buildPlantGrid(result.getNewlyUnlockedPlants()));
        addDropdown(dropdownHost, layout.texts.dropdownZombies,
            result.getNewlyDiscoveredZombies().size(),
            buildZombieGrid(result.getNewlyDiscoveredZombies()));
        addDropdown(dropdownHost, layout.texts.dropdownQuests,
            result.getChangedQuests().size(),
            buildQuestList(result.getChangedQuests()));
        addDropdown(dropdownHost, layout.texts.dropdownRewards,
            result.getRewards().size(),
            buildRewardList(result.getRewards()));

        root.add(dropdownHost).growX()
            .padTop(layout.pause.spacing.sectionSpacing)
            .row();
    }

    @Override
    protected ButtonRow createButtons() {
        String nextLabel = result.hasNextStage() ? layout.texts.nextStage : layout.texts.chapterComplete;
        return buildButtons(
            ButtonSpec.of("replay", layout.texts.replay)
                .style(layout.texts.leftButtonStyle)
                .fixedWidth()
                .action(() -> callbacks.onReplay()),
            ButtonSpec.of("back", layout.texts.stageSelect)
                .style(layout.texts.middleButtonStyle)
                .fixedWidth()
                .action(() -> callbacks.onBackToStageSelect()),
            ButtonSpec.of("next", nextLabel)
                .style(layout.texts.rightButtonStyle)
                .fixedWidth()
                .action(() -> callbacks.onNextStage())
        );
    }

    private void addDropdown(Table host, String title, int count, Table content) {
        ResultDropdown section = new ResultDropdown(
            layout, title, count, content, this::closeOthers, this::relayoutPanel);
        sections.add(section);
        host.add(section).growX().row();
    }

    private void closeOthers(ResultDropdown opened) {
        for (ResultDropdown section : sections) {
            if (section != opened) {
                section.collapseQuietly();
            }
        }
    }

    private Table buildPlantGrid(List<PlantName> plants) {
        Table table = new Table();
        table.top().left();
        if (plants == null || plants.isEmpty()) {
            table.add(TextSpec.of(layout.pause.text.statLabel).build(layout.texts.emptyPlants)).left();
            return table;
        }
        int column = 0;
        for (PlantName plant : plants) {
            Table cell = new Table();
            ResultPlantCard card = new ResultPlantCard(plant, layout.cardScale);
            cell.add(card).size(card.getWidth(), card.getHeight()).row();
            cell.add(TextSpec.of(layout.pause.text.statLabel).build(plant.getLabel())).padTop(layout.captionPadTop);
            table.add(cell).pad(layout.cardPad);
            column++;
            if (column % layout.plantColumns == 0) {
                table.row();
            }
        }
        return table;
    }

    private Table buildZombieGrid(List<String> zombies) {
        Table table = new Table();
        table.top().left();
        if (zombies == null || zombies.isEmpty()) {
            table.add(TextSpec.of(layout.pause.text.statLabel).build(layout.texts.emptyZombies)).left();
            return table;
        }
        int column = 0;
        for (String alias : zombies) {
            Table cell = new Table();
            ResultZombieCard card = new ResultZombieCard(alias, layout.cardScale);
            cell.add(card).size(card.getWidth(), card.getHeight()).row();
            cell.add(TextSpec.of(layout.pause.text.statLabel).build(alias)).padTop(layout.captionPadTop);
            table.add(cell).pad(layout.cardPad);
            column++;
            if (column % layout.zombieColumns == 0) {
                table.row();
            }
        }
        return table;
    }

    private Table buildQuestList(List<Quest> quests) {
        Table table = new Table();
        table.defaults().growX().padBottom(layout.questRowGap);
        if (quests == null || quests.isEmpty()) {
            table.add(TextSpec.of(layout.pause.text.statLabel).build(layout.texts.emptyQuests)).left().row();
            return table;
        }
        QuestMenuConfig config = QuestMenuConfigLoader.load();
        for (Quest quest : quests) {
            QuestCardRow row = new QuestCardRow(quest, config, this::texture, () -> claimQuest(quest));
            table.add(row).growX().height(config.card.height).row();
        }
        return table;
    }

    private Table buildRewardList(List<StageRewardGrant> rewards) {
        Table table = new Table();
        table.defaults().growX().padBottom(4f);
        if (rewards == null || rewards.isEmpty()) {
            table.add(TextSpec.of(layout.pause.text.statLabel).build(layout.texts.emptyRewards)).left().row();
            return table;
        }
        for (StageRewardGrant reward : rewards) {
            SkinPanel chip = new SkinPanel(layout.pause.backgrounds.statField);
            chip.pad(layout.rewardChipPadY, layout.rewardChipPadX, layout.rewardChipPadY, layout.rewardChipPadX);
            if (reward.getKind() == StageRewardGrant.Kind.PLANT_UNLOCK && reward.getPlantName() != null) {
                ResultPlantCard card = new ResultPlantCard(reward.getPlantName(), layout.rewardCardScale);
                chip.add(card).size(card.getWidth(), card.getHeight()).padRight(layout.rewardCardGap);
            }
            chip.add(TextSpec.of(layout.pause.text.statValue)
                .color(layout.pause.text.infoColor)
                .build(reward.getLabel())).left().growX();
            table.add(chip).growX().row();
        }
        return table;
    }

    private void claimQuest(Quest quest) {
        User user = App.getCurrentUser();
        if (user == null || user.getQuestManager() == null || quest == null) {
            return;
        }
        user.getQuestManager().claimQuest(quest);
    }

    private Texture texture(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        String key = path.replace('\\', '/');
        if (textures.containsKey(key)) {
            return textures.get(key);
        }
        Texture loaded = MenuTextures.load(key, "WinResultPopup");
        textures.put(key, loaded);
        return loaded;
    }

    @Override
    public boolean remove() {
        for (Texture texture : textures.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textures.clear();
        return super.remove();
    }
}
