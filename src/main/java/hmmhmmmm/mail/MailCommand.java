package hmmhmmmm.mail;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MailCommand extends Command{
   private Mail plugin;
   private String playerName;
   private int count;
   private Player player;
   private PlayerData playerData;
   private int msgCount;
   private Set<String> mailPlayer;
   
   public MailCommand(String name, Mail plugin){
      super(name);
      this.plugin = plugin;
   }
   public String getPrefix(){
      return plugin.getPrefix();
   }
   public void sendConsoleError(CommandSender sender){
      sender.sendMessage("§cขออภัย: คำสั่งสามารถพิมพ์ได้เฉพาะในเกมส์");
   }
   public void sendPermissionError(CommandSender sender){
      sender.sendMessage("§cขออภัย: คุณไม่สามารถพิมพ์คำสั่งนี้ได้");
   }
   public void sendHelp(CommandSender sender){
      sender.sendMessage(getPrefix()+" : §fCommand");
      if(sender.hasPermission("mail.command.info")){
         sender.sendMessage("§a/mail info : §fเครดิตผู้สร้างปลั๊กอิน");
      }
      if(sender.hasPermission("mail.command.write")){
         sender.sendMessage("§a/mail write <ชื่อผู้เล่น> : §fแล้วพิมที่แชทเขียนข้อความเพื่อส่งข้อความให้ผู้เล่นคนนั้น");
      }
      if(sender.hasPermission("mail.command.read")){
         sender.sendMessage("§a/mail read <ชื่อผู้ที่ส่งข้อความ> : §fอ่านข้อความผู้ที่ส่งมา");
      }
      if(sender.hasPermission("mail.command.readall")){
         sender.sendMessage("§a/mail read-all : §fอ่านข้อความผู้ที่ส่งมาทั้งหมด");
      }
      if(sender.hasPermission("mail.command.clear")){
         sender.sendMessage("§a/mail clear <ชื่อผู้ที่ส่งข้อความ> <หมายเลขข้อความ> : §fเพื่อลบข้อความนั้น");
      }
      if(sender.hasPermission("mail.command.clearall")){
         sender.sendMessage("§a/mail clear-all : §fเพื่อลบข้อความของผู้ที่ส่งมาทั้งหมด");
      }
      if(sender.hasPermission("mail.command.see")){
         sender.sendMessage("§a/mail see <ชื่อผู้เล่น> : §fเพื่อดูข้อความที่เราส่งไปว่าเค้าอ่านรึยัง?");
      }
      if(sender.hasPermission("mail.command.read") && sender.hasPermission("mail.command.readall")){
         sender.sendMessage("§fคุณมี (§a"+plugin.getCountMail(sender.getName())+"§f) ข้อความ");
         sender.sendMessage("§eรายชื่อ ผู้ที่ส่งข้อความ มาหาคุณ:");
         mailPlayer = plugin.getMailSender(sender.getName());
         if(mailPlayer.size() == 0){
            sender.sendMessage("§cไม่มี");
         }else{
            for(String playerName : mailPlayer){
               sender.sendMessage(plugin.listMail(sender.getName(), playerName));
            }
         }
      }
   }
   @Override
   public boolean execute(CommandSender sender, String commandLabel, String[] args){
      if(!(sender instanceof Player)){
         sendConsoleError(sender);
         return true;
      }
      if(args.length == 0){
         plugin.getForm().MailMenu((Player) sender);
         sender.sendMessage("§eคุณสามารถดูคำสั่งเพิ่มเติมได้โดยใช้ /mail help");
         return true;
      }
      mailPlayer = plugin.getMailSender(sender.getName());
      switch(args[0].toLowerCase()){
         case "help":
            this.sendHelp(sender);
            break;
         case "info":
            if(!sender.hasPermission("mail.command.info")){
               sendPermissionError(sender);
               return true;
            }
            sender.sendMessage(plugin.getPluginInfo());
            break;
         case "write":
            if(!sender.hasPermission("mail.command.write")){
               sendPermissionError(sender);
               return true;
            }
            if(args.length < 2){
               sender.sendMessage("§cลอง: /mail write <ชื่อผู้เล่น>");
               return true;
            }
            playerData = new PlayerData(plugin, args[1]);
            if(!playerData.isData()){
               sender.sendMessage(getPrefix()+" §cไม่พบชื่อผู้เล่น");
               return true;
            }
            plugin.map.put((Player) sender, playerData.getName());
            sender.sendMessage(getPrefix()+": §aกรุณาพิมพ์ที่แชทเพื่อเขียนข้อความ");
            break;
         case "read":
            if(!sender.hasPermission("mail.command.read")){
               sendPermissionError(sender);
               return true;
            }
            if(args.length < 2){
               sender.sendMessage("§cลอง: /mail write <ชื่อผู้ที่ส่งข้อความ>");
               return true;
            }
            if(mailPlayer.size() == 0){
               sender.sendMessage(getPrefix()+" §cยังไม่มีใครส่งข้อความมาหาคุณ");
               return true;
            }
            playerName = args[1];
            if(!plugin.isMailSender(sender.getName(), playerName)){
               sender.sendMessage(getPrefix()+" §cไม่พบชื่อผู้ที่ส่งข้อความ");
               return true;
            }
            count = plugin.getCountMail(sender.getName()) - plugin.getCountMailSender(sender.getName(), playerName);
            plugin.setCountMail(sender.getName(), count);
            plugin.setCountMailSender(sender.getName(), playerName, 0);
            for(String msgCount2 : plugin.getMailSenderWrite(sender.getName(), playerName)){
               plugin.setMailRead(sender.getName(), playerName, Integer.parseInt(msgCount2), "§aอ่านแล้ว");
            }
            for(String msgCount2 : plugin.getMailSenderWrite(sender.getName(), playerName)){
               sender.sendMessage(plugin.readMail(sender.getName(), playerName, Integer.parseInt(msgCount2)));
            }
            player = plugin.getServer().getPlayer(playerName);
            if(player instanceof Player){
               player.sendMessage(getPrefix()+" §b"+sender.getName()+" §fได้อ่านข้อความของคุณแล้ว!");
            }
            break;
         case "read-all":
            if(!sender.hasPermission("mail.command.readall")){
               sendPermissionError(sender);
               return true;
            }
            if(mailPlayer.size() == 0){
               sender.sendMessage(getPrefix()+" §cยังไม่มีใครส่งข้อความมาหาคุณ");
               return true;
            }
            for(String playerName2 : plugin.getMailSender(sender.getName())){
               if(!plugin.isMailSender(sender.getName(), playerName2)){
                  sender.sendMessage(getPrefix()+" §cไม่พบชื่อผู้ที่ส่งข้อความ");
                  return true;
               }
               count = plugin.getCountMail(sender.getName()) - plugin.getCountMailSender(sender.getName(), playerName2);
               plugin.setCountMail(sender.getName(), count);
               plugin.setCountMailSender(sender.getName(), playerName2, 0);
               for(String msgCount2 : plugin.getMailSenderWrite(sender.getName(), playerName2)){
                  plugin.setMailRead(sender.getName(), playerName2, Integer.parseInt(msgCount2), "§aอ่านแล้ว");
               }
               for(String msgCount2 : plugin.getMailSenderWrite(sender.getName(), playerName2)){
                  sender.sendMessage(plugin.readMail(sender.getName(), playerName2, Integer.parseInt(msgCount2)));
               }
               player = plugin.getServer().getPlayer(playerName2);
               if(player instanceof Player){
                  player.sendMessage(getPrefix()+" §b"+sender.getName()+" §fได้อ่านข้อความของคุณแล้ว!");
               }
            }
            break;
         case "clear":
            if(!sender.hasPermission("mail.command.clear")){
               sendPermissionError(sender);
               return true;
            }
            if(args.length < 3){
               sender.sendMessage("§cลอง: /mail clear <ชื่อผู้ที่ส่งข้อความ> <หมายเลขข้อความ>");
               return true;
            }
            playerName = args[1];
            try{
               msgCount = Integer.parseInt(args[2]);
            }catch(NumberFormatException e){
               sender.sendMessage("§cลอง: /mail clear <ชื่อผู้ที่ส่งข้อความ> <หมายเลขข้อความ>");
               return true;
            }
            plugin.delMailSender((Player) sender, playerName, msgCount);
            break;
         case "clear-all":
            if(!sender.hasPermission("mail.command.clearall")){
               sendPermissionError(sender);
               return true;
            }
            plugin.resetMail(sender.getName());
            sender.sendMessage(getPrefix()+" §aลบข้อความของผู้ที่ส่งมาทั้งหมดเรียบร้อย!");
            break;
         case "see":
            if(!sender.hasPermission("mail.command.see")){
               sendPermissionError(sender);
               return true;
            }
            if(args.length < 2){
               sender.sendMessage("§cลอง: /mail see <ชื่อผู้เล่น>");
               return true;
            }
            playerData = new PlayerData(plugin, args[1]);
            if(!playerData.isData()){
               sender.sendMessage(getPrefix()+" §cไม่พบชื่อผู้เล่น");
               return true;
            }
            if(!plugin.isMailSender(playerData.getName(), sender.getName())){
               sender.sendMessage(getPrefix()+" §cขออภัย: ไม่พบข้อความของคุณ? คุณไม่ได้ส่งข้อความไปหาคนนี้ หรือ เค้าลบข้อความของคุณไปแล้ว");
               return true;
            }
            for(String msgCount2 : plugin.getMailSenderWrite(playerData.getName(), sender.getName())){
               sender.sendMessage(plugin.readMail(playerData.getName(), sender.getName(), Integer.parseInt(msgCount2)));
            }
            break;
         default:
            sendHelp(sender);
            break;
      }
      return true;
   }
}