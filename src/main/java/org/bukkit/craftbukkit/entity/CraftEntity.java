package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.server.*;
import net.minecraft.util.DamageSource;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public abstract class CraftEntity implements org.bukkit.entity.Entity {
    private static PermissibleBase perm;

    protected final CraftServer server;
    protected Entity entity;
    private EntityDamageEvent lastDamageEvent;

    public CraftEntity(final CraftServer server, final Entity entity) {
        this.server = server;
        this.entity = entity;
    }

    public static CraftEntity getEntity(CraftServer server, Entity entity) {
        /**
         * Order is *EXTREMELY* important -- keep it right! =D
         */
        if (entity instanceof EntityLivingBase) {
            // Players
            if (entity instanceof EntityPlayer) {
                if (entity instanceof EntityPlayerMP) { return new CraftPlayer(server, (EntityPlayerMP) entity); }
                else { return new CraftHumanEntity(server, (EntityPlayer) entity); }
            }
            // Water Animals
            else if (entity instanceof EntityWaterMob) {
                if (entity instanceof EntitySquid) { return new CraftSquid(server, (EntitySquid) entity); }
                else { return new CraftWaterMob(server, (EntityWaterMob) entity); }
            }
            else if (entity instanceof EntityCreature) {
                // Animals
                if (entity instanceof EntityAnimal) {
                    if (entity instanceof EntityChicken) { return new CraftChicken(server, (EntityChicken) entity); }
                    else if (entity instanceof EntityCow) {
                        if (entity instanceof EntityMooshroom) { return new CraftMushroomCow(server, (EntityMooshroom) entity); }
                        else { return new CraftCow(server, (EntityCow) entity); }
                    }
                    else if (entity instanceof EntityPig) { return new CraftPig(server, (EntityPig) entity); }
                    else if (entity instanceof EntityTameable) {
                        if (entity instanceof EntityWolf) { return new CraftWolf(server, (EntityWolf) entity); }
                        else if (entity instanceof EntityOcelot) { return new CraftOcelot(server, (EntityOcelot) entity); }
                    }
                    else if (entity instanceof EntitySheep) { return new CraftSheep(server, (EntitySheep) entity); }
                    else if (entity instanceof EntityHorse) { return new CraftHorse(server, (EntityHorse) entity); }
                    else if (entity instanceof EntityRabbit) { return new CraftRabbit(server, (EntityRabbit) entity); }
                    else  { return new CraftAnimals(server, (EntityAnimal) entity); }
                }
                // Monsters
                else if (entity instanceof EntityMob) {
                    if (entity instanceof EntityZombie) {
                        if (entity instanceof EntityPigZombie) { return new CraftPigZombie(server, (EntityPigZombie) entity); }
                        else { return new CraftZombie(server, (EntityZombie) entity); }
                    }
                    else if (entity instanceof EntityCreeper) { return new CraftCreeper(server, (EntityCreeper) entity); }
                    else if (entity instanceof EntityEnderman) { return new CraftEnderman(server, (EntityEnderman) entity); }
                    else if (entity instanceof EntitySilverfish) { return new CraftSilverfish(server, (EntitySilverfish) entity); }
                    else if (entity instanceof EntityGiantZombie) { return new CraftGiant(server, (EntityGiantZombie) entity); }
                    else if (entity instanceof EntitySkeleton) { return new CraftSkeleton(server, (EntitySkeleton) entity); }
                    else if (entity instanceof EntityBlaze) { return new CraftBlaze(server, (EntityBlaze) entity); }
                    else if (entity instanceof EntityWitch) { return new CraftWitch(server, (EntityWitch) entity); }
                    else if (entity instanceof EntityWither) { return new CraftWither(server, (EntityWither) entity); }
                    else if (entity instanceof EntitySpider) {
                        if (entity instanceof EntityCaveSpider) { return new CraftCaveSpider(server, (EntityCaveSpider) entity); }
                        else { return new CraftSpider(server, (EntitySpider) entity); }
                    }
                    else if (entity instanceof EntityEndermite) { return new CraftEndermite(server, (EntityEndermite) entity); }
                    else if (entity instanceof EntityGuardian) { return new CraftGuardian(server, (EntityGuardian) entity); }

                    else  { return new CraftMonster(server, (EntityMob) entity); }
                }
                else if (entity instanceof EntityGolem) {
                    if (entity instanceof EntitySnowman) { return new CraftSnowman(server, (EntitySnowman) entity); }
                    else if (entity instanceof EntityIronGolem) { return new CraftIronGolem(server, (EntityIronGolem) entity); }
                    else if (entity instanceof EntityShulker) { return new CraftShulker(server, (EntityShulker) entity); }
                }
                else if (entity instanceof EntityVillager) { return new CraftVillager(server, (EntityVillager) entity); }
                else { return new CraftCreature(server, (EntityCreature) entity); }
            }
            // Slimes are a special (and broken) case
            else if (entity instanceof EntitySlime) {
                if (entity instanceof EntityMagmaCube) { return new CraftMagmaCube(server, (EntityMagmaCube) entity); }
                else { return new CraftSlime(server, (EntitySlime) entity); }
            }
            // Flying
            else if (entity instanceof EntityFlying) {
                if (entity instanceof EntityGhast) { return new CraftGhast(server, (EntityGhast) entity); }
                else { return new CraftFlying(server, (EntityFlying) entity); }
            }
            else if (entity instanceof EntityDragon) {
                return new CraftEnderDragon(server, (EntityDragon) entity);
            }
            // Ambient
            else if (entity instanceof EntityAmbientCreature) {
                if (entity instanceof EntityBat) { return new CraftBat(server, (EntityBat) entity); }
                else { return new CraftAmbient(server, (EntityAmbientCreature) entity); }
            }
            else if (entity instanceof EntityArmorStand) { return new CraftArmorStand(server, (EntityArmorStand) entity); }
            else  { return new CraftLivingEntity(server, (EntityLiving) entity); }
        }
        else if (entity instanceof EntityDragonPart) {
        	EntityDragonPart part = (EntityDragonPart) entity;
            if (part.entityDragonObj instanceof EntityDragon) { return new CraftEnderDragonPart(server, (EntityDragonPart) entity); }
            else { return new CraftComplexPart(server, (EntityDragonPart) entity); }
        }
        else if (entity instanceof EntityXPOrb) { return new CraftExperienceOrb(server, (EntityXPOrb) entity); }
        else if (entity instanceof EntityTippedArrow) {
        	if (((EntityTippedArrow) entity).isTipped()) { return new CraftTippedArrow(server, (EntityTippedArrow) entity); }
        	else { return new CraftArrow(server, (EntityArrow) entity); }
        }
        else if (entity instanceof EntitySpectralArrow) { return new CraftSpectralArrow(server, (EntitySpectralArrow) entity); }
        else if (entity instanceof EntityArrow) { return new CraftArrow(server, (EntityArrow) entity); }
        else if (entity instanceof EntityBoat) { return new CraftBoat(server, (EntityBoat) entity); }
        else if (entity instanceof EntityThrowable) {
            if (entity instanceof EntityEgg) { return new CraftEgg(server, (EntityEgg) entity); }
            else if (entity instanceof EntitySnowball) { return new CraftSnowball(server, (EntitySnowball) entity); }
            else if (entity instanceof EntityPotion) {
                if (!((EntityPotion) entity).isLingering()) { return new CraftSplashPotion(server, (EntityPotion) entity); }
            	else { return new CraftLingeringPotion(server, (EntityPotion) entity); }
            }
            else if (entity instanceof EntityEnderPearl) { return new CraftEnderPearl(server, (EntityEnderPearl) entity); }
            else if (entity instanceof EntityExpBottle) { return new CraftThrownExpBottle(server, (EntityExpBottle) entity); }
        }
        else if (entity instanceof EntityFallingBlock) { return new CraftFallingSand(server, (EntityFallingBlock) entity); }
        else if (entity instanceof EntityFireball) {
            if (entity instanceof EntitySmallFireball) { return new CraftSmallFireball(server, (EntitySmallFireball) entity); }
            else if (entity instanceof EntityLargeFireball) { return new CraftLargeFireball(server, (EntityLargeFireball) entity); }
            else if (entity instanceof EntityWitherSkull) { return new CraftWitherSkull(server, (EntityWitherSkull) entity); }
            else if (entity instanceof EntityDragonFireball) { return new CraftDragonFireball(server, (EntityDragonFireball) entity); }
            else { return new CraftFireball(server, (EntityFireball) entity); }
        }
        else if (entity instanceof EntityEnderEye) { return new CraftEnderSignal(server, (EntityEnderEye) entity); }
        else if (entity instanceof EntityEnderCrystal) { return new CraftEnderCrystal(server, (EntityEnderCrystal) entity); }
        else if (entity instanceof EntityFishHook) { return new CraftFish(server, (EntityFishHook) entity); }
        else if (entity instanceof EntityItem) { return new CraftItem(server, (EntityItem) entity); }
        else if (entity instanceof EntityWeatherEffect) {
            if (entity instanceof EntityLightningBolt) { return new CraftLightningStrike(server, (EntityLightningBolt) entity); }
            else { return new CraftWeather(server, (EntityWeatherEffect) entity); }
        }
        else if (entity instanceof EntityMinecart) {
            if (entity instanceof EntityMinecartFurnace) { return new CraftMinecartFurnace(server, (EntityMinecartFurnace) entity); }
            else if (entity instanceof EntityMinecartChest) { return new CraftMinecartChest(server, (EntityMinecartChest) entity); }
            else if (entity instanceof EntityMinecartTNT) { return new CraftMinecartTNT(server, (EntityMinecartTNT) entity); }
            else if (entity instanceof EntityMinecartHopper) { return new CraftMinecartHopper(server, (EntityMinecartHopper) entity); }
            else if (entity instanceof EntityMinecartMobSpawner) { return new CraftMinecartMobSpawner(server, (EntityMinecartMobSpawner) entity); }
            else if (entity instanceof EntityMinecartEmpty) { return new CraftMinecartRideable(server, (EntityMinecartEmpty) entity); }
            else if (entity instanceof EntityMinecartCommandBlock) { return new CraftMinecartCommand(server, (EntityMinecartCommandBlock) entity); }
        } else if (entity instanceof EntityHanging) {
            if (entity instanceof EntityPainting) { return new CraftPainting(server, (EntityPainting) entity); }
            else if (entity instanceof EntityItemFrame) { return new CraftItemFrame(server, (EntityItemFrame) entity); }
            else if (entity instanceof EntityLeashKnot) { return new CraftLeash(server, (EntityLeashKnot) entity); }
            else { return new CraftHanging(server, (EntityHanging) entity); }
        }
        else if (entity instanceof EntityTNTPrimed) { return new CraftTNTPrimed(server, (EntityTNTPrimed) entity); }
        else if (entity instanceof EntityFireworkRocket) { return new CraftFirework(server, (EntityFireworkRocket) entity); }
        else if (entity instanceof EntityShulkerBullet) { return new CraftShulkerBullet(server, (EntityShulkerBullet) entity); }
        else if (entity instanceof EntityAreaEffectCloud) { return new CraftAreaEffectCloud(server, (EntityAreaEffectCloud) entity); }

        throw new AssertionError("Unknown entity " + (entity == null ? null : entity.getClass()));
    }

    public Location getLocation() {
        return new Location(getWorld(), entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
    }

    public Location getLocation(Location loc) {
        if (loc != null) {
            loc.setWorld(getWorld());
            loc.setX(entity.posX);
            loc.setY(entity.posY);
            loc.setZ(entity.posZ);
            loc.setYaw(entity.rotationYaw);
            loc.setPitch(entity.rotationPitch);
        }

        return loc;
    }

    public Vector getVelocity() {
        return new Vector(entity.motionX, entity.motionY, entity.motionZ);
    }

    public void setVelocity(Vector vel) {
        entity.motionX = vel.getX();
        entity.motionY = vel.getY();
        entity.motionZ = vel.getZ();
        entity.velocityChanged = true;
    }

    public boolean isOnGround() {
        if (entity instanceof EntityArrow) {
            return ((EntityArrow) entity).isInGround();
        }
        return entity.onGround;
    }

    public World getWorld() {
        return entity.worldObj.getWorld();
    }

    public boolean teleport(Location location) {
        return teleport(location, TeleportCause.PLUGIN);
    }

    public boolean teleport(Location location, TeleportCause cause) {
        if (entity.isBeingRidden() || entity.isDead) {
            return false;
        }

        // If this entity is riding another entity, we must dismount before teleporting.
        entity.dismountRidingEntity();

        entity.worldObj = ((CraftWorld) location.getWorld()).getHandle();
        entity.setPositionAndRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        // entity.setLocation() throws no event, and so cannot be cancelled
        return true;
    }

    public boolean teleport(org.bukkit.entity.Entity destination) {
        return teleport(destination.getLocation());
    }

    public boolean teleport(org.bukkit.entity.Entity destination, TeleportCause cause) {
        return teleport(destination.getLocation(), cause);
    }

    public List<org.bukkit.entity.Entity> getNearbyEntities(double x, double y, double z) {
        List<Entity> notchEntityList = entity.worldObj.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(x, y, z), null);
        List<org.bukkit.entity.Entity> bukkitEntityList = new java.util.ArrayList<org.bukkit.entity.Entity>(notchEntityList.size());

        for (Entity e : notchEntityList) {
            bukkitEntityList.add(e.getBukkitEntity());
        }
        return bukkitEntityList;
    }

    public int getEntityId() {
        return entity.getEntityId();
    }

    public int getFireTicks() {
        return entity.fire;
    }

    public int getMaxFireTicks() {
        return entity.fireResistance;
    }

    public void setFireTicks(int ticks) {
        entity.fire = ticks;
    }

    public void remove() {
        entity.isDead = true;
    }

    public boolean isDead() {
        return !entity.isEntityAlive();
    }

    public boolean isValid() {
        return entity.isEntityAlive() && entity.valid;
    }

    public Server getServer() {
        return server;
    }

    public Vector getMomentum() {
        return getVelocity();
    }

    public void setMomentum(Vector value) {
        setVelocity(value);
    }

    public org.bukkit.entity.Entity getPassenger() {
        return isEmpty() ? null : getHandle().riddenByEntities.get(0).getBukkitEntity();
    }

    public boolean setPassenger(org.bukkit.entity.Entity passenger) {
        Preconditions.checkArgument(!this.equals(passenger), "Entity cannot ride itself.");
        if (passenger instanceof CraftEntity) {
            eject();
            ((CraftEntity) passenger).getHandle().startRiding(getHandle());
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmpty() {
        return !getHandle().isBeingRidden();
    }

    public boolean eject() {
        if (isEmpty()) {
            return false;
        }

        getPassenger().leaveVehicle();
        return true;
    }

    public float getFallDistance() {
        return getHandle().fallDistance;
    }

    public void setFallDistance(float distance) {
        getHandle().fallDistance = distance;
    }

    public void setLastDamageCause(EntityDamageEvent event) {
        lastDamageEvent = event;
    }

    public EntityDamageEvent getLastDamageCause() {
        return lastDamageEvent;
    }

    public UUID getUniqueId() {
        return getHandle().getUniqueID();
    }

    public int getTicksLived() {
        return getHandle().ticksExisted;
    }

    public void setTicksLived(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Age must be at least 1 tick");
        }
        getHandle().ticksExisted = value;
    }

    public Entity getHandle() {
        return entity;
    }

    public void playEffect(EntityEffect type) {
        this.getHandle().worldObj.setEntityState(getHandle(), type.getData());
    }

    public void setHandle(final Entity entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "CraftEntity{" + "id=" + getEntityId() + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CraftEntity other = (CraftEntity) obj;
        return (this.getEntityId() == other.getEntityId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.getEntityId();
        return hash;
    }

    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        server.getEntityMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return server.getEntityMetadata().getMetadata(this, metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return server.getEntityMetadata().hasMetadata(this, metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        server.getEntityMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    public boolean isInsideVehicle() {
        return getHandle().isRiding();
    }

    public boolean leaveVehicle() {
        if (!isInsideVehicle()) {
            return false;
        }

        getHandle().dismountRidingEntity();
        return true;
    }

    public org.bukkit.entity.Entity getVehicle() {
        if (!isInsideVehicle()) {
            return null;
        }

        return getHandle().getLowestRidingEntity().getBukkitEntity();
    }

    @Override
    public void setCustomName(String name) {
        if (name == null) {
            name = "";
        }

        getHandle().setCustomNameTag(name);
    }

    @Override
    public String getCustomName() {
        String name = getHandle().getCustomNameTag();

        if (name == null || name.length() == 0) {
            return null;
        }

        return name;
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        getHandle().setAlwaysRenderNameTag(flag);
    }

    @Override
    public boolean isCustomNameVisible() {
        return getHandle().getAlwaysRenderNameTag();
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void sendMessage(String[] messages) {

    }

    @Override
    public String getName() {
        return getHandle().getName();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return getPermissibleBase().isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return CraftEntity.getPermissibleBase().isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return getPermissibleBase().hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return getPermissibleBase().hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return getPermissibleBase().addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return getPermissibleBase().addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return getPermissibleBase().addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return getPermissibleBase().addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        getPermissibleBase().removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        getPermissibleBase().recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return getPermissibleBase().getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return getPermissibleBase().isOp();
    }

    @Override
    public void setOp(boolean value) {
        getPermissibleBase().setOp(value);
    }

    @Override
    public void setGlowing(boolean flag) {
        getHandle().glowing = flag;
        Entity e = getHandle();
        if (e.getFlag(6) != flag) {
            e.setFlag(6, flag);
        }
    }

    @Override
    public boolean isGlowing() {
        return getHandle().glowing;
    }

    @Override
    public void setInvulnerable(boolean flag) {
        getHandle().setEntityInvulnerable(flag);
    }

    @Override
    public boolean isInvulnerable() {
        return getHandle().isEntityInvulnerable(DamageSource.generic);
    }

    @Override
	public boolean isSilent() {
		//return getHandle().ad(); // PAIL: Rename isSilent
    	return false; // TODO
	}

	@Override
	public void setSilent(boolean flag) {
		//getHandle().c(flag); // PAIL: Rename setSilent //TODO
	}

    private static PermissibleBase getPermissibleBase() {
        if (perm == null) {
            perm = new PermissibleBase(new ServerOperator() {

                @Override
                public boolean isOp() {
                    return false;
                }

                @Override
                public void setOp(boolean value) {

                }
            });
        }
        return perm;
    }
}
