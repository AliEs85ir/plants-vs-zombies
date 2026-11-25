package server.network;

import app.enums.Color;
import app.enums.greenhouse.GreenhousePlantType;
import app.enums.plant.PlantCategory;
import app.enums.plant.PlantName;
import app.enums.quest.QuestName;
import app.model.News;
import app.model.greenhouse.BoostPlant;
import app.model.greenhouse.Greenhouse;
import app.model.greenhouse.GreenhousePlant;
import app.model.greenhouse.MarigoldPlant;
import app.model.shop.DailyOffer;
import app.model.shop.Shop;
import app.model.user.GameProgress;
import app.model.user.User;
import com.google.gson.*;
import game.entity.plant.PlantLevel;
import game.entity.plant.seedpacket.SeedBank;
import game.entity.plantfood.PlantFoodInventory;
import game.entity.quest.Quest;
import game.service.quest.QuestFactory;
import game.service.quest.QuestManager;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public final class GameStateSerializer {
    public static final int STORAGE_VERSION = 1;

    private GameStateSerializer() {
    }

    public static class ShopPersistState {
        public String dailyOfferDate;
        public String dailyOfferProductId;
        public String dailyOfferPlant;
        public int dailyOfferDiscount;
        public boolean dailyOfferSold;
    }

    public static class GameStateSnapshot {
        public int version = STORAGE_VERSION;
        public String username;
        public String nickname;
        public String profilePictureId;
        public String email;
        public String gender;
        public Integer age;
        public Integer coins;
        public Integer gems;
        public Integer mioPoint;
        public Integer gamesPlayed;
        public Integer difficulty;
        public QuestManager questManager;
        public Greenhouse greenhouse;
        public GameProgress gameProgress;
        public List<News> newsList;
        public PlantFoodInventory foodInventory;
        public Map<PlantName, Boolean> plantBoostStatus;
        public Map<PlantName, PlantLevel> plantLevelMap;
        public SeedBank seedBank;
        public ShopPersistState shop;
    }

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()

        .registerTypeAdapter(
            LocalDateTime.class,
            (JsonSerializer<LocalDateTime>) (src, type, context) ->
                new JsonPrimitive(src.toString())
        )
        .registerTypeAdapter(
            LocalDateTime.class,
            (JsonDeserializer<LocalDateTime>) (json, type, context) ->
                LocalDateTime.parse(json.getAsString())
        )

        .registerTypeAdapter(
            LocalDate.class,
            (JsonSerializer<LocalDate>) (src, type, context) ->
                new JsonPrimitive(src.toString())
        )
        .registerTypeAdapter(
            LocalDate.class,
            (JsonDeserializer<LocalDate>) (json, type, context) ->
                LocalDate.parse(json.getAsString())
        )

        .registerTypeAdapter(
            LocalTime.class,
            (JsonSerializer<LocalTime>) (src, type, context) ->
                new JsonPrimitive(src.toString())
        )
        .registerTypeAdapter(
            LocalTime.class,
            (JsonDeserializer<LocalTime>) (json, type, context) ->
                LocalTime.parse(json.getAsString())
        )

        .registerTypeAdapter(QuestManager.class, new QuestManagerAdapter())
        .registerTypeAdapter(GreenhousePlant.class, new GreenhousePlantAdapter())

        .create();

    public static String serialize(User user) {
        if (user == null) {
            return null;
        }

        GameStateSnapshot snapshot = new GameStateSnapshot();
        snapshot.version = STORAGE_VERSION;
        snapshot.username = user.getUsername();
        snapshot.nickname = user.getNickname();
        snapshot.profilePictureId = user.getProfilePictureId();
        snapshot.email = user.getEmail();
        snapshot.gender = user.getGender();
        snapshot.age = user.getAge();
        snapshot.coins = Math.max(0, user.getCoins());
        snapshot.gems = Math.max(0, user.getGems());
        snapshot.mioPoint = Math.max(0, user.getMioPoint());
        snapshot.gamesPlayed = Math.max(0, user.getGamesPlayed());
        snapshot.difficulty = user.getDifficultyLevel();
        snapshot.questManager = user.getQuestManager();
        snapshot.greenhouse = user.getGreenhouse();
        snapshot.gameProgress = user.getGameProgress();
        snapshot.newsList = user.getNewsList();
        snapshot.foodInventory = user.getFoodInventory();
        snapshot.plantBoostStatus = user.getPlantBoostStatus();
        snapshot.plantLevelMap = user.getPlantLevelMap();
        snapshot.seedBank = user.getSeedBank();
        snapshot.shop = serializeShop(user.getShop());

        return GSON.toJson(snapshot);
    }

    public static String readProfilePictureId(String gameStateJson) {
        if (gameStateJson == null || gameStateJson.isBlank()) {
            return null;
        }
        try {
            JsonObject root = JsonParser.parseString(gameStateJson).getAsJsonObject();
            JsonElement picture = root.get("profilePictureId");
            if (picture == null || picture.isJsonNull() || !picture.isJsonPrimitive()) {
                return null;
            }
            String id = picture.getAsString();
            return id == null || id.isBlank() ? null : id.trim();
        } catch (Exception exception) {
            return null;
        }
    }

    public static QuestManager readQuestManager(String gameStateJson) {
        if (gameStateJson == null || gameStateJson.isBlank()) {
            return null;
        }
        try {
            JsonObject root = JsonParser.parseString(gameStateJson).getAsJsonObject();
            JsonElement quests = root.get("questManager");
            if (quests == null || quests.isJsonNull()) {
                return null;
            }
            return GSON.fromJson(quests, QuestManager.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static String replaceQuestManager(String gameStateJson, QuestManager questManager) {
        JsonObject root;
        if (gameStateJson == null || gameStateJson.isBlank()) {
            root = new JsonObject();
            root.addProperty("version", STORAGE_VERSION);
        } else {
            try {
                JsonElement parsed = JsonParser.parseString(gameStateJson);
                root = parsed.isJsonObject() ? parsed.getAsJsonObject() : new JsonObject();
            } catch (Exception e) {
                root = new JsonObject();
                root.addProperty("version", STORAGE_VERSION);
            }
        }
        if (questManager == null) {
            root.remove("questManager");
        } else {
            root.add("questManager", GSON.toJsonTree(questManager, QuestManager.class));
        }
        return GSON.toJson(root);
    }

    public static boolean applyToUser(User user, String gameStateJson) {
        if (user == null || gameStateJson == null || gameStateJson.isBlank()) {
            return false;
        }

        try {
            GameStateSnapshot snapshot =
                GSON.fromJson(gameStateJson, GameStateSnapshot.class);

            if (snapshot == null) {
                return false;
            }

            applySnapshot(user, snapshot);
            return true;
        } catch (Exception e) {
            System.err.println(
                "GameStateSerializer: could not load game state: " +
                    e.getMessage()
            );
            return false;
        }
    }

    public static void applySnapshot(User user, GameStateSnapshot snapshot) {
        if (user == null || snapshot == null) {
            return;
        }

        // Account identity fields are server-authoritative when already present.
        // Local progress may hydrate blank identity fields (e.g. local session restore
        // before server sync), but must never overwrite non-blank server values.
        if ((user.getNickname() == null || user.getNickname().isBlank())
            && snapshot.nickname != null
            && !snapshot.nickname.isBlank()) {
            user.setNickname(snapshot.nickname);
        }
        if ((user.getEmail() == null || user.getEmail().isBlank())
            && snapshot.email != null
            && !snapshot.email.isBlank()) {
            user.setEmail(snapshot.email);
        }
        if ((user.getGender() == null || user.getGender().isBlank())
            && snapshot.gender != null
            && !snapshot.gender.isBlank()) {
            user.setGender(snapshot.gender);
        }
        if (user.getAge() < 0 && snapshot.age != null && snapshot.age >= 0) {
            user.setAge(snapshot.age);
        }
        if (snapshot.profilePictureId != null
            && (user.getProfilePictureId() == null || user.getProfilePictureId().isBlank())) {
            user.setProfilePictureId(snapshot.profilePictureId);
        }

        if (snapshot.coins != null) {
            user.setCoins(Math.max(0, snapshot.coins));
        }

        if (snapshot.gems != null) {
            user.setGems(Math.max(0, snapshot.gems));
        }

        if (snapshot.mioPoint != null) {
            user.setMioPoint(Math.max(0, snapshot.mioPoint));
        }

        if (snapshot.gamesPlayed != null) {
            user.setGamesPlayed(Math.max(0, snapshot.gamesPlayed));
        }

        if (snapshot.difficulty != null && snapshot.difficulty >= 1) {
            user.setDifficultyLevel(snapshot.difficulty);
        }

        if (snapshot.questManager != null) {
            user.setQuestManager(snapshot.questManager);
        }

        if (snapshot.greenhouse != null) {
            user.setGreenhouse(snapshot.greenhouse);
        }

        if (snapshot.gameProgress != null) {
            snapshot.gameProgress.ensureStageEntries();
            user.setGameProgress(snapshot.gameProgress);
        }

        if (snapshot.newsList != null) {
            user.setNewsList(snapshot.newsList);
        }

        if (snapshot.foodInventory != null) {
            if (snapshot.foodInventory.getAmount() < 0) {
                snapshot.foodInventory.setAmount(0);
            }
            user.setFoodInventory(snapshot.foodInventory);
        }

        if (snapshot.plantBoostStatus != null) {
            user.setPlantBoostStatus(snapshot.plantBoostStatus);
        }

        if (snapshot.plantLevelMap != null) {
            user.setPlantLevelMap(snapshot.plantLevelMap);
        }

        if (snapshot.seedBank != null) {
            user.setSeedBank(snapshot.seedBank);
        }

        if (snapshot.shop != null) {
            applyShopState(user.getShop(), snapshot.shop);
        }
    }

    private static ShopPersistState serializeShop(Shop shop) {
        if (shop == null) {
            return null;
        }

        DailyOffer offer = shop.getPersistedDailyOffer();
        if (offer == null || offer.getProductId() == null) {
            return null;
        }

        ShopPersistState state = new ShopPersistState();
        state.dailyOfferDate = offer.getDate() == null ? null : offer.getDate().toString();
        state.dailyOfferProductId = offer.getProductId();
        state.dailyOfferDiscount = offer.getDiscountPercent();
        state.dailyOfferSold = offer.isSold();
        return state;
    }

    private static void applyShopState(Shop shop, ShopPersistState state) {
        if (shop == null || state == null) {
            return;
        }

        if (state.dailyOfferDate == null) {
            return;
        }

        try {
            LocalDate date = LocalDate.parse(state.dailyOfferDate);
            String productId = state.dailyOfferProductId;
            if (productId == null && state.dailyOfferPlant != null) {
                productId = "random_seed_packet";
            }
            if (productId == null) {
                return;
            }
            int discount = state.dailyOfferDiscount > 0 ? state.dailyOfferDiscount : 30;
            shop.restoreDailyOffer(productId, date, discount, state.dailyOfferSold);
        } catch (Exception e) {
            System.err.println(
                "GameStateSerializer: could not restore shop state: " +
                    e.getMessage()
            );
        }
    }

    private static class GreenhousePlantAdapter
        implements JsonSerializer<GreenhousePlant>,
        JsonDeserializer<GreenhousePlant> {

        @Override
        public JsonElement serialize(
            GreenhousePlant source,
            Type typeOfSource,
            JsonSerializationContext context
        ) {
            JsonObject object = new JsonObject();

            object.addProperty(
                "plantedAt",
                source.getPlantedAt().toString()
            );

            object.addProperty(
                "readyAt",
                source.getReadyAt().toString()
            );

            if (source instanceof BoostPlant boostPlant) {
                object.addProperty(
                    "type",
                    GreenhousePlantType.BOOST.name()
                );

                object.addProperty(
                    "plant",
                    boostPlant.getPlant().name()
                );
            } else if (source instanceof MarigoldPlant marigoldPlant) {
                object.addProperty(
                    "type",
                    GreenhousePlantType.MARIGOLD.name()
                );

                object.addProperty(
                    "color",
                    marigoldPlant.getColor().name()
                );

                object.addProperty(
                    "coinValue",
                    marigoldPlant.getCoinValue()
                );
            }

            return object;
        }

        @Override
        public GreenhousePlant deserialize(
            JsonElement json,
            Type typeOfTarget,
            JsonDeserializationContext context
        ) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();

            GreenhousePlantType type = GreenhousePlantType.valueOf(
                object.get("type").getAsString()
            );

            LocalDateTime plantedAt = LocalDateTime.parse(
                object.get("plantedAt").getAsString()
            );

            LocalDateTime readyAt = LocalDateTime.parse(
                object.get("readyAt").getAsString()
            );

            if (type == GreenhousePlantType.BOOST) {
                PlantName plant = PlantName.valueOf(
                    object.get("plant").getAsString()
                );

                return new BoostPlant(plant, plantedAt, readyAt);
            }

            if (type == GreenhousePlantType.MARIGOLD) {
                Color color = Color.valueOf(
                    object.get("color").getAsString()
                );

                int coinValue = object.get("coinValue").getAsInt();

                return new MarigoldPlant(
                    color,
                    coinValue,
                    plantedAt,
                    readyAt
                );
            }

            throw new JsonParseException(
                "Unknown GreenhousePlantType."
            );
        }
    }

    private static class QuestManagerAdapter
        implements JsonSerializer<QuestManager>,
        JsonDeserializer<QuestManager> {

        @Override
        public JsonElement serialize(
            QuestManager source,
            Type typeOfSource,
            JsonSerializationContext context
        ) {
            JsonObject root = new JsonObject();

            root.addProperty(
                "lastResetDate",
                source.getLastResetDate() == null
                    ? null
                    : source.getLastResetDate().toString()
            );

            JsonArray activeQuests = new JsonArray();

            for (Quest quest : source.getActiveQuests()) {
                activeQuests.add(serializeQuest(quest));
            }

            JsonArray completedQuests = new JsonArray();

            for (Quest quest : source.getCompletedQuests()) {
                completedQuests.add(serializeQuest(quest));
            }

            root.add("activeQuests", activeQuests);
            root.add("completedQuests", completedQuests);

            return root;
        }

        @Override
        public QuestManager deserialize(
            JsonElement json,
            Type typeOfTarget,
            JsonDeserializationContext context
        ) throws JsonParseException {
            JsonObject root = json.getAsJsonObject();

            QuestManager manager = new QuestManager();

            JsonElement lastResetDateElement = root.get("lastResetDate");

            if (lastResetDateElement != null &&
                !lastResetDateElement.isJsonNull()) {

                manager.setLastResetDate(
                    LocalDate.parse(lastResetDateElement.getAsString())
                );
            }

            JsonArray activeQuests = root.getAsJsonArray("activeQuests");

            if (activeQuests != null) {
                for (JsonElement element : activeQuests) {
                    Quest quest = deserializeQuest(element.getAsJsonObject());

                    if (quest != null) {
                        manager.getActiveQuests().add(quest);
                    }
                }
            }

            JsonArray completedQuests =
                root.getAsJsonArray("completedQuests");

            if (completedQuests != null) {
                for (JsonElement element : completedQuests) {
                    Quest quest = deserializeQuest(element.getAsJsonObject());

                    if (quest != null) {
                        manager.getCompletedQuests().add(quest);
                    }
                }
            }

            manager.ensureEventQuests();
            return manager;
        }

        private JsonObject serializeQuest(Quest quest) {
            JsonObject object = new JsonObject();

            object.addProperty(
                "questName",
                quest.getName().name()
            );

            object.addProperty(
                "currentProgress",
                quest.getCondition() == null
                    ? 0
                    : quest.getCondition().getCurrentProgress()
            );

            object.addProperty(
                "currentTierIndex",
                quest.getCurrentTierIndex()
            );

            object.addProperty(
                "completed",
                quest.isCompleted()
            );

            object.addProperty(
                "targetPlant",
                quest.getTargetPlant()
            );

            object.addProperty(
                "targetCategory",
                quest.getTargetCategory()
            );

            object.addProperty(
                "targetValue",
                quest.getTargetValue()
            );

            return object;
        }

        private Quest deserializeQuest(JsonObject object) {
            if (object == null || !object.has("questName")) {
                return null;
            }

            try {
                QuestName questName = QuestName.valueOf(
                    object.get("questName").getAsString()
                );

                String targetPlant = getAsString(object, "targetPlant");
                String targetCategory =
                    getAsString(object, "targetCategory");

                int targetValue =
                    getAsInt(object, "targetValue", -1);

                int currentProgress =
                    getAsInt(object, "currentProgress", 0);

                int currentTierIndex =
                    getAsInt(object, "currentTierIndex", 0);

                boolean completed =
                    getAsBoolean(object, "completed", false);

                Quest quest = recreateQuest(
                    questName,
                    targetPlant,
                    targetCategory,
                    targetValue
                );

                if (quest == null) {
                    return null;
                }

                restoreQuestState(
                    quest,
                    currentProgress,
                    currentTierIndex,
                    completed
                );

                return quest;
            } catch (Exception ignored) {
                return null;
            }
        }

        private Quest recreateQuest(
            QuestName name,
            String targetPlant,
            String targetCategory,
            int targetValue
        ) {
            switch (name) {
                case DAILY_SUN_COLLECTOR:
                    return QuestFactory.createDailySunCollector();

                case CHAPTER_HUNTER:
                    return QuestFactory.createChapterHunter(
                        targetValue != -1 ? targetValue : 1
                    );

                case PLANT_EXPERT:
                    return QuestFactory.createPlantExpert(
                        parsePlantName(targetPlant)
                    );

                case CACTUS_ONLY:
                    return QuestFactory.createCactusOnly();

                case ECONOMICAL_GARDENER:
                    return QuestFactory.createEconomicalGardener(
                        targetValue != -1 ? targetValue : 3
                    );

                case DEFENSE_MASTER:
                    return QuestFactory.createDefenseMaster();

                case QUICK_ACTION:
                    return QuestFactory.createQuickAction();

                case DEMOLITION_EXPERT:
                    return QuestFactory.createDemolitionExpert();

                case SYMMETRY:
                    return QuestFactory.createSymmetry();

                case FAMILY_HUNTER:
                    return QuestFactory.createFamilyHunter(
                        parsePlantCategory(targetCategory)
                    );

                case LIMITED_BLOOM:
                    return QuestFactory.createLimitedBloom(
                        parsePlantCategory(targetCategory)
                    );

                case DAY_OR_NIGHT:
                    return QuestFactory.createDayOrNight();

                case WIN_STREAK:
                    return QuestFactory.createWinStreak();

                case ALMOST_VICTORY:
                    return QuestFactory.createAlmostVictory();

                case OCD_MASTER:
                    return QuestFactory.createOcdMaster();

                case CLOUDY_DAY:
                    return QuestFactory.createCloudyDay();

                case ONE_COLUMN_LESS:
                    return QuestFactory.createOneColumnLess(
                        targetValue != -1 ? targetValue : 8
                    );

                case DEFENSELESS_ROW:
                    return QuestFactory.createDefenselessRow(
                        targetValue != -1 ? targetValue : 4
                    );

                case DEFENSELESS_CROSS:
                    return QuestFactory.createDefenselessCross(
                        targetValue != -1 ? targetValue : 2
                    );

                case LAWN_MOWER_TIME:
                    return QuestFactory.createLawnMowerTime(
                        targetValue != -1 ? targetValue : 10
                    );

                default:
                    return null;
            }
        }

        private void restoreQuestState(
            Quest quest,
            int currentProgress,
            int currentTierIndex,
            boolean completed
        ) {
            quest.restoreProgress(currentProgress, currentTierIndex, completed);
        }

        private String getAsString(
            JsonObject object,
            String memberName
        ) {
            JsonElement element = object.get(memberName);

            if (element == null || element.isJsonNull()) {
                return null;
            }

            return element.getAsString();
        }

        private int getAsInt(
            JsonObject object,
            String memberName,
            int defaultValue
        ) {
            JsonElement element = object.get(memberName);

            if (element == null || element.isJsonNull()) {
                return defaultValue;
            }

            return element.getAsInt();
        }

        private boolean getAsBoolean(
            JsonObject object,
            String memberName,
            boolean defaultValue
        ) {
            JsonElement element = object.get(memberName);

            if (element == null || element.isJsonNull()) {
                return defaultValue;
            }

            return element.getAsBoolean();
        }

        private PlantName parsePlantName(String raw) {
            if (raw != null) {
                try {
                    return PlantName.valueOf(raw);
                } catch (Exception ignored) {
                }
            }

            return PlantName.values()[0];
        }

        private PlantCategory parsePlantCategory(String raw) {
            if (raw != null) {
                try {
                    return PlantCategory.valueOf(raw);
                } catch (Exception ignored) {
                }
            }

            return PlantCategory.values()[0];
        }
    }
}
