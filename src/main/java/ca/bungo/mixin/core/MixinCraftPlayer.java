/*
 * This file is part of Ignite, licensed under the MIT License (MIT).
 *
 * Copyright (c) vectrix.space <https://vectrix.space/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ca.bungo.mixin.core;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(CraftPlayer.class)
public abstract class MixinCraftPlayer {

  @Unique
  private Map<String, List<Entity>> bungoPatches$toTeleport = new HashMap<>();

  @Inject(method = "teleport(Lorg/bukkit/Location;Lorg/bukkit/event/player/PlayerTeleportEvent$TeleportCause;[Lio/papermc/paper/entity/TeleportFlag;)Z",
    at = @At("HEAD"), remap = false)
  public void teleport(Location location, PlayerTeleportEvent.TeleportCause cause,
                       TeleportFlag[] flags, CallbackInfoReturnable<Boolean> cir){

    Player player = ((CraftPlayer)(Object)this);
    List<Entity> passengers = player.getPassengers();

    if (!passengers.isEmpty()) {
      for (Entity passenger : passengers) {
        if (passenger.getType() == EntityType.TEXT_DISPLAY) {
          List<Entity> displays = bungoPatches$toTeleport.get(player.getUniqueId().toString());
          if(displays == null) displays = new ArrayList<>();
          displays.add(passenger);
          bungoPatches$toTeleport.put(player.getUniqueId().toString(), displays);
        }
        player.removePassenger(passenger);
      }
    }
  }

  @Inject(method = "teleport(Lorg/bukkit/Location;Lorg/bukkit/event/player/PlayerTeleportEvent$TeleportCause;[Lio/papermc/paper/entity/TeleportFlag;)Z",
    at = @At("RETURN"), remap = false)
  public void teleportOnReturn(Location location, PlayerTeleportEvent.TeleportCause cause, TeleportFlag[] flags, CallbackInfoReturnable<Boolean> cir){

    if(cir.getReturnValue()){
      CraftPlayer player = ((CraftPlayer)(Object)this);
      List<Entity> displays = bungoPatches$toTeleport.get(player.getUniqueId().toString());
      if(displays == null) return;
      for(Entity display : displays){
        display.teleport(location);
        player.addPassenger(display);
      }
      bungoPatches$toTeleport.remove(player.getUniqueId().toString());

    }

  }

}
