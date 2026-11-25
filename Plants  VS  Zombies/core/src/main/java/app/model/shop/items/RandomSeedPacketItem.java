package app.model.shop.items;

import app.enums.plant.PlantName;
import app.model.shop.Price;
import app.model.shop.ShopItem;
import app.model.shop.enums.CurrencyType;
import app.model.user.User;

public class RandomSeedPacketItem extends ShopItem {

    private PlantName plantName;

    public RandomSeedPacketItem() {
        this(null);
    }

    public RandomSeedPacketItem(
        PlantName plantName
    ) {
        super(
            "Random Seed Packet",
            new Price(
                1000,
                CurrencyType.COIN
            )
        );

        this.plantName = plantName;
    }

    public PlantName getPlantName() {
        return plantName;
    }

    public void setPlantName(
        PlantName plantName
    ) {
        this.plantName = plantName;
    }

    @Override
    public String getDisplayName() {

        if (plantName == null) {
            return "Random Seed Packet";
        }

        return "Seed Packet: "
            + plantName;
    }

    @Override
    public void apply(
        User user,
        int count
    ) {

        if (plantName == null) {
            return;
        }

        user.getSeedBank()
            .addPacket(
                plantName,
                count
            );
    }
}
