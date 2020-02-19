package hmmhmmmm.mail;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerData{
   private final Mail plugin;
   private String name = "Steve";
   private File playerDataFolder;

   public PlayerData(Mail plugin, String name){
      this.plugin = plugin;
      this.name = name;         
      playerDataFolder = new File(plugin.getDataFolder(), "account");
      playerDataFolder.mkdirs();
   }
   public Mail getPlugin(){
      return plugin;
   }
   public String getName(){
      return name;
   }
   public Config getConfig(){
      File path = new File(playerDataFolder, getName().toLowerCase()+".yml");
      if(path.exists()){
         Config config = new Config(path, Config.YAML);
         return config;
      }
      return null;
   }
   public boolean isData(){
      File path = new File(playerDataFolder, getName().toLowerCase()+".yml");        
      return path.exists();
   }
   public void registerData(){
      Config data = new Config(new File(playerDataFolder, getName().toLowerCase()+".yml"), Config.YAML);
      data.set("mail.count", 0);
      data.set("mail.message", new HashMap<>());
      data.set("mail.players", new ArrayList<String>());
      data.save();     
   }
   public boolean removeData(){
      File path = new File(playerDataFolder, getName().toLowerCase()+".yml");        
      return path.delete();
   }
}