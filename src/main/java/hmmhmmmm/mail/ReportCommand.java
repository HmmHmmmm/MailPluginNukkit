package hmmhmmmm.mail;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class ReportCommand extends Command{
   private Mail plugin;
   public ReportCommand(String name, Mail plugin){
      super(name);
      this.plugin = plugin;
      this.setPermission("mirage.command.report");
   }
   public void sendConsoleError(CommandSender sender){
      sender.sendMessage("§cขออภัย: คำสั่งสามารถพิมพ์ได้เฉพาะในเกมส์");
   }
   public void sendPermissionError(CommandSender sender){
      sender.sendMessage("§cขออภัย: คุณไม่สามารถพิมพ์คำสั่งนี้ได้");
   }
   @Override
   public boolean execute(CommandSender sender, String commandLabel, String[] args){
      if(!this.testPermission(sender)){
         return true;
      }
      if(!(sender instanceof Player)){
         sendConsoleError(sender);
         return true;
      }
      if(args.length == 0){
         plugin.getForm().ReportUI((Player) sender, "");
         return true;
      }
      return true;
   }
}