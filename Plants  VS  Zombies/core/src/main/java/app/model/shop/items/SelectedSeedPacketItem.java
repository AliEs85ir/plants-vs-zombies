package app.model.shop.items;

import app.enums.plant.PlantName;
import app.model.shop.Price;
import app.model.shop.ShopItem;
import app.model.shop.enums.CurrencyType;
import app.model.user.User;

public class SelectedSeedPacketItem extends ShopItem {

    private PlantName plantName;

    public SelectedSeedPacketItem() {
        this(null);
    }

    public SelectedSeedPacketItem(
        PlantName plantName
    ) {
        super(
            "Selected Seed Packet",
            new Price(5, CurrencyType.GEM)
        );

        this.plantName = plantName;
    }

    public void setPlantName(
        PlantName plantName
    ) {
        this.plantName = plantName;
    }

    public PlantName getPlantName() {
        return plantName;
    }

    @Override
    public String getDisplayName() {
        if (plantName == null) {
            return "Selected Seed Packet";
        }

        return "Seed Packet: " + plantName;
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
            .addPacket(plantName, count);
    }
}
