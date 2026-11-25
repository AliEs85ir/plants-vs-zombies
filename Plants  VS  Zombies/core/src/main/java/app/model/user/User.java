package app.model.user;

import app.enums.plant.PlantName;
import app.model.News;
import app.model.greenhouse.Greenhouse;
import app.model.shop.Shop;
import app.service.AppService;
import game.entity.plant.PlantLevel;
import game.entity.plant.seedpacket.SeedBank;
import game.entity.plantfood.PlantFoodInventory;
import game.service.quest.QuestManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User{
    private String username;
    private String password;
    private String email;
    private String gender;
    private int securityQuestionNumber;
    private String nickname;
    private String profilePictureId;
    private String securityQuestionAnswer;
    private int age;
    private int gamesPlayed;
    private int coins;
    private int gems;
    private int mioPoint;
    private QuestManager questManager;
    private int difficultyLevel;
    private Greenhouse greenhouse;
    private Shop shop;
    private GameProgress gameProgress;
    private List<News> newsList;
    private PlantFoodInventory foodInventory;

    private Map <PlantName , Boolean> plantBoostStatus;
    private Map <PlantName , PlantLevel> plantLevelMap;

    private SeedBank seedBank;

    public static final int STARTING_GEMS = 150;
    public static final int MIN_DIFFICULTY_LEVEL = 1;
    public static final int MAX_DIFFICULTY_LEVEL = 5;
    public static final int DEFAULT_DIFFICULTY_LEVEL = 3;

    public User(String username, String password,
                String nickname, String email,
                String gender, int securityQuestionNumber,
                String securityQuestionAnswer) {
        this(username, password, nickname, email, gender, securityQuestionNumber, securityQuestionAnswer, 0);
    }

    public User(String username, String password,
                String nickname, String email,
                String gender, int securityQuestionNumber,
                String securityQuestionAnswer,
                int age) {

        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.securityQuestionNumber = securityQuestionNumber;
        this.securityQuestionAnswer = securityQuestionAnswer;
        this.age = age;
        this.gamesPlayed = 0;
        this.gems = STARTING_GEMS;
        this.coins = 0;
        this.difficultyLevel = DEFAULT_DIFFICULTY_LEVEL;
        this.plantBoostStatus = new HashMap<>();
        this.greenhouse = new Greenhouse();
        this.shop = new Shop();
        this.gameProgress = new GameProgress();
        this.foodInventory = new PlantFoodInventory();
        this.seedBank = new SeedBank();
        this.questManager = new QuestManager();
        this.newsList = new ArrayList<>();
        this.plantLevelMap = new HashMap<>();
        applyInitialUnlocks();

    }

    public User() {
        this.gamesPlayed = 0;
        this.gems = STARTING_GEMS;
        this.coins = 0;
        this.difficultyLevel = DEFAULT_DIFFICULTY_LEVEL;
        this.plantBoostStatus = new HashMap<>();
        this.greenhouse = new Greenhouse();
        //this.shop = new Shop();
        this.gameProgress = new GameProgress();
        this.foodInventory = new PlantFoodInventory();
        this.seedBank = new SeedBank();
        this.questManager = new QuestManager();
        this.newsList = new ArrayList<>();
        this.plantLevelMap = new HashMap<>();
        applyInitialUnlocks();

    }

    private void applyInitialUnlocks() {
        app.service.progression.ProgressionService.applyNewUserDefaults(this);
    }

    private void unlockPlants() {
        applyInitialUnlocks();
    }

    public void setMioPoint(int mioPoint) {
        this.mioPoint = mioPoint;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getCoins() {
        return coins;
    }

    public int getGems() {
        return gems;
    }

    public int getMioPoint() {
        return mioPoint;
    }


    public QuestManager getQuestManager() {
        return questManager;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public int getSecurityQuestionNumber() {
        return securityQuestionNumber;
    }

    public void setQuestManager(QuestManager questManager) {
        this.questManager = questManager;
    }

    public String getSecurityQuestionAnswer() {
        return securityQuestionAnswer;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = clampDifficultyLevel(difficultyLevel);
    }

    public static int clampDifficultyLevel(int level) {
        return Math.max(MIN_DIFFICULTY_LEVEL, Math.min(MAX_DIFFICULTY_LEVEL, level));
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfilePictureId() {
        return profilePictureId;
    }

    public void setProfilePictureId(String profilePictureId) {
        this.profilePictureId = profilePictureId == null || profilePictureId.isBlank()
            ? null
            : profilePictureId.trim();
    }

    public Map<PlantName, Boolean> getPlantBoostStatus() {
        return plantBoostStatus;
    }

    public List<PlantName> getUnlockedPlants() {
        return new ArrayList<>(plantBoostStatus.keySet());
    }

    public GameProgress getGameProgress() {
        return gameProgress;
    }

    public Map<PlantName, PlantLevel> getPlantLevelMap() {
        return plantLevelMap;
    }

    public PlantLevel getPlantLevel(PlantName plant) {
        if (plant == null || plantLevelMap == null) {
            return null;
        }
        return plantLevelMap.get(plant);
    }

    public PlantLevel ensurePlantLevel(PlantName plant) {
        if (plant == null) {
            return null;
        }
        if (plantLevelMap == null) {
            plantLevelMap = new HashMap<>();
        }
        return plantLevelMap.computeIfAbsent(plant, PlantLevel::new);
    }

    public SeedBank getSeedBank() {
        return seedBank;
    }

    public void deductCoins(int amount) {
        if (amount < 0) return;
        if (this.coins - amount < 0) return;
        this.coins -= amount;
        AppService.markGameDataDirty();
    }

    public void addCoins(int amount) {
        if (amount <= 0) {
            return;
        }
        this.coins = Math.max(0, this.coins + amount);
        AppService.markGameDataDirty();
    }

    public void setCoins(int coins) {
        if (coins < 0) {
            return;
        }
        this.coins = coins;
        AppService.markGameDataDirty();
    }

    public void unlockPlant(PlantName plant) {
        if (!plantBoostStatus.containsKey(plant) && !plantLevelMap.containsKey(plant)) {
            plantBoostStatus.put(plant, false);
            plantLevelMap.put(plant , new PlantLevel(plant));
        } else if (!plantBoostStatus.containsKey(plant)) {
            plantBoostStatus.put(plant, false);
        } else if (!plantLevelMap.containsKey(plant)) {
            plantLevelMap.put(plant , new PlantLevel(plant));
        }
    }

    public boolean isUnlockPlant(PlantName name)
    {
        if (plantBoostStatus.containsKey(name)) return true;
        return false;
    }

    public List<News> getNewsList() {
        return newsList;
    }

    // Check if it is unlocked?
    public boolean isUnlocked(PlantName plant) {
        return plantBoostStatus.containsKey(plant);
    }

    // Enable Boost
    public void setBoost(PlantName plant, boolean status) {
        if (isUnlocked(plant)) {
            plantBoostStatus.put(plant, status);
        }
        AppService.markGameDataDirty();
    }

    // Checking the status of the kiss
    public boolean isBoosted(PlantName plant) {
        return plantBoostStatus.getOrDefault(plant, false);
    }

    public boolean tryDeductGems(int amount) {
        if (amount <= 0) {
            return true;
        }
        if (this.gems < amount) {
            return false;
        }
        this.gems -= amount;
        AppService.markGameDataDirty();
        return true;
    }

    public void deductGems(int amount) {
        tryDeductGems(amount);
    }

    public void addGems(int amount) {
        if (amount <= 0) {
            return;
        }
        this.gems = Math.max(0, this.gems + amount);
        AppService.markGameDataDirty();
    }

    public void setGems(int gems) {
        if (gems < 0) {
            return;
        }
        this.gems = gems;
        AppService.markGameDataDirty();
    }

    public Greenhouse getGreenhouse() {
        return greenhouse;
    }

    public Shop getShop() {

        if (shop == null) {
            shop = new Shop();
        }

        return shop;
    }

    public PlantFoodInventory getFoodInventory() {
        return foodInventory;
    }

    public boolean recordBestMioPoint(int sessionScore) {
        if (sessionScore <= this.mioPoint) {
            return false;
        }
        this.mioPoint = sessionScore;
        AppService.markGameDataDirty();
        return true;
    }

    public void addNews(News news) {
        newsList.add(news);
        AppService.markGameDataDirty();
    }

    public void unlockRandomPlant() {
        // TODO
        AppService.markGameDataDirty();
    }

    public void incrementGamesPlayed() {
        int played = this.gamesPlayed + 1;
        if (gameProgress != null) {
            played = Math.max(played, gameProgress.getTotalGamesPlayed());
        }
        setGamesPlayed(played);
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = Math.max(0, gamesPlayed);
        AppService.markGameDataDirty();
    }

    public void setSecurityQuestionNumber(int securityQuestionNumber) {
        this.securityQuestionNumber = securityQuestionNumber;
    }

    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

    public void setGreenhouse(Greenhouse greenhouse) {
        this.greenhouse = greenhouse;
        AppService.markGameDataDirty();
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public void setGameProgress(GameProgress gameProgress) {
        this.gameProgress = gameProgress;
        AppService.markGameDataDirty();
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
        AppService.markGameDataDirty();
    }

    public void setFoodInventory(PlantFoodInventory foodInventory) {
        this.foodInventory = foodInventory;
    }

    public void setPlantBoostStatus(Map<PlantName, Boolean> plantBoostStatus) {
        this.plantBoostStatus = plantBoostStatus;
        AppService.markGameDataDirty();
    }

    public void setPlantLevelMap(Map<PlantName, PlantLevel> plantLevelMap) {
        this.plantLevelMap = plantLevelMap;
        AppService.markGameDataDirty();
    }

    public void setSeedBank(SeedBank seedBank) {
        this.seedBank = seedBank;
        AppService.markGameDataDirty();
    }

    public void addGamesPlayed() {
        this.gamesPlayed++;
    }
}
