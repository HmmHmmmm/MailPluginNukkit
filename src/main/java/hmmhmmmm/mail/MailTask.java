package hmmhmmmm.mail;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask; 

import java.util.HashMap;
import java.util.Map;

public class MailTask extends PluginTask<Mail>{

   public Map<Player, Long> mapLong = new HashMap<>();

   public MailTask(Mail owner){
      super(owner);
   }
   public Mail getPlugin(){
      return this.getOwner();
   }
   @Override
   public void onRun(int currentTick){
      for(Player player : getPlugin().getServer().getOnlinePlayers().values()){
         long current = System.currentTimeMillis();
         if(!mapLong.containsKey(player)){
            long end = current + 180 * 1000L;
            mapLong.put(player, end);
         }else{
            if(mapLong.get(player) < current){
               player.sendMessage(getPlugin().countMail(player.getName()));
               mapLong.remove(player);
            }
         }
      }
   }
}
