package hmmhmmmm.mail;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FormListener implements Listener{
   private Mail plugin;
   
   static final int MAIL_MENU = 0xAAA001;
   static final int MAIL_WRITE = 0xAAA002;
   static final int MAIL_SEEMSG = 0xAAA003;  
   static final int MAIL_SEEALL = 0xAAA004;  
   static final int MAIL_READMSG = 0xAAA005;  
   static final int MAIL_READALL = 0xAAA006;  
   static final int MAIL_READ_CONFIRM = 0xAAA007;
   
   static final int REPORT_UI = 0xAAA008;
   
   static final int MESSAGE_UI = 0xAAA009;
   
   static final int CONFIRM_UI = 0xAAA010;
   
   public FormListener(Mail plugin){
      this.plugin = plugin;
   }
   public Mail getPlugin(){
      return plugin;
   }
   public String getPrefix(){
      return plugin.getPrefix();
   }
   
   @EventHandler
   public void onFormResponse(PlayerFormRespondedEvent event){
      Player player = event.getPlayer();
      String[] mArr;
      if(event.getWindow() instanceof FormWindowSimple){
         FormWindowSimple window = (FormWindowSimple) event.getWindow();
         String button;
         if(!event.wasClosed()){
            button = window.getResponse().getClickedButton().getText();
         }else{
            button = "";
         }
         if(event.getFormID() == MAIL_MENU){
            if(event.wasClosed()){
               return;
            }
            if(window.getResponse().getClickedButtonId() == 0){
               MailWrite(player, "");
            }
            if(window.getResponse().getClickedButtonId() == 1){
               MailSeeAll(player, "");
            }
            if(window.getResponse().getClickedButtonId() == 2){
               MailReadAll(player, "");
            }
            if(window.getResponse().getClickedButtonId() == 3){
               ConfirmUI(player, "mail clear-all\n"+getPrefix()+" คุณแน่ใจแล้วใช่มั้ยที่จะลบข้อความของผู้ที่ส่งมาทั้งหมด");
            }
         }
         if(event.getFormID() == MAIL_SEEALL){
            if(event.wasClosed()){
               return;
            }
            if(!plugin.isMailSender(button, player.getName())){
               MailSeeAll(player, "§cเกิดข้อผิดพลาด\n§eไม่พบข้อความของคุณ? คุณไม่ได้ส่งข้อความไปหาคนนี้ หรือ เค้าลบข้อความของคุณไปแล้ว");
               return;
            }
            List<String> array = new ArrayList<String>();
            for(String msgCount2 : plugin.getMailSenderWrite(button, player.getName())){
               array.add(plugin.readMail(button, player.getName(), Integer.parseInt(msgCount2)));
            }
            String msg = String.join("\n", array);
            MailSeeMsg(player, button, msg);
         }
         if(event.getFormID() == MAIL_READALL){
            if(event.wasClosed()){
               return;
            }
            Set<String> mailSender = plugin.getMailSender(player.getName());
            List<String> array = new ArrayList<String>();
            for(String senderName : mailSender){
               array.add(senderName);
            }
            String name = array.get(window.getResponse().getClickedButtonId());
            int count = plugin.getCountMail(player.getName()) - plugin.getCountMailSender(player.getName(), name);
            plugin.setCountMail(player.getName(), count);
            plugin.setCountMailSender(player.getName(), name, 0);
            for(String msgCount2 : plugin.getMailSenderWrite(player.getName(), name)){
               plugin.setMailRead(player.getName(), name, Integer.parseInt(msgCount2), "§aอ่านแล้ว");
            }
            List<String> array2 = new ArrayList<String>();
            for(String msgCount2 : plugin.getMailSenderWrite(player.getName(), name)){
               array2.add(plugin.readMail(player.getName(), name, Integer.parseInt(msgCount2)));
            }
            String msg = String.join("\n", array2);
            MailReadMsg(player, name, msg);
         }
      }
      if(event.getWindow() instanceof FormWindowModal){
         FormWindowModal window = (FormWindowModal) event.getWindow();
         String button = window.getResponse().getClickedButtonText();
         if(event.getFormID() == CONFIRM_UI){
            if(window.getResponse().getClickedButtonId() == 0){
               String content = window.getContent();
               String[] cmd = content.split("\n");
               plugin.getServer().dispatchCommand(player, cmd[0]);
            }
            if(window.getResponse().getClickedButtonId() == 1){
            }
         }
         if(event.getFormID() == MAIL_READ_CONFIRM){
            if(window.getResponse().getClickedButtonId() == 0){
               String title = window.getTitle();
               String[] arr = title.split(" ");
               String name = arr[2];
               int count = plugin.getCountMail(player.getName()) - plugin.getCountMailSender(player.getName(), name);
               plugin.setCountMail(player.getName(), count);
               plugin.setCountMailSender(player.getName(), name, 0);
               for(String msgCount2 : plugin.getMailSenderWrite(player.getName(), name)){
                  plugin.setMailRead(player.getName(), name, Integer.parseInt(msgCount2), "§aอ่านแล้ว");
               }
               List<String> array2 = new ArrayList<String>();
               for(String msgCount2 : plugin.getMailSenderWrite(player.getName(), name)){
                  array2.add(plugin.readMail(player.getName(), name, Integer.parseInt(msgCount2)));
               }
               String msg = String.join("\n", array2);
               MailReadMsg(player, name, msg);
            }
            if(window.getResponse().getClickedButtonId() == 1){
            
            }
         }
      }
      if(event.getWindow() instanceof FormWindowCustom){
         FormWindowCustom window = (FormWindowCustom) event.getWindow();
         if(event.getFormID() == MAIL_WRITE){
            if(event.wasClosed()){
               return;
            }
            String name;
            name = window.getResponse().getInputResponse(0);
            String message;
            message = window.getResponse().getInputResponse(1);
            mArr = message.split(" ");
            String[] nArr = name.split(" ");
            if(nArr[0] == ""){
               MailWrite(player, "§cเกิดข้อผิดพลาด\n§e<ชื่อผู้เล่น> กรุณาอย่าว่างข้อความ");
               return;
            }
            name = nArr[0];
            PlayerData playerData = new PlayerData(plugin, name);
            if(!playerData.isData()){
               MailWrite(player, "§cเกิดข้อผิดพลาด\n§eไม่พบชื่อของผู้เล่น");
               return;
            }
            if(mArr[0] == ""){
               MailWrite(player, "§cเกิดข้อผิดพลาด\n§eกรุณาอย่าว่างข้อความ");
               return;
            }
            plugin.addMail(name, player, message, false);
            Player sOnline = plugin.getServer().getPlayer(name);
            if(sOnline instanceof Player){
               MailReadConfirm(sOnline, player.getName());
            }
         }
         if(event.getFormID() == MAIL_SEEMSG){
            if(event.wasClosed()){
               return;
            }
            String title = window.getTitle();
            String[] arr = title.split(" ");
            String name = arr[2];
            String message;
            message = window.getResponse().getInputResponse(1);
            String content = window.getResponse().getLabelResponse(0);
            mArr = message.split(" ");
            if(mArr[0] == ""){
               MailSeeMsg(player, name, "§cเกิดข้อผิดพลาด\n§eกรุณาอย่าว่างข้อความ\n"+content);
               return;
            }
            plugin.addMail(name, player, message, false);
            Player sOnline = plugin.getServer().getPlayer(name);
            if(sOnline instanceof Player){
               MailReadConfirm(sOnline, player.getName());
            }
         }
         if(event.getFormID() == MAIL_READMSG){
            if(event.wasClosed()){
               return;
            }
            String title = window.getTitle();
            String[] arr = title.split(" ");
            String name = arr[2];
            String message;
            message = window.getResponse().getInputResponse(2);
            String content = window.getResponse().getLabelResponse(0);
            mArr = message.split(" ");
            if(window.getResponse().getDropdownResponse(1).getElementID() == 0){
               if(mArr[0] == ""){
                  MailReadMsg(player, name, "§cเกิดข้อผิดพลาด\n§eกรุณาอย่าว่างข้อความ\n"+content);
                  return;
               }
               plugin.addMail(name, player, message, false);
               Player sOnline = plugin.getServer().getPlayer(name);
               if(sOnline instanceof Player){
                  MailReadConfirm(sOnline, player.getName());
               }
            }
            if(window.getResponse().getDropdownResponse(1).getElementID() == 1){
               if(mArr[0] == ""){
                  MailReadMsg(player, name, "§cเกิดข้อผิดพลาด\n§eกรุณาอย่าว่างข้อความ\n"+content);
                  return;
               }
               int msgCount;
               try{
                  msgCount = Integer.parseInt(message);
               }catch(NumberFormatException e){
                  MailReadMsg(player, name, "§cเกิดข้อผิดพลาด\n§eกรุณาเขียนให้เป็นตัวเลข\n"+content);
                  return;
               }
               plugin.delMailSender(player, name, msgCount);
            }
         }
         if(event.getFormID() == REPORT_UI){
            if(event.wasClosed()){
               return;
            }
            String content = window.getResponse().getLabelResponse(0);
            String[] arr = content.split("\n");
            String name = arr[0];
            String message;
            message = window.getResponse().getInputResponse(2);
            mArr = message.split(" ");
            if(window.getResponse().getDropdownResponse(1).getElementID() == 0){
               if(mArr[0] == ""){
                  ReportUI(player, "§cเกิดข้อผิดพลาด\n§eกรุณาอย่าว่างข้อความ\n"+content);
                  return;
               }
               plugin.addMail(name, player, message, false);
               Player sOnline = plugin.getServer().getPlayer(name);
               if(sOnline instanceof Player){
                  MailReadConfirm(sOnline, player.getName());
               }
            }
            if(window.getResponse().getDropdownResponse(1).getElementID() == 1){
               if(!plugin.isMailSender(player.getName(), name)){
                  MessageUI(player, "§cเกิดข้อผิดพลาด\n§eแอดมินยังไม่ได้ตอบกลับ");
                  return;
               }
               int count = plugin.getCountMail(player.getName()) - plugin.getCountMailSender(player.getName(), name);
               plugin.setCountMail(player.getName(), count);
               plugin.setCountMailSender(player.getName(), name, 0);
               for(String msgCount2 : plugin.getMailSenderWrite(player.getName(), name)){
                  plugin.setMailRead(player.getName(), name, Integer.parseInt(msgCount2), "§aอ่านแล้ว");
               }
               List<String> array2 = new ArrayList<String>();
               for(String msgCount2 : plugin.getMailSenderWrite(player.getName(), name)){
                  array2.add(plugin.readMail(player.getName(), name, Integer.parseInt(msgCount2)));
               }
               String msg = String.join("\n", array2);
               MailReadMsg(player, name, msg);
            }
         }
      }
   }
   public void MessageUI(Player player, String message){        
      FormWindowSimple window = new FormWindowSimple("Message UI", message);
      player.showFormWindow(window, MESSAGE_UI);
   }
   public void ConfirmUI(Player player, String content){            
      FormWindowModal window = new FormWindowModal("Confirm UI", content, "§aตกลง", "§cไม่");
      player.showFormWindow(window, CONFIRM_UI);
   }
   public void MailMenu(Player player){
      FormWindowSimple window = new FormWindowSimple("Mail Menu", "§eโปรดเลือกเมนู.");
      window.addButton(new ElementButton("§fส่งข้อความหาผู้เล่น"));
      window.addButton(new ElementButton("§fดูข้อความที่เคยส่งไป"));                 
      window.addButton(new ElementButton("§fคุณมี (§a"+plugin.getCountMail(player.getName())+"§f) ข้อความจากทั้งหมด"));
      window.addButton(new ElementButton("§cลบข้อความของผู้ที่ส่งมาทั้งหมด"));
      player.showFormWindow(window, MAIL_MENU);
   }
   public void MailWrite(Player player, String content){            
      FormWindowCustom window = new FormWindowCustom("Mail Write");
      window.addElement(new ElementInput("§eชื่อผู้เล่น", ""));       
      window.addElement(new ElementInput("§bข้อความที่จะส่ง", ""));       
      window.addElement(new ElementLabel(content));
      player.showFormWindow(window, MAIL_WRITE);
   }
   public void MailSeeMsg(Player player, String senderName, String content){            
      FormWindowCustom window = new FormWindowCustom("Mail SeeMsg "+senderName);
      Player pOnline = plugin.getServer().getPlayer(senderName);
      String online;
      if(pOnline instanceof Player){
         online = "§aออนไลน์§f";
         }else{
         online = "§cออฟไลน์§f";
      }
      content = "§f§lห้องแชทของ §e"+senderName+" §fตอนนี้ "+online+" §fอยู่\n§r"+content;
      window.addElement(new ElementLabel(content));
      window.addElement(new ElementInput("", "ส่งขอความ"));  
      player.showFormWindow(window, MAIL_SEEMSG);
   }
   public void MailSeeAll(Player player, String content){     
      List<String> mailSender = new ArrayList<String>(plugin.getMailPlayers(player.getName()));
      if(mailSender.size() == 0){
         MessageUI(player, "§cเกิดข้อผิดพลาด\n§eคุณยังไม่ได้ส่งข้อความหาใคร");
         return;
      }
      FormWindowSimple window = new FormWindowSimple("Mail SeeAll", content);
      for(String senderName : plugin.getMailPlayers(player.getName())){
         window.addButton(new ElementButton(senderName));      
      }
      player.showFormWindow(window, MAIL_SEEALL);
   }
   public void MailReadMsg(Player player, String senderName, String content){            
      FormWindowCustom window = new FormWindowCustom("Mail ReadMsg "+senderName);
      Player pOnline = plugin.getServer().getPlayer(senderName);
      String online;
      if(pOnline instanceof Player){
         online = "§aออนไลน์§f";
         pOnline.sendMessage(getPrefix()+" §b"+player.getName()+" §fได้อ่านข้อความของคุณแล้ว!");
      }else{
         online = "§cออฟไลน์§f";
      }
      content = "§f§lห้องแชทของ §e"+senderName+" §fตอนนี้ "+online+" §fอยู่\n§r"+content;
      window.addElement(new ElementLabel(content));
      List<String> menu = Arrays.asList("ตอบกลับ", "ลบข้อความ\nกรุณาใส่หมายเลขข้อความ");
      window.addElement(new ElementDropdown("เมนู", menu));
      window.addElement(new ElementInput("", ""));  
      player.showFormWindow(window, MAIL_READMSG);
   }
   public void MailReadAll(Player player, String content){     
      Set<String> mailSender = plugin.getMailSender(player.getName());
      if(mailSender.size() == 0){
         MessageUI(player, "§cเกิดข้อผิดพลาด\n§eไม่มีใครส่งข้อความหาคุณ");
         return;
      }
      FormWindowSimple window = new FormWindowSimple("Mail ReadAll", content);
      for(String senderName : mailSender){
         window.addButton(new ElementButton(plugin.listMail(player.getName(), senderName)));
      }
      player.showFormWindow(window, MAIL_READALL);
   }
   public void MailReadConfirm(Player player, String senderName){
      Player sender = plugin.getServer().getPlayer(senderName);
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
      groupSender = "§f["+groupSender+"§f] "+senderName;
      String content = getPrefix()+" ผู้เล่น "+groupSender+" ได้ส่งข้อความหาคุณ\nคุณต้องการอ่านหรือไม่?";
      FormWindowModal window = new FormWindowModal("Mail Read "+senderName, content, "§aอ่าน", "§fเก็บไว้");
      player.showFormWindow(window, MAIL_READ_CONFIRM);
   }
   public void ReportUI(Player player, String content){
      String senderName = (String) plugin.getConfig().get("report.name");
      String online;
      Player pOnline = plugin.getServer().getPlayer(senderName);
      if(pOnline instanceof Player){
         online = "§aออนไลน์§f";
      }else{
         online = "§cออฟไลน์§f";
      }
      if(plugin.isMailSender(senderName, player.getName())){
         List<String> array2 = new ArrayList<String>();
         for(String msgCount2 : plugin.getMailSenderWrite(senderName, player.getName())){
            array2.add(plugin.readMail(senderName, player.getName(), Integer.parseInt(msgCount2)));
         }
         String msg = String.join("\n", array2);
         content = "§f[§bReport§f] คุณสามารถ report ได้ดังนี้\n§e1.§fแจ้งให้เพิ่มระบบได้\n§e2.§fแจ้งให้แก้บัคในเซิฟต่างๆ #อันนี้มีรางวัลให้\n§e3.§fฝากข้อความหาแอดมินได้\n§e4.§fแจ้งปัญหาต่างๆ\n§a[ข้อความที่คุณเคยแจ้งไป]\n"+msg;
      }else{
         content = "§f[§bReport§f] คุณสามารถ report ได้ดังนี้\n§e1.§fแจ้งให้เพิ่มระบบได้\n§e2.§fแจ้งให้แก้บัคในเซิฟต่างๆ #อันนี้มีรางวัลให้\n§e3.§fฝากข้อความหาแอดมินได้\n§e4.§fแจ้งปัญหาต่างๆ";
      }
      FormWindowCustom window = new FormWindowCustom("§l§bReport §fUI ตอนนี้ผู้ดูแล "+online+" อยู่");
      window.addElement(new ElementLabel(senderName+"\n"+content));
      int msgCount;
      if(plugin.isMailSender(player.getName(), senderName)){
         msgCount = plugin.getCountMailSender(player.getName(), senderName);
      }else{
         msgCount = 0;
      }
      List<String> menu = Arrays.asList("§aส่งข้อความ", "§fข้อความที่แอดมินตอบกลับหาคุณ  (§a"+msgCount+"§f) ข้อความ\nดูข้อความกด Submit");
      window.addElement(new ElementDropdown("เมนู", menu));
      window.addElement(new ElementInput("", ""));  
      player.showFormWindow(window, REPORT_UI);
   }
}