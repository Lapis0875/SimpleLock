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

public class ObjectLockDataType implements PersistentDataType<PersistentDataContainer, ObjectLock> {
    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public @NotNull Class<ObjectLock> getComplexType() {
        return ObjectLock.class;
    }

    @Override
    public @NotNull PersistentDataContainer toPrimitive(@NotNull ObjectLock complex, @NotNull PersistentDataAdapterContext context) {
        return complex.toNBTTag(context);
    }

    @Override
    public @NotNull ObjectLock fromPrimitive(@NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        String ownerName = primitive.get(new NamespacedKey(Simplelock.instance(), Constants.LOCK_OWNER_NAME), PersistentDataType.STRING);
        UUID ownerUUID = primitive.get(new NamespacedKey(Simplelock.instance(), Constants.LOCK_OWNER_UUID), new UUIDDataType());
        ObjectLock lock = new ObjectLock(ownerName, ownerUUID);
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
