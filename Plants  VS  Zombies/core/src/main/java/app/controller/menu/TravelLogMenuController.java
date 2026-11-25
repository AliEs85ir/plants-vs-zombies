package app.controller.menu;

import app.enums.Menu;
import app.model.App;
import common.Result;
import game.entity.quest.Quest;
import game.world.chapter.Chapter;
import game.world.chapter.ChapterRegistry;

import java.util.List;

public class TravelLogMenuController {

    public Result manageEnteringMinigame(String name) {
        name = name.trim().toUpperCase();

        Chapter chapter = ChapterRegistry.getMinigameChapterByName(name);

        if (chapter == null) {
            return new Result(false, "Chapter " + name + " does not exist.");
        }

        App.getPendingGameSession().clear();
        App.getPendingGameSession().setSelectedChapter(chapter);
        MenuNavigator.goTo(Menu.PlantSelectionMenu);
        return new Result(true, "Entering plant selection for " + name);
    }

    public Result manageShowQuests() {
        List<Quest> activeQuests = App.getCurrentUser().getQuestManager().getQuestsByPriority();

        if (activeQuests.isEmpty()) {
            return new Result(true, "📭 No active quests at the moment.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🌟 ====== ACTIVE QUESTS ====== 🌟\n\n");

        for (Quest quest : activeQuests) {
            sb.append("╔══════════════════════════════════════════════╗\n");
            sb.append("║ 📜 ").append(quest.getName().name()).append("\n");
            sb.append("╠══════════════════════════════════════════════╣\n");
            sb.append("  ⚠️ Priority: ").append(quest.getPriority()).append("\n");
            sb.append("  📌 Title: ").append(quest.getTitle()).append("\n");
            sb.append("  📝 Desc: ").append(quest.getDescription()).append("\n");
            sb.append("  --------------------------------------------\n");

            int progress = quest.getCondition() != null ? quest.getCondition().getCurrentProgress() : 0;

            sb.append("  📊 Progress: ").append(progress).append("\n");
            sb.append("  📈 Tier Index: ").append(quest.getCurrentTierIndex()).append("\n");
            sb.append("  ✅ Completed: ").append(quest.isCompleted() ? "Yes" : "No").append("\n");

            if (quest.getTargetPlant() != null && !quest.getTargetPlant().isEmpty()) {
                sb.append("  🌿 Target Plant: ").append(quest.getTargetPlant()).append("\n");
            }

            if (quest.getTargetCategory() != null && !quest.getTargetCategory().isEmpty()) {
                sb.append("  🏷️ Target Category: ").append(quest.getTargetCategory()).append("\n");
            }

            if (quest.getTargetValue() != -1) {
                sb.append("  🎯 Target Value: ").append(quest.getTargetValue()).append("\n");
            }

            sb.append("╚══════════════════════════════════════════════╝\n\n");
        }

        return new Result(true, sb.toString());
    }
}
