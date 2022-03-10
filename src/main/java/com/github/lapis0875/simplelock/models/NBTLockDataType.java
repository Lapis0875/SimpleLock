package com.github.lapis0875.simplelock.models;

import com.github.lapis0875.simplelock.Constants;
import com.github.lapis0875.simplelock.Simplelock;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class NBTLockDataType implements PersistentDataType<PersistentDataContainer, NBTLock> {
    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public @NotNull Class<NBTLock> getComplexType() {
        return NBTLock.class;
    }

    @Override
    public @NotNull PersistentDataContainer toPrimitive(@NotNull NBTLock complex, @NotNull PersistentDataAdapterContext context) {
        return complex.toNBTTag(context);
    }

    @Override
    public @NotNull NBTLock fromPrimitive(@NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        String ownerName = primitive.get(new NamespacedKey(Simplelock.instance(), Constants.LOCK_OWNER_NAME), PersistentDataType.STRING);
        UUID ownerUUID = primitive.get(new NamespacedKey(Simplelock.instance(), Constants.LOCK_OWNER_UUID), new UUIDDataType());
        NBTLock lock = new NBTLock(ownerName, ownerUUID);
        Optional<PersistentDataContainer> allowedPlayersTag = Optional.ofNullable(primitive.get(
                new NamespacedKey(Simplelock.instance(), Constants.LOCK_ALLOWED_PLAYERS),
                PersistentDataType.TAG_CONTAINER
        ));
        allowedPlayersTag.ifPresent((tag) -> {
            tag.getKeys().forEach(
                    (namespacedKey) -> {
                        lock.allowPlayer(tag.get(namespacedKey, new UUIDDataType()));
                    }
            );

        });
        return lock;
    }
}
