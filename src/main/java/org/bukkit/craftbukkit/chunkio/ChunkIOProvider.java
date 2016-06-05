package org.bukkit.craftbukkit.chunkio;

import java.io.IOException;

import org.bukkit.Server;
import org.bukkit.craftbukkit.util.AsynchronousExecutor;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

import java.util.concurrent.atomic.AtomicInteger;

class ChunkIOProvider implements AsynchronousExecutor.CallBackProvider<QueuedChunk, Chunk, Runnable, RuntimeException> {
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    // async stuff
    public Chunk callStage1(QueuedChunk queuedChunk) throws RuntimeException {
        try {
        	AnvilChunkLoader loader = queuedChunk.loader;
            Object[] data = loader.loadChunk(queuedChunk.world, queuedChunk.x, queuedChunk.z);
            
            if (data != null) {
                queuedChunk.compound = (NBTTagCompound) data[1];
                return (Chunk) data[0];
            }

            return null;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // sync stuff
    public void callStage2(QueuedChunk queuedChunk, Chunk chunk) throws RuntimeException {
        if (chunk == null) {
            // If the chunk loading failed just do it synchronously (may generate)
            queuedChunk.provider.originalGetChunkAt(queuedChunk.x, queuedChunk.z);
            return;
        }

        queuedChunk.loader.loadEntities(chunk, queuedChunk.compound.getCompoundTag("Level"), queuedChunk.world);
        chunk.setLastSaved(queuedChunk.provider.worldObj.getTime());
        queuedChunk.provider.id2ChunkMap.put(ChunkPos.chunkXZ2Int(queuedChunk.x, queuedChunk.z), chunk);
        chunk.onChunkLoad();

        if (queuedChunk.provider.chunkGenerator != null) {
            queuedChunk.provider.chunkGenerator.recreateStructures(chunk, queuedChunk.x, queuedChunk.z);
        }

        Server server = queuedChunk.provider.worldObj.getServer();
        if (server != null) {
            server.getPluginManager().callEvent(new org.bukkit.event.world.ChunkLoadEvent(chunk.bukkitChunk, false));
        }

        // Update neighbor counts
        for (int x = -2; x < 3; x++) {
            for (int z = -2; z < 3; z++) {
                if (x == 0 && z == 0) {
                    continue;
                }

                Chunk neighbor = queuedChunk.provider.getLoadedChunkAt(chunk.locX + x, chunk.locZ + z);
                if (neighbor != null) {
                    neighbor.setNeighborLoaded(-x, -z);
                    chunk.setNeighborLoaded(x, z);
                }
            }
        }

        chunk.populateChunk(queuedChunk.provider, queuedChunk.provider.chunkGenerator);
    }

    public void callStage3(QueuedChunk queuedChunk, Chunk chunk, Runnable runnable) throws RuntimeException {
        runnable.run();
    }

    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, "Chunk I/O Executor Thread-" + threadNumber.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    }
}