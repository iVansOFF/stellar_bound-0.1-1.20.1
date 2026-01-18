package net.cardboard.stellarbound.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SkraeveEntity extends Monster implements GeoEntity {

    // Estados del mob
    private static final EntityDataAccessor<Integer> ATTACK_STATE =
            SynchedEntityData.defineId(SkraeveEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_DASHING =
            SynchedEntityData.defineId(SkraeveEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_STUNNED =
            SynchedEntityData.defineId(SkraeveEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_ATTACK_CHARGING =
            SynchedEntityData.defineId(SkraeveEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DASH_STATE =
            SynchedEntityData.defineId(SkraeveEntity.class, EntityDataSerializers.INT);

    // Constantes para estados de ataque
    private static final int NO_ATTACK = 0;
    private static final int ATTACK_CHARGING_1 = 1;
    private static final int ATTACK_EXECUTING_1 = 2;
    private static final int ATTACK_CHARGING_2 = 3;
    private static final int ATTACK_EXECUTING_2 = 4;

    // Constantes para estados de dash
    private static final int DASH_NONE = 0;
    private static final int DASH_STARTING = 1;
    private static final int DASH_ACTIVE = 2;
    private static final int DASH_ENDING = 3;

    // Constantes para tiempos de ataque (en ticks, 20 ticks = 1 segundo)
    // Attack1: Total 1.83s, golpe en 0.83s
    private static final int ATTACK1_CHARGE_TICKS = 16;
    private static final int ATTACK1_EXECUTE_TICKS = 20;

    // Attack2: Total 1.63s, golpe en 0.92s
    private static final int ATTACK2_CHARGE_TICKS = 17;
    private static final int ATTACK2_EXECUTE_TICKS = 15;

    // Constantes para dash (tiempos ajustados)
    // START: 1.17 segundos = 23 ticks (carga, sin movimiento)
    // DASH: 2.00 segundos = 40 ticks (movimiento activo)
    // END: 1.04 segundos = 21 ticks (sin movimiento)
    // TOTAL: 4.21 segundos = 84 ticks
    private static final int DASH_START_TICKS = 23;  // 1.17s - Carga sin movimiento
    private static final int DASH_TICKS = 40;        // 2.00s - Dash activo
    private static final int DASH_END_TICKS = 21;    // 1.04s - Finalización sin movimiento
    private static final int STUN_TICKS = 40;        // 2.00s - Stun por colisión
    private static final float DASH_SPEED = 1.5F;
    private static final float DASH_DAMAGE = 8.0F;

    // Distancia para activar dash (en bloques)
    private static final double MIN_DASH_DISTANCE = 7.0; // Mínimo 7 bloques para dash
    private static final double MAX_DASH_DISTANCE = 25.0; // Máximo 25 bloques para dash

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Contadores
    private int attackChargeTick = 0;
    private int attackExecuteTick = 0;
    private int dashTick = 0;
    private int dashCooldown = 0;
    private int stunnedTick = 0;
    private int attackCooldown = 0;

    // Para timing preciso del golpe
    private boolean damageApplied = false;

    // Variables de dash
    private Vec3 dashDirection = Vec3.ZERO;
    private Vec3 dashTargetLookDirection = Vec3.ZERO; // Nueva variable para almacenar la dirección de mirada

    public SkraeveEntity(EntityType<? extends Monster> type, @NotNull Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    @SuppressWarnings("unused")
    public static boolean checkSkraeveSpawnRules(
            EntityType<SkraeveEntity> entityType,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return checkMonsterSpawnRules(entityType, level, spawnType, pos, random);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_STATE, NO_ATTACK);
        this.entityData.define(IS_DASHING, false);
        this.entityData.define(IS_STUNNED, false);
        this.entityData.define(IS_ATTACK_CHARGING, false);
        this.entityData.define(DASH_STATE, DASH_NONE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SkraeveDashGoal(this));
        this.goalSelector.addGoal(2, new SkraeveMeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            // Manejar cooldown de ataques
            if (attackCooldown > 0) {
                attackCooldown--;
            }

            // Manejar cooldown de dash
            if (dashCooldown > 0) {
                dashCooldown--;
            }

            // Manejar estado de stun
            if (this.entityData.get(IS_STUNNED)) {
                stunnedTick++;
                if (stunnedTick >= STUN_TICKS) {
                    this.entityData.set(IS_STUNNED, false);
                    stunnedTick = 0;
                }
                return;
            }

            // Manejar animación de ataque usando switch mejorado
            int attackState = this.entityData.get(ATTACK_STATE);

            switch (attackState) {
                case ATTACK_CHARGING_1 -> {
                    attackChargeTick++;
                    if (attackChargeTick >= ATTACK1_CHARGE_TICKS) {
                        // Aplicar daño en el momento exacto (0.83s)
                        if (!damageApplied) {
                            applyAttackDamage();
                            damageApplied = true;
                        }

                        // Pasar a fase de ejecución
                        this.entityData.set(ATTACK_STATE, ATTACK_EXECUTING_1);
                        attackChargeTick = 0;
                    }
                }
                case ATTACK_EXECUTING_1 -> {
                    attackExecuteTick++;
                    if (attackExecuteTick >= ATTACK1_EXECUTE_TICKS) {
                        // Terminar ataque 1
                        this.entityData.set(ATTACK_STATE, NO_ATTACK);
                        this.entityData.set(IS_ATTACK_CHARGING, false);
                        attackExecuteTick = 0;
                        damageApplied = false;
                        attackCooldown = 20;
                    }
                }
                case ATTACK_CHARGING_2 -> {
                    attackChargeTick++;
                    if (attackChargeTick >= ATTACK2_CHARGE_TICKS) {
                        // Aplicar daño en el momento exacto (0.92s)
                        if (!damageApplied) {
                            applyAttackDamage();
                            damageApplied = true;
                        }

                        // Pasar a fase de ejecución
                        this.entityData.set(ATTACK_STATE, ATTACK_EXECUTING_2);
                        attackChargeTick = 0;
                    }
                }
                case ATTACK_EXECUTING_2 -> {
                    attackExecuteTick++;
                    if (attackExecuteTick >= ATTACK2_EXECUTE_TICKS) {
                        // Terminar ataque 2
                        this.entityData.set(ATTACK_STATE, NO_ATTACK);
                        this.entityData.set(IS_ATTACK_CHARGING, false);
                        attackExecuteTick = 0;
                        damageApplied = false;
                        attackCooldown = 20;
                    }
                }
            }

            // Detener movimiento durante carga de ataque
            if (this.entityData.get(IS_ATTACK_CHARGING)) {
                this.getNavigation().stop();
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            }

            // Manejar dash
            if (this.entityData.get(IS_DASHING)) {
                handleDash();
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        // Solo en el servidor
        if (!this.level().isClientSide) {
            int currentDashState = this.entityData.get(DASH_STATE);

            // Durante la fase de carga (STARTING), mirar siempre al jugador
            if (currentDashState == DASH_STARTING) {
                LivingEntity target = this.getTarget();
                if (target != null) {
                    // Calcular la dirección hacia el jugador y ajustar la rotación
                    Vec3 targetPos = target.position();
                    Vec3 myPos = this.position();

                    // Guardar la dirección de mirada para usarla después (sin variable redundante)
                    dashTargetLookDirection = targetPos.subtract(myPos).normalize();

                    // Actualizar la rotación del mob para que mire al jugador
                    double dx = target.getX() - this.getX();
                    double dz = target.getZ() - this.getZ();
                    float targetYRot = (float)(Math.atan2(dz, dx) * (180 / Math.PI)) - 90.0F;

                    // Interpolar suavemente hacia la rotación objetivo
                    float yRotDifference = targetYRot - this.getYRot();
                    while (yRotDifference < -180.0F) yRotDifference += 360.0F;
                    while (yRotDifference >= 180.0F) yRotDifference -= 360.0F;

                    float rotationSpeed = 10.0F; // Velocidad de rotación (ajustable)
                    this.setYRot(this.getYRot() + yRotDifference * rotationSpeed * 0.05F);

                    // También ajustar la rotación vertical si es necesario
                    double dy = target.getY() - this.getY();
                    float targetXRot = (float)(-Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
                    float xRotDifference = targetXRot - this.getXRot();
                    this.setXRot(this.getXRot() + xRotDifference * rotationSpeed * 0.05F);

                    // Asegurarse de que la cabeza también siga al jugador
                    this.getLookControl().setLookAt(target, 30.0F, 30.0F);
                }
            }
            // Durante la fase activa del dash, mantener la dirección calculada
            else if (currentDashState == DASH_ACTIVE && dashTargetLookDirection != Vec3.ZERO) {
                // Calcular la rotación basada en la dirección del dash
                double dx = dashTargetLookDirection.x;
                double dz = dashTargetLookDirection.z;
                float targetYRot = (float)(Math.atan2(dz, dx) * (180 / Math.PI)) - 90.0F;

                // Fijar la rotación (sin interpolar)
                this.setYRot(targetYRot);
                this.yHeadRot = targetYRot;
                this.yBodyRot = targetYRot;

                // Ajustar también la rotación vertical
                double dy = dashTargetLookDirection.y;
                float targetXRot = (float)(-Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
                this.setXRot(targetXRot);
            }
        }
    }

    private void applyAttackDamage() {
        LivingEntity target = this.getTarget();
        if (target != null && this.canAttack(target)) {
            // Verificar distancia al objetivo
            double distSqr = this.distanceToSqr(target);
            double reach = this.getAttackReachSqr(target);

            if (distSqr <= reach) {
                // Aplicar daño básico
                this.doHurtTarget(target);

                // Sonido de golpe
                if (this.entityData.get(ATTACK_STATE) == ATTACK_CHARGING_1) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 0.9F);
                } else {
                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                }
            }
        }
    }

    private double getAttackReachSqr(@NotNull LivingEntity target) {
        // Alcance de ataque base (3.5 bloques) más la mitad del ancho del objetivo
        double baseReach = 3.5;
        double targetWidth = target.getBbWidth() / 2.0;
        return (baseReach + targetWidth) * (baseReach + targetWidth);
    }

    private void handleDash() {
        dashTick++;

        int currentDashState = this.entityData.get(DASH_STATE);
        switch (currentDashState) {
            case DASH_STARTING -> {
                // Fase de carga (1.17s) - SIN MOVIMIENTO
                this.getNavigation().stop();
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

                if (dashTick >= DASH_START_TICKS) {
                    // Cambiar a fase de dash
                    this.entityData.set(DASH_STATE, DASH_ACTIVE);
                    dashTick = 0;

                    // Calcular dirección del dash basada en la última mirada al jugador
                    if (dashTargetLookDirection != Vec3.ZERO) {
                        // Usar la dirección en la que estaba mirando al jugador
                        dashDirection = dashTargetLookDirection;
                        // Añadir un poco de altura para que no quede plano
                        dashDirection = new Vec3(dashDirection.x, 0.1, dashDirection.z).normalize();
                    } else {
                        // Fallback si no hay dirección guardada
                        LivingEntity target = getTarget();
                        if (target != null) {
                            Vec3 targetPos = target.position();
                            Vec3 myPos = this.position();
                            dashDirection = targetPos.subtract(myPos).normalize();
                            dashDirection = new Vec3(dashDirection.x, 0.1, dashDirection.z).normalize();
                        } else {
                            dashDirection = this.getLookAngle();
                        }
                    }
                }
            }
            case DASH_ACTIVE -> {
                // Fase de dash activo (2.00s) - CON MOVIMIENTO
                Vec3 motion = dashDirection.scale(DASH_SPEED);
                this.setDeltaMovement(motion.x, this.getDeltaMovement().y, motion.z);

                // Verificar colisión con bloques
                if (checkDashCollision()) {
                    startStun();
                    return;
                }

                // Aplicar daño a entidades durante el dash
                checkDashEntityCollision();

                if (dashTick >= DASH_TICKS) {
                    // Cambiar a fase de finalización
                    this.entityData.set(DASH_STATE, DASH_ENDING);
                    dashTick = 0;
                }
            }
            case DASH_ENDING -> {
                // Fase de finalización (1.04s) - SIN MOVIMIENTO
                this.getNavigation().stop();
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

                if (dashTick >= DASH_END_TICKS) {
                    endDash();
                }
            }
        }
    }

    private boolean checkDashCollision() {
        // Verificar si hay bloques sólidos en la dirección del dash
        Vec3 nextPos = this.position().add(dashDirection.scale(1.0)); // Mirar 1 bloque adelante
        BlockPos blockPos = BlockPos.containing(nextPos.x, nextPos.y, nextPos.z);

        // Verificar múltiples bloques en altura
        for (int y = 0; y <= 2; y++) {
            BlockPos checkPos = blockPos.above(y);
            if (!this.level().getBlockState(checkPos).isAir() &&
                    this.level().getBlockState(checkPos).isSolidRender(this.level(), checkPos)) {
                return true;
            }
        }
        return false;
    }

    private void checkDashEntityCollision() {
        // Área de búsqueda para daño durante dash
        AABB searchBox = this.getBoundingBox().inflate(2.0D); // Mayor área para dash

        this.level().getEntitiesOfClass(LivingEntity.class, searchBox,
                        entity -> entity != this && entity.isAlive() && this.canAttack(entity))
                .forEach(entity -> {
                    // Solo aplicar daño cada 5 ticks para no ser OP
                    if (dashTick % 5 == 0) {
                        entity.hurt(this.damageSources().mobAttack(this), DASH_DAMAGE / 2.0F);

                        // Aplicar knockback en la dirección del dash
                        Vec3 knockback = dashDirection.scale(2.0);
                        entity.setDeltaMovement(entity.getDeltaMovement().add(knockback.x, 0.5, knockback.z));
                    }
                });
    }

    private void startStun() {
        this.entityData.set(IS_DASHING, false);
        this.entityData.set(IS_STUNNED, true);
        this.entityData.set(DASH_STATE, DASH_NONE);
        dashTick = 0;
        stunnedTick = 0;
        dashCooldown = 200; // Mayor cooldown por chocar

        this.setDeltaMovement(Vec3.ZERO);
        this.playSound(SoundEvents.ANVIL_LAND, 1.0F, 1.2F);

        // Resetear dirección de mirada
        dashTargetLookDirection = Vec3.ZERO;
    }

    private void endDash() {
        this.entityData.set(IS_DASHING, false);
        this.entityData.set(DASH_STATE, DASH_NONE);
        dashTick = 0;
        dashCooldown = 100; // Cooldown normal después de dash exitoso

        // Resetear dirección de mirada
        dashTargetLookDirection = Vec3.ZERO;
    }

    public void startDash() {
        if (dashCooldown <= 0 && !this.entityData.get(IS_STUNNED) &&
                this.entityData.get(ATTACK_STATE) == NO_ATTACK) {
            this.entityData.set(IS_DASHING, true);
            this.entityData.set(DASH_STATE, DASH_STARTING);
            dashTick = 0;

            // Resetear dirección de mirada
            dashTargetLookDirection = Vec3.ZERO;

            this.playSound(SoundEvents.ENDER_DRAGON_FLAP, 1.0F, 0.8F);
        }
    }

    public void startAttack(int attackType) {
        if (this.entityData.get(ATTACK_STATE) == NO_ATTACK &&
                !this.entityData.get(IS_DASHING) &&
                !this.entityData.get(IS_STUNNED) &&
                attackCooldown <= 0) {

            damageApplied = false;

            if (attackType == 1) {
                this.entityData.set(ATTACK_STATE, ATTACK_CHARGING_1);
                this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
            } else {
                this.entityData.set(ATTACK_STATE, ATTACK_CHARGING_2);
                this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
            }

            this.entityData.set(IS_ATTACK_CHARGING, true);
            attackChargeTick = 0;
            attackExecuteTick = 0;
        }
    }

    @Override
    public void registerControllers(@NotNull AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            // Prioridad: stunned > dash > attack > movement

            if (this.entityData.get(IS_STUNNED)) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("skraeve.stunned"));
            }

            if (this.entityData.get(IS_DASHING)) {
                int currentDashState = this.entityData.get(DASH_STATE);
                return switch (currentDashState) {
                    case DASH_STARTING -> state.setAndContinue(RawAnimation.begin().thenPlayAndHold("skraeve.start"));
                    case DASH_ACTIVE -> state.setAndContinue(RawAnimation.begin().thenPlayAndHold("skraeve.dash"));
                    case DASH_ENDING -> state.setAndContinue(RawAnimation.begin().thenPlayAndHold("skraeve.end"));
                    default -> state.setAndContinue(RawAnimation.begin().thenPlayAndHold("skraeve.dash"));
                };
            }

            int attackState = this.entityData.get(ATTACK_STATE);
            return switch (attackState) {
                case ATTACK_CHARGING_1, ATTACK_EXECUTING_1 ->
                        state.setAndContinue(RawAnimation.begin().thenPlayAndHold("skraeve.attack1"));
                case ATTACK_CHARGING_2, ATTACK_EXECUTING_2 ->
                        state.setAndContinue(RawAnimation.begin().thenPlayAndHold("skraeve.attack2"));
                default -> {
                    boolean moving = this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6;
                    boolean hasTarget = this.getTarget() != null;

                    if (moving) {
                        if (hasTarget) {
                            yield state.setAndContinue(RawAnimation.begin().thenLoop("skraeve.walk_detected"));
                        }
                        yield state.setAndContinue(RawAnimation.begin().thenLoop("skraeve.walk"));
                    }

                    yield state.setAndContinue(RawAnimation.begin().thenLoop("skraeve.idle"));
                }
            };
        }));
    }

    @Override
    public @NotNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // Goals personalizados
    static class SkraeveMeleeAttackGoal extends MeleeAttackGoal {
        private final SkraeveEntity skraeve;

        public SkraeveMeleeAttackGoal(SkraeveEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(mob, speedModifier, followingTargetEvenIfNotSeen);
            this.skraeve = mob;
        }

        @Override
        public boolean canUse() {
            return super.canUse() &&
                    !skraeve.entityData.get(IS_DASHING) &&
                    !skraeve.entityData.get(IS_STUNNED) &&
                    skraeve.entityData.get(ATTACK_STATE) == NO_ATTACK &&
                    skraeve.attackCooldown <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() &&
                    !skraeve.entityData.get(IS_DASHING) &&
                    !skraeve.entityData.get(IS_STUNNED) &&
                    skraeve.entityData.get(ATTACK_STATE) == NO_ATTACK;
        }

        @Override
        protected double getAttackReachSqr(@NotNull LivingEntity target) {
            // Alcance de ataque base (3.5 bloques) más la mitad del ancho del objetivo
            double baseReach = 3.5;
            double targetWidth = target.getBbWidth() / 2.0;
            return (baseReach + targetWidth) * (baseReach + targetWidth);
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity target, double distToTargetSqr) {
            double attackReachSqr = this.getAttackReachSqr(target);

            if (distToTargetSqr <= attackReachSqr) {
                // Alternar entre attack1 y attack2
                int attackType = skraeve.getRandom().nextBoolean() ? 1 : 2;
                skraeve.startAttack(attackType);
            }
        }
    }

    static class SkraeveDashGoal extends Goal {
        private final SkraeveEntity skraeve;
        private int dashCheckDelay = 0;

        public SkraeveDashGoal(SkraeveEntity mob) {
            this.skraeve = mob;
        }

        @Override
        public boolean canUse() {
            if (dashCheckDelay > 0) {
                dashCheckDelay--;
                return false;
            }

            LivingEntity target = skraeve.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }

            // Calcular distancia al objetivo
            double distance = Math.sqrt(skraeve.distanceToSqr(target));

            // Usar dash si el objetivo está entre 7 y 25 bloques de distancia
            boolean inDashRange = distance >= MIN_DASH_DISTANCE && distance <= MAX_DASH_DISTANCE;

            // Mayor probabilidad si el objetivo está lejos
            float dashProbability = 0.3F;
            if (distance > 15.0) {
                dashProbability = 0.5F; // 50% de probabilidad si está muy lejos
            }

            return inDashRange &&
                    skraeve.dashCooldown <= 0 &&
                    skraeve.entityData.get(ATTACK_STATE) == NO_ATTACK &&
                    !skraeve.entityData.get(IS_DASHING) &&
                    !skraeve.entityData.get(IS_STUNNED) &&
                    skraeve.getRandom().nextFloat() < dashProbability;
        }

        @Override
        public void start() {
            skraeve.startDash();
            dashCheckDelay = 80; // 4 segundos antes de volver a intentar
        }

        @Override
        public boolean canContinueToUse() {
            return skraeve.entityData.get(IS_DASHING);
        }
    }

    // Sonidos
    @Override
    protected @NotNull SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.WITHER_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.BLAZE_AMBIENT;
    }

    @Override
    protected float getSoundVolume() {
        return 0.7F;
    }

    @Override
    public float getVoicePitch() {
        return 1.2F;
    }
}