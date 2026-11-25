package server.quest;

import app.enums.plant.PlantCategory;
import app.enums.plant.PlantName;
import app.enums.quest.QuestName;
import game.entity.quest.Quest;
import game.service.quest.QuestFactory;
import game.service.quest.QuestManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class QuestProgressMapper {
    private QuestProgressMapper() {
    }

    public static QuestManagerSaveDTO toDto(QuestManager manager) {
        if (manager == null) {
            return new QuestManagerSaveDTO(LocalDate.now().toString(), List.of(), List.of());
        }
        String resetDate = manager.getLastResetDate() != null
            ? manager.getLastResetDate().toString()
            : LocalDate.now().toString();
        return new QuestManagerSaveDTO(
            resetDate,
            toDtoList(manager.getActiveQuests()),
            toDtoList(manager.getCompletedQuests())
        );
    }

    public static QuestManager fromDto(QuestManagerSaveDTO dto) {
        QuestManager manager = new QuestManager();
        if (dto == null) {
            QuestFactory.populateNewPlayer(manager);
            return manager;
        }
        if (dto.getLastResetDate() != null && !dto.getLastResetDate().isBlank()) {
            manager.setLastResetDate(LocalDate.parse(dto.getLastResetDate()));
        }
        populate(manager.getActiveQuests(), dto.getActiveQuests());
        populate(manager.getCompletedQuests(), dto.getCompletedQuests());
        if (manager.getActiveQuests().isEmpty() && manager.getCompletedQuests().isEmpty()) {
            QuestFactory.populateNewPlayer(manager);
        }
        manager.ensureEventQuests();
        return manager;
    }

    private static List<QuestStateDTO> toDtoList(List<Quest> quests) {
        List<QuestStateDTO> dtos = new ArrayList<>();
        if (quests == null) {
            return dtos;
        }
        for (Quest quest : quests) {
            dtos.add(new QuestStateDTO(
                quest.getName().name(),
                quest.getCondition() != null ? quest.getCondition().getCurrentProgress() : 0,
                quest.getCurrentTierIndex(),
                quest.isCompleted(),
                quest.getTargetPlant(),
                quest.getTargetCategory(),
                quest.getTargetValue()
            ));
        }
        return dtos;
    }

    private static void populate(List<Quest> target, List<QuestStateDTO> dtos) {
        if (dtos == null || target == null) {
            return;
        }
        for (QuestStateDTO dto : dtos) {
            Quest quest = recreate(dto);
            if (quest != null) {
                restoreState(quest, dto);
                target.add(quest);
            }
        }
    }

    private static Quest recreate(QuestStateDTO dto) {
        if (dto == null || dto.getQuestName() == null) {
            return null;
        }
        try {
            QuestName name = QuestName.valueOf(dto.getQuestName());
            return switch (name) {
                case DAILY_SUN_COLLECTOR -> QuestFactory.createDailySunCollector();
                case CHAPTER_HUNTER -> QuestFactory.createChapterHunter(valueOr(dto.getTargetValue(), 1));
                case PLANT_EXPERT -> QuestFactory.createPlantExpert(parsePlantName(dto.getTargetPlant()));
                case CACTUS_ONLY -> QuestFactory.createCactusOnly();
                case ECONOMICAL_GARDENER -> QuestFactory.createEconomicalGardener(valueOr(dto.getTargetValue(), 3));
                case DEFENSE_MASTER -> QuestFactory.createDefenseMaster();
                case QUICK_ACTION -> QuestFactory.createQuickAction();
                case DEMOLITION_EXPERT -> QuestFactory.createDemolitionExpert();
                case SYMMETRY -> QuestFactory.createSymmetry();
                case FAMILY_HUNTER -> QuestFactory.createFamilyHunter(parsePlantCategory(dto.getTargetCategory()));
                case LIMITED_BLOOM -> QuestFactory.createLimitedBloom(parsePlantCategory(dto.getTargetCategory()));
                case DAY_OR_NIGHT -> QuestFactory.createDayOrNight();
                case WIN_STREAK -> QuestFactory.createWinStreak();
                case ALMOST_VICTORY -> QuestFactory.createAlmostVictory();
                case OCD_MASTER -> QuestFactory.createOcdMaster();
                case CLOUDY_DAY -> QuestFactory.createCloudyDay();
                case ONE_COLUMN_LESS -> QuestFactory.createOneColumnLess(valueOr(dto.getTargetValue(), 8));
                case DEFENSELESS_ROW -> QuestFactory.createDefenselessRow(valueOr(dto.getTargetValue(), 4));
                case DEFENSELESS_CROSS -> QuestFactory.createDefenselessCross(valueOr(dto.getTargetValue(), 2));
                case LAWN_MOWER_TIME -> QuestFactory.createLawnMowerTime(valueOr(dto.getTargetValue(), 10));
            };
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static void restoreState(Quest quest, QuestStateDTO dto) {
        quest.restoreProgress(dto.getCurrentProgress(), dto.getCurrentTierIndex(), dto.isCompleted());
    }

    private static int valueOr(int value, int fallback) {
        return value != -1 ? value : fallback;
    }

    private static PlantName parsePlantName(String raw) {
        if (raw != null) {
            try {
                return PlantName.valueOf(raw);
            } catch (Exception ignored) {
            }
        }
        return PlantName.values()[0];
    }

    private static PlantCategory parsePlantCategory(String raw) {
        if (raw != null) {
            try {
                return PlantCategory.valueOf(raw);
            } catch (Exception ignored) {
            }
        }
        return PlantCategory.values()[0];
    }
}
