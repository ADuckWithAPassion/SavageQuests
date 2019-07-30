package tahpie.savage.savagequests.friendlyNPC;

import java.util.EnumSet;

import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.event.CraftEventFactory;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EntityEquipment;

import net.citizensnpcs.api.event.SpawnReason;
import net.minecraft.server.v1_14_R1.AttributeBase;
import net.minecraft.server.v1_14_R1.AttributeModifier;
import net.minecraft.server.v1_14_R1.DifficultyDamageScaler;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityArrow;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityInsentient;
import net.minecraft.server.v1_14_R1.EntityIronGolem;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPig;
import net.minecraft.server.v1_14_R1.EntityPigZombie;
import net.minecraft.server.v1_14_R1.EntitySkeletonAbstract;
import net.minecraft.server.v1_14_R1.EntityTurtle;
import net.minecraft.server.v1_14_R1.EntityVillagerAbstract;
import net.minecraft.server.v1_14_R1.EntityZombie;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.IMaterial;
import net.minecraft.server.v1_14_R1.IRangedEntity;
import net.minecraft.server.v1_14_R1.Item;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.Items;
import net.minecraft.server.v1_14_R1.Material;
import net.minecraft.server.v1_14_R1.MathHelper;
import net.minecraft.server.v1_14_R1.PathfinderGoal;
import net.minecraft.server.v1_14_R1.PathfinderGoalArrowAttack;
import net.minecraft.server.v1_14_R1.PathfinderGoalBowShoot;
import net.minecraft.server.v1_14_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_14_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_14_R1.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_14_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_14_R1.PathfinderGoalRandomStrollLand;
import net.minecraft.server.v1_14_R1.PathfinderGoalZombieAttack;
import net.minecraft.server.v1_14_R1.ProjectileHelper;
import net.minecraft.server.v1_14_R1.SoundEffects;
import net.minecraft.server.v1_14_R1.World;

public class customZombie extends EntityZombie implements IRangedEntity {
    		
	public customZombie(Player player, World world){
		super(world);
		this.enderTeleportAndLoad(player.getLocation().getX(),player.getLocation().getY() , player.getLocation().getZ());
		world.addEntity(this);
	}

	@Override
	protected void initAttributes() {
		// Calling the super method for the rest of the attributes.
		super.initAttributes();

		// Next, overriding armor and max health!
		// Setting the max health to 40:

		getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(40.0);
		// Setting the 'defense' (armor) to 5:
        getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(20.0D);//follow range

        getAttributeInstance(GenericAttributes.ARMOR).setValue(5);
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.35);
        getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(4);
        getAttributeInstance(GenericAttributes.ATTACK_KNOCKBACK).setValue(10.0);
		Zombie parent = (Zombie) this.getBukkitEntity();
		parent.setCanPickupItems(true);

	}
	
	@Override
    protected void l() {
        this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1.0, false));
        this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0, true, 4, this::ee));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[0]).a(EntityPigZombie.class));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<EntityPig>(this, EntityPig.class, true));
        if (this.world.spigotConfig.zombieAggressiveTowardsVillager) {
            this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<EntityVillagerAbstract>(this, EntityVillagerAbstract.class, false));
        }
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<EntityIronGolem>(this, EntityIronGolem.class, true));
        this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget<EntityTurtle>(this, EntityTurtle.class, 10, true, false, EntityTurtle.bz));
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1, 12, 20));

    }
	
    @Override
    public void a(EntityLiving entityliving, float f2) {
    	Log.info("TEST");
        ItemStack itemstack = this.f(this.b(ProjectileHelper.a(this, Items.BOW)));
        EntityArrow entityarrow = this.b(itemstack, f2);
        double d0 = entityliving.locX - this.locX;
        double d1 = entityliving.getBoundingBox().minY + (double)(entityliving.getHeight() / 3.0f) - entityarrow.locY;
        double d2 = entityliving.locZ - this.locZ;
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6f, 14 - this.world.getDifficulty().a() * 4);
        EntityShootBowEvent event = CraftEventFactory.callEntityShootBowEvent(this, this.getItemInMainHand(), entityarrow, 0.8f);
        if (event.isCancelled()) {
            event.getProjectile().remove();
            return;
        }
        if (event.getProjectile() == entityarrow.getBukkitEntity()) {
            this.world.addEntity(entityarrow);
        }
        this.a(SoundEffects.ENTITY_SKELETON_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
    }

    protected EntityArrow b(ItemStack itemstack, float f2) {
        return ProjectileHelper.a(this, itemstack, f2);
    }
    public class PathfinderGoalArrowAttack
    extends PathfinderGoal {
        private final EntityInsentient a;
        private final IRangedEntity b;
        private EntityLiving c;
        private int d = -1;
        private final double e;
        private int f;
        private final int g;
        private final int h;
        private final float i;
        private final float j;

        public PathfinderGoalArrowAttack(IRangedEntity var0, double var1, int var3, float var4) {
            this(var0, var1, var3, var3, var4);
        }

        public PathfinderGoalArrowAttack(IRangedEntity var0, double var1, int var3, int var4, float var5) {
            if (!(var0 instanceof EntityLiving)) {
                throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
            }
            this.b = var0;
            this.a = (EntityInsentient)((Object)var0);
            this.e = var1;
            this.g = var3;
            this.h = var4;
            this.i = var5;
            this.j = var5 * var5;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            EntityLiving var0 = this.a.getGoalTarget();
            if (var0 == null || !var0.isAlive()) {
                return false;
            }
            this.c = var0;
            return true;
        }

        @Override
        public boolean b() {
            return this.a() || !this.a.getNavigation().n();
        }

        @Override
        public void d() {
            this.c = null;
            this.f = 0;
            this.d = -1;
        }

        @Override
        public void e() {
            double var0 = this.a.e(this.c.locX, this.c.getBoundingBox().minY, this.c.locZ);
            boolean var2 = this.a.getEntitySenses().a(this.c);
            this.f = var2 ? ++this.f : 0;
            if (var0 > (double)this.j || this.f < 5) {
                this.a.getNavigation().a(this.c, this.e);
            } else {
                this.a.getNavigation().o();
            }
            this.a.getControllerLook().a(this.c, 30.0f, 30.0f);
            this.d = this.d-5;
            if (this.d <= 0) {
                float var3;
                if (!var2) {
                    return;
                }
                float var4 = var3 = MathHelper.sqrt(var0) / this.i;
                var4 = MathHelper.a(var4, 0.1f, 1.0f);
                this.b.a(this.c, var4);
                this.d = MathHelper.d(var3 * (float)(this.h - this.g) + (float)this.g);
            } else if (this.d < 0) {
                float var3 = MathHelper.sqrt(var0) / this.i;
                this.d = MathHelper.d(var3 * (float)(this.h - this.g) + (float)this.g);
            }
        }
    }
}