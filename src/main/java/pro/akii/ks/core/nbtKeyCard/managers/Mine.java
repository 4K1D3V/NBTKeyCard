package pro.akii.ks.core.nbtKeyCard.managers;

import org.bukkit.World;

public class Mine {
    private final String name, access, requiredNbtKey, requiredNbtValue, code;
    private final World world;
    private final int minX, minY, minZ, maxX, maxY, maxZ;
    private final double shopPrice;

    public Mine(String name, World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
                String access, String requiredNbtKey, String requiredNbtValue, String code, double shopPrice) {
        this.name = name;
        this.world = world;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.access = access;
        this.requiredNbtKey = requiredNbtKey;
        this.requiredNbtValue = requiredNbtValue;
        this.code = code;
        this.shopPrice = shopPrice;
    }

    public String getName() { return name; }
    public String getAccess() { return access; }
    public String getRequiredNbtKey() { return requiredNbtKey; }
    public String getRequiredNbtValue() { return requiredNbtValue; }
    public String getCode() { return code; }
    public World getWorld() { return world; }
    public int getMinX() { return minX; }
    public int getMinY() { return minY; }
    public int getMinZ() { return minZ; }
    public int getMaxX() { return maxX; }
    public int getMaxY() { return maxY; }
    public int getMaxZ() { return maxZ; }
    public double getShopPrice() { return shopPrice; }
}