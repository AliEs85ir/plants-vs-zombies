package game.entity.plant.seedpacket;

import app.enums.plant.PlantName;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SeedBank {

    private final Map<PlantName, SeedPacket> packets;
    private int capacity;

    public SeedBank() {
        this(500);
    }

    public SeedBank(int capacity) {
        this.packets = new HashMap<>();
        this.capacity = capacity;
    }

    public boolean addPacket(PlantName name, int amount) {
        if (amount <= 0) {
            return false;
        }

        if (!packets.containsKey(name) && isFull()) {
            return false;
        }

        packets.computeIfAbsent(name, k -> new SeedPacket(k, 0)).addPacket(amount);
        return true;
    }

    public boolean addPacket(SeedPacket packet) {
        if (packet == null) {
            return false;
        }

        return addPacket(packet.getPlantName(), packet.getAmount());
    }

    public boolean removePacket(PlantName name, int amount) {
        if (amount <= 0) {
            return true;
        }

        SeedPacket seedPacket = packets.get(name);

        if (seedPacket == null) {
            return false;
        }

        if (seedPacket.getAmount() < amount) {
            return false;
        }

        seedPacket.removePacket(amount);

        if (seedPacket.isEmpty()) {
            packets.remove(name);
        }

        return true;
    }

    public int getAmount(PlantName name) {
        SeedPacket packet = packets.get(name);
        return packet != null ? packet.getAmount() : 0;
    }

    public SeedPacket getPacket(PlantName name) {
        return packets.get(name);
    }

    public boolean hasPacket(PlantName name) {
        return getAmount(name) > 0;
    }

    public boolean hasEnough(PlantName name, int amount) {
        return getAmount(name) >= amount;
    }

    public boolean isFull() {
        return packets.size() >= capacity;
    }

    public boolean isEmpty() {
        return packets.isEmpty();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getSize() {
        return packets.size();
    }

    public Map<PlantName, SeedPacket> getPackets() {
        return Collections.unmodifiableMap(packets);
    }

    public void clear() {
        packets.clear();
    }
}
