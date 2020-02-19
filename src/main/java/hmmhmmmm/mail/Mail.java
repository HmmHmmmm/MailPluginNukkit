package hmmhmmmm.mail;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Config;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mail extends PluginBase implements Listener{
   public static Mail obj = null;
   private FormListener form;
   private String prefix = "?";
   private String facebook = "ไม่มี";
   private String youtube = "ไม่มี";
  
   public Map<Player, String> map = new HashMap<>();
   
   public static Mail getThis(){
      return obj;   }
   public void onLoad(){
      obj = this;
   }
   public void onEnable(){
      getDataFolder().mkdirs();
      File account = new File(getDataFolder(), "account");
      account.mkdirs();
      saveDefaultConfig();
      this.form = new FormListener(this);
      this.prefix = "Mail";
      this.facebook = "https://m.facebook.com/phonlakrit.knaongam.1";
      this.youtube = "https://m.youtube.com/channel/UCtjvLXDxDAUt-8CXV1eWevA";
      getServer().getCommandMap().register("mail", new MailCommand("mail", this));
      getServer().getCommandMap().register("report", new ReportCommand("report", this));
      getServer().getScheduler().scheduleRepeatingTask(new MailTask(this), 20);
      getServer().getPluginManager().registerEvents(this, this);
      getServer().getPluginManager().registerEvents(this.form, this);
      getLogger().info(getPluginInfo());
   }
   public FormListener getForm(){
      return form;   }
   public String getPrefix(){
      return "§e[§d"+this.prefix+"§e]§f";
   }
   public String getFacebook(){
      return this.facebook;
   } 
   public String getYoutube(){
      return this.youtube;
   } 
   public String getPluginInfo(){
      List<String> author = new ArrayList<String>(getDescription().getAuthors());
      String text = "\n"+getPrefix()+" ชื่อปลั๊กอิน "+getDescription().getName()+"\n"+getPrefix()+" เวอร์ชั่น "+getDescription().getVersion()+"\n"+getPrefix()+" รายชื่อผู้สร้าง "+String.join(", ", author)+"\n"+getPrefix()+" คำอธิบายของปลั๊กอิน: ปลั๊กอินนี้ทำแจก โปรดอย่าเอาไปขาย *หากจะเอาไปแจกต่อโปรดให้เครดิตด้วย*\n"+getPrefix()+" เฟสบุ๊ค "+getFacebook()+"\n"+getPrefix()+" ยูทูป "+getYoutube()+"\n"+getPrefix()+" เว็บไซต์ "+getDescription().getWebsite();
      return text;   }
   public PlayerData getPlayerData(String name){
      return new PlayerData(this, name);
   } 
   @EventHandler
   public void onPlayerChat(PlayerChatEvent event){
      Player player = event.getPlayer();
      String message = event.getMessage();
      if(map.containsKey(player)){
         event.setCancelled(true);
         addMail(map.get(player), player, message);
         map.remove(player);
      }
   }
   @EventHandler
   public void onPlayerLogin(PlayerLoginEvent event){
      Player player = event.getPlayer();
      PlayerData playerData = getPlayerData(player.getName());
      if(!playerData.isData()){
         playerData.registerData();
      }
   }
   public boolean isMail(String name){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      return data.exists("mail");        
   }
   public int getCountMail(String name){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      return (int) data.get("mail.count");        
   }
   public void setCountMail(String name, int count){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      data.set("mail.count", count);
      data.save();
   }
   public boolean isMailSender(String name, String senderName){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      return data.exists("mail.message."+senderName);        
   }
   public Set<String> getMailSender(String name){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();            
      Set<String> key = data.getSection("mail.message").getKeys();
      key.removeIf(s -> s.contains("."));      
      return key;
   }
   public boolean isCountMailSender(String name, String senderName){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      return data.exists("mail.message."+senderName+".count");        
   }     
   public int getCountMailSender(String name, String senderName){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      return (int) data.get("mail.message."+senderName+".count");        
   }
   public void setCountMailSender(String name, String senderName, int count){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      data.set("mail.message."+senderName+".count", count);
      data.save();
   }
   public Set<String> getMailSenderWrite(String name, String senderName){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig(); 
      Set<String> key = data.getSection("mail.message."+senderName+".write").getKeys();
      key.removeIf(s -> s.contains("."));      
      return key;     
   }
   public boolean isCountMailSenderWrite(String name, String senderName, int msgCount){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      return data.exists("mail.message."+senderName+".write."+msgCount);        
   }
   public String getMailRead(String name, String senderName, int msgCount){ 
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      return (String) data.get("mail.message."+senderName+".write."+msgCount+".read");
   }
   public void setMailRead(String name, String senderName, int msgCount, String message){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      data.set("mail.message."+senderName+".write."+msgCount+".read", message);
      data.save();
   }
   public String getMailMsg(String name, String senderName, int msgCount){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      return (String) data.get("mail.message."+senderName+".write."+msgCount+".msg");
   }
   public void setMailMsg(String name, String senderName, int msgCount, String message){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      data.set("mail.message."+senderName+".write."+msgCount+".msg", message);
      data.save();
   }
   public List<String> getMailPlayers(String name){      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      return (List) data.get("mail.players");
   }
   public void setMailPlayers(String name, String playerName){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      List<String> mailPlayers = new ArrayList<String>(getMailPlayers(name));
      if(!mailPlayers.contains(playerName.toLowerCase())){
         mailPlayers.add(playerName.toLowerCase());
         data.set("mail.players", mailPlayers);
         data.save();
      }
   }
   
   public void addMail(String name, Player sender, String message){
      addMail(name, sender, message, true);
   }
   public void addMail(String name, Player sender, String message, Boolean tip){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      String senderName = sender.getName();
      String groupSender;
      if(!(sender instanceof Player)){
         groupSender = "§4Owner";
      }else{
         if(sender.isOp()){
            groupSender = "§2Admin";
         }else{
            groupSender = "§ePlayer";
         }
      }
      LocalDateTime myDateObj = LocalDateTime.now();
      DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
      String formattedDate = myDateObj.format(myFormatObj);
      String message1 = "§fจาก §f["+groupSender+"§f] §e"+senderName+" §fวันที่/เวลา §a"+formattedDate+"\n§fได้เขียนว่า §b"+message;
      int msgCount;
      if(isMailSender(name, senderName)){
         Set<String> msgWrite = getMailSenderWrite(name, senderName); 
         msgCount = msgWrite.size() + 1;
         for(int i = 0; i < msgWrite.size(); i++){//อันนี้แม่งงานแต่คิดอยุนาน
            if(isCountMailSenderWrite(name, senderName, msgCount)){
               msgCount++;
            }
         }
      }else{
         msgCount = 1;
      }
      int count;
      if(isCountMailSender(name, senderName)){
         count = getCountMailSender(name, senderName) + 1;
      }else{
         count = 1;
      }
      setCountMailSender(name, senderName, count);
      setMailRead(name, senderName, msgCount, "§cยังไม่อ่าน");
      setMailMsg(name, senderName, msgCount, message1);
      count = getCountMail(name) + 1;
      setCountMail(name, count);
      setMailPlayers(senderName, name);
      sender.sendMessage(getPrefix()+" ได้ส่งข้อความให้กับ "+name+" แล้ว คุณได้เขียนข้อความว่า "+message);
      Player player = getServer().getPlayer(name);
      if(player instanceof Player){
         if(tip){
            player.sendMessage(getPrefix()+" คุณมี §a1§fข้อความใหม่! จากผู้เล่นชื่อ §e"+senderName+" §fพิม /mail read "+senderName+" อ่านดูสิ!");
            player.sendTitle(("§fคุณมี §a1§fข้อความใหม่!"), ("§fจากผู้เล่นชื่อ §e"+senderName+" §fพิม /mail อ่านดูสิ!"));
         }
      }
   }
   public String readMail(String name, String senderName, int msgCount){
      return "§fหมายเลขข้อความที่ §b"+msgCount+" "+getMailRead(name, senderName, msgCount)+"\n"+getMailMsg(name, senderName, msgCount)+"\n§f-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-";
   }
   public String listMail(String name, String senderName){
      return "§f"+senderName+" §f(§a"+getCountMailSender(name, senderName)+"§f) ข้อความ";
   }
   public String countMail(String name){
      return getPrefix()+" คุณมี (§a"+getCountMail(name)+"§f) ข้อความจากทั้งหมด พิม /mail อ่านดูสิ!";
   }
   public void removeCountMailSender(String name, String senderName){
      int count;
      if(!(getCountMailSender(name, senderName) == 0)){
         count = getCountMailSender(name, senderName) - 1;
         setCountMailSender(name, senderName, count);
         count = getCountMail(name) - 1;
         setCountMail(name, count);
      }
   }
   public void removeMailSender(String name, String senderName, int msgCount){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      Map<String, Object> map = (Map<String, Object>) data.get("mail.message."+senderName+".write");
      if(map == null){
         return;
      }
      map.remove(String.valueOf(msgCount));
      data.set("mail.message."+senderName+".write", map);
      data.save();
   }
   public void delMailSender(Player player, String senderName, int msgCount){
      if(!isMailSender(player.getName(), senderName)){
         player.sendMessage(getPrefix()+" §cไม่พบข้อความของ "+senderName);
         return;
      }
      if(!isCountMailSenderWrite(player.getName(), senderName, msgCount)){
         player.sendMessage(getPrefix()+" §cไม่พบหมายเลขข้อความ "+msgCount);
         return;
      }
      removeCountMailSender(player.getName(), senderName);
      removeMailSender(player.getName(), senderName, msgCount);
      player.sendMessage(getPrefix()+" ได้ลบข้อความของ "+senderName+" หมายเลขข้อความ "+msgCount+" สำเร็จ");
   }
   public void resetMail(String name){
      PlayerData playerData = getPlayerData(name);
      Config data = playerData.getConfig();
      data.set("mail.count", 0);
      data.set("mail.message", new HashMap<>());
      data.save();
   }
}