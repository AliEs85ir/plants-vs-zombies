package game.service.Zombie;


import app.enums.zombie.Armor.ArmorFlag;
import app.enums.zombie.Armor.ArmorType;
import app.enums.zombie.ZombieStatTier;
import app.enums.zombie.ZombieStatsType;
import com.badlogic.gdx.Gdx;
import com.google.gson.*;
import common.model.Rect;
import game.entity.registry.ZombieRegistry;
import game.entity.zombie.data.StatsData;
import game.entity.zombie.data.properties.*;
import game.entity.zombie.data.properties.DarkAges.JugglerProperty;
import game.entity.zombie.data.properties.DarkAges.KingProperty;
import game.entity.zombie.data.properties.DarkAges.WizardProperty;
import game.entity.zombie.data.properties.ancientEgypt.ExplorerProperty;
import game.entity.zombie.data.properties.ancientEgypt.RaProperty;
import game.entity.zombie.data.properties.ancientEgypt.TombRaiserProperty;
import game.entity.zombie.data.properties.armor.ArmorProperty;
import game.entity.zombie.data.properties.beach.FishermanProperty;
import game.entity.zombie.data.properties.beach.OctopusProperty;
import game.entity.zombie.data.properties.beach.SnorkelProperty;
import game.entity.zombie.data.properties.iceAge.HunterProperty;
import game.entity.zombie.data.properties.iceAge.IceAgeDodoProperty;
import game.entity.zombie.data.properties.iceAge.TroglobiteProperty;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ZombieJsonParserService {
    private static final Map<String, Class<? extends Property>> CLASS_MAP = new HashMap<>();

    static {
        CLASS_MAP.put("ZombiePropertySheet", OrdinaryZombieProperty.class);
        CLASS_MAP.put("ZombieGargantuarProps", GargantuarProperty.class);
        CLASS_MAP.put("ZombieRaProps", RaProperty.class);
        CLASS_MAP.put("ZombieExplorerProps", ExplorerProperty.class);
        CLASS_MAP.put("ZombieTombRaiserProps", TombRaiserProperty.class);
        CLASS_MAP.put("ZombieIceAgeDodoProps", IceAgeDodoProperty.class);
        CLASS_MAP.put("ZombieIceAgeHunterProps", HunterProperty.class);
        CLASS_MAP.put("ZombieIceAgeTroglobiteProps", TroglobiteProperty.class);
        CLASS_MAP.put("ZombieBeachFishermanProps", FishermanProperty.class);
        CLASS_MAP.put("ZombieBeachOctopusProps", OctopusProperty.class);
        CLASS_MAP.put("ZombieBeachSnorkelProps", SnorkelProperty.class);
        CLASS_MAP.put("ZombieDarkJugglerProps", JugglerProperty.class);
        CLASS_MAP.put("ZombieDarkWizardProps", WizardProperty.class);
        CLASS_MAP.put("ZombieDarkKingProps", KingProperty.class);
        CLASS_MAP.put("ZombieModernAllStarProps", AllStarProperty.class);
        CLASS_MAP.put("ZombieLostCityJaneProps", LostCityProperty.class);
        CLASS_MAP.put("ZombieCrystalSkullProps", CrystalSkullProperty.class);
        CLASS_MAP.put("ZombieProspectorProps", ProspectorProperty.class);
        CLASS_MAP.put("ZombiePianoProps", PianoProperty.class);
        CLASS_MAP.put("ZombieNewspaperProps", NewspaperProperty.class);
        CLASS_MAP.put("ZombieArcadeProps", ArcadeProperty.class);
        CLASS_MAP.put("ZombieBossPropertySheet", BossProperty.class);

        // TODO : I should add other Zombie property classes too .
    }

    public static void loadZombiesFromJson(String filePath) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Property.class, new PropertyDeserializer())
                .registerTypeAdapter(Rect.class, new RectDeserializer())
                .registerTypeAdapter(StatsData.class, new StatsDataDeserializer())
                .registerTypeAdapter(ScalingProperty.class, new ScalingPropertyDeserializer())
                .create();
        try (Reader reader = Gdx.files.internal(filePath).reader()){
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement element : jsonArray) {
                // 4. Deserialize each element into a Property
                Property property = gson.fromJson(element, Property.class);

                // 5. Register it in your ZombieRegistry
                if (property != null) {
                    ZombieRegistry.register(property);
                }
            }
        }
        catch (Exception e){
            System.err.println("Failed to read JSON file: " + e.getMessage());
        }

    }

    public static void loadArmorsFromJson(String filePath) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ArmorProperty.class, new ArmorPropertyDeserializer())
                .create();

        try (Reader reader = Gdx.files.internal(filePath).reader()) {
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement element : jsonArray) {
                ArmorProperty armorProperty = gson.fromJson(element, ArmorProperty.class);
                if (armorProperty != null) {
                    ZombieRegistry.register(armorProperty);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to read armor JSON file: " + e.getMessage());
        }
    }



    private static class PropertyDeserializer implements JsonDeserializer<Property> {
        @Override
        public Property deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            String objClass = jsonObject.get("objclass").getAsString();
            JsonArray aliasesArray = jsonObject.getAsJsonArray("aliases");
            JsonObject objData = jsonObject.getAsJsonObject("objdata");

            Class<? extends Property> targetClass = CLASS_MAP.get(objClass);
            if (targetClass == null) {
                System.out.println("Warning: Unmapped objclass: " + objClass + ". Skipping.");
                return null;
            }

            Property property = context.deserialize(objData, targetClass);


            if (aliasesArray != null && !aliasesArray.isEmpty()) {
                String alias = aliasesArray.get(0).getAsString();
                property.setAlias(alias);
            }

            return property;
        }

    }

    private static class RectDeserializer implements JsonDeserializer<Rect> {
        @Override
        public Rect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            float w = obj.has("mWidth") ? obj.get("mWidth").getAsFloat() : 0;
            float h = obj.has("mHeight") ? obj.get("mHeight").getAsFloat() : 0;
            float x = obj.has("mX") ? obj.get("mX").getAsFloat() : 0;
            float y = obj.has("mY") ? obj.get("mY").getAsFloat() : 0;
            return new Rect(x, y, w, h);
        }
    }

    private static class StatsDataDeserializer implements JsonDeserializer<StatsData> {
        @Override
        public StatsData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            StatsData statsData = new StatsData();
            if (json.isJsonArray()) {
                JsonArray array = json.getAsJsonArray();
                for (JsonElement element : array) {
                    JsonObject obj = element.getAsJsonObject();
                    String typeStr = obj.get("Type").getAsString().toUpperCase();
                    String valStr = obj.get("Value").getAsString().toUpperCase();

                    try {
                        ZombieStatsType type = ZombieStatsType.valueOf(typeStr);
                        char tierLevel = valStr.charAt(valStr.length()-1);
                        String enumName = "TIER_" + tierLevel;
                        ZombieStatTier tier = ZombieStatTier.valueOf(enumName);
                        statsData.addStats(type, tier);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Unrecognized Stat Enum: " + typeStr + " or " + valStr);
                    }
                }
            }
            return statsData;
        }
    }

    // --- NEW: ScalingProperty Deserializer ---
    private static class ScalingPropertyDeserializer implements JsonDeserializer<ScalingProperty> {
        @Override
        public ScalingProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            ScalingProperty scalingProp = new ScalingProperty();
            // Since you haven't provided ScalingData class, here is the skeleton.
            // You will need to parse the array and populate your Map<ScaledPropsKeys, ScalingData> here.
            /*
            if (json.isJsonArray()) {
                for (JsonElement element : json.getAsJsonArray()) {
                     JsonObject obj = element.getAsJsonObject();
                     // Parse Key, Formula, Arg1, Arg2 and put into ScalingProperty
                }
            }
            */
            return scalingProp;
        }
    }

    private static class ArmorPropertyDeserializer implements JsonDeserializer<ArmorProperty> {
        @Override
        public ArmorProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonArray aliasesArray = jsonObject.getAsJsonArray("aliases");
            JsonObject objData = jsonObject.getAsJsonObject("objdata");

            if (objData == null) {
                return null;
            }

            ArmorProperty armorProperty = new ArmorProperty();

            if (aliasesArray != null && !aliasesArray.isEmpty()) {
                armorProperty.setAlias(aliasesArray.get(0).getAsString());
            }

            if (objData.has("ArmorType")) {
                armorProperty.setArmorType(ArmorType.valueOf(objData.get("ArmorType").getAsString()));
            }

            if (objData.has("BaseHealth")) {
                armorProperty.setBaseHealth(objData.get("BaseHealth").getAsInt());
            }

            if (objData.has("ArmorFlags")) {
                ArrayList<ArmorFlag> flags = new ArrayList<>();
                for (JsonElement flagElement : objData.getAsJsonArray("ArmorFlags")) {
                    flags.add(ArmorFlag.valueOf(flagElement.getAsString()));
                }
                armorProperty.setArmorFlags(flags);
            }

            return armorProperty;
        }
    }

}
