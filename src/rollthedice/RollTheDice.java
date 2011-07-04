/*
 * RollTheDice.java
 * 
 * @author: Matt Ryder
 * @since:  2011.0703
 */
package rollthedice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;
import java.util.logging.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.*;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author matt
 */
public class RollTheDice extends JavaPlugin {

    static final Logger log = Logger.getLogger("Minecraft");
    private boolean hasOccurred = false;
    private boolean validConfig = false;
    static final String mainDirectory = "plugins/RollTheDice";
    static File rollConfig = new File(mainDirectory + File.separator + "RollConfig.dat");
    static Properties prop = new Properties();
    //Permissions Handler for the Permissions plugin.
    public static PermissionHandler permissionHandler;

    public static void main(String[] args) {
        //Do Nothing
    }

    @Override
    public void onEnable() {
        log.info("Roll The Dice - v0.2 (Matt Ryder) [ENABLED]");
        PluginManager pm = this.getServer().getPluginManager();
        PluginDescriptionFile pdf = this.getDescription();

        //Setup the Permissions-plugin hook:
        setupPermissions();

        new File(mainDirectory).mkdir();

        if (!rollConfig.exists()) {
            {
                FileOutputStream out = null;
                try {
                    rollConfig.createNewFile();
                    out = new FileOutputStream(rollConfig);
                    prop.put("Diamond", "1");
                    prop.put("Cobblestone", "25");
                    prop.put("Wool", "30");
                    prop.put("Dirt", "-1");
                    prop.store(out, " This is the Default RollTheDice Config! Update Me!\n Format: [Material]:[Stack Size]  E.G Diamond:64\n Use -1 for Amount for a random amount!");
                    out.flush();
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(RollTheDice.class.getName()).log(Level.SEVERE, "IOEXCEPTION!", ex);
                } finally {
                    try {
                        out.close();
                    } catch (IOException ex) {
                        Logger.getLogger(RollTheDice.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }
        
        //Load Config, return if invalid.
        if (LoadConfiguration()) {
            validConfig = true;
        }
    }

    @Override
    public void onDisable() {
        log.info("Roll The Dice - v0.2 (Matt Ryder) [DISABLED]");
    }

    private void setupPermissions() {

        if (permissionHandler != null) {
            return;
        }

        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (permissionsPlugin == null) {
            log.info("Roll The Dice - Permission system not detected, defaulting to OP");
            return;
        }

        permissionHandler = ((Permissions) permissionsPlugin).getHandler();
        log.log(Level.INFO, "Roll The Dice - Found and will use plugin " + ((Permissions) permissionsPlugin).getDescription().getFullName());
    }

    public boolean LoadConfiguration() {
        try {
            FileInputStream in = new FileInputStream(rollConfig);
            prop.load(in);

            //No records indicates invalidity. Return that.
            if (prop.size() == 0) {
                log.info(Level.WARNING + " Roll The Dice - No Items in config.dat. Suspending Self.");
                return false;
            }
            return true;

        } catch (IOException ex) {
            Logger.getLogger(RollTheDice.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        String commandName = command.getName().toLowerCase();
        Player player = (Player) sender;
        
        boolean isOp = sender.isOp();
        
        if (validConfig) 
        {

            if (permissionHandler == null) 
            {
                //Support for basic OPs usage.
                if (sender.isOp()) 
                {
                    roll(sender, args);
                    return true;
                } 
                else if (!hasOccurred) 
                {
                    roll(sender, args);
                    hasOccurred = true;
                    return true;
                }
                else if (hasOccurred) 
                { //If the player has activated it.
                    player.sendMessage(ChatColor.BLUE + "[RollTheDice] " + ChatColor.WHITE + "You can only Roll once per session!");
                    return true;
                }
            } 
            else //Permissions is hooked and running.
            {
                boolean isMulti = permissionHandler.has(player, "rollthedice.roll.multi");

                //Check if the player is authorized to use /roll, if not: Return.
                if (!permissionHandler.has(player, "rollthedice.roll")) 
                {
                    return true;
                }

                //roll is called and not previously called:
                if (commandName.equals("roll") && (hasOccurred == false)) 
                {
                    roll(sender, args);
                    if (!isMulti) 
                    {
                        hasOccurred = true;
                    }  //No Multi permission? No multi-action.
                    return true;
                } 
                else if (hasOccurred) 
                {
                    player.sendMessage(ChatColor.BLUE + "[RollTheDice] " + ChatColor.WHITE + "You can only Roll once per session!");
                    return true;
                } 
            }
        }
        else { 
            player.sendMessage(ChatColor.BLUE + "[RollTheDice] " + ChatColor.WHITE + "RTD is disabled. Ask Op to configure Plugin"); return true; 
        }
        //Else it's a bad name.
        return false;
    }

    /* *
     * roll: Called to generate a random Materal + Amount for an ItemStack to
     *       give to the user, from Properties loaded from file.
     * 
     * @param sender The CommandSender object of who init'd the command.
     * @param args Any additional arguments sent to the command.
     * 
     */
    private void roll(CommandSender sender, String[] args) {
        //Default material that won't cause game/economy problems
        //Also, set the amount to autogen.
        Material rolledMaterial = Material.SNOW_BALL;
        int intPredefinedAmount = -1;
        int option = 0;
        int amount = 0;

        Random rndGen = new Random();
        Player player = (Player) sender;
        PlayerInventory pInv = player.getInventory();

        try {
            option = rndGen.nextInt(prop.size() - 1);
            amount = rndGen.nextInt(64);

            Enumeration e = prop.propertyNames();
            int c = 0; //Enum counter
            while (e.hasMoreElements()) {
                String rmString = (String) e.nextElement();

                if("Sand".equals(rmString)) {
                    log.info("Out SAND");
                }
                if (c == option) {
                    //Found the option rolled. 
                    //Get it's Material and overridden amount.
                    try {
                        rolledMaterial = findMaterial(rmString);
                        intPredefinedAmount = Integer.parseInt(prop.getProperty(rmString));
                        break;
                    } catch (Exception ex) {
                        log.info(ex.toString());
                    }
                }
                c++;
            }
        } catch (Exception ex) {
            log.info(ex.toString());
        }

        if (intPredefinedAmount == -1) {
            //-1 Found as Amount Key, convert Amount to our Amount rolled.
            intPredefinedAmount = amount;
        }

        try {
            ItemStack iStack = new ItemStack(rolledMaterial, intPredefinedAmount);
            pInv.addItem(iStack);

            player.sendMessage(ChatColor.BLUE + "[RollTheDice] " + ChatColor.WHITE + "You rolled " + intPredefinedAmount + " of " + rolledMaterial.toString());
        } catch (Exception ex) {
            log.info(ex.toString());
        }
    }

    /* *
     * findMaterial: Locates the Material Object for a given String/Integer  
     * Courtesy of some kind soul from Bukkit Forums. Thank you! 
     */
    private static Material findMaterial(Object m) throws IllegalArgumentException {
        if (m instanceof String) {
            return Material.getMaterial(((String) m).toUpperCase());
        } else if (m instanceof Integer) {
            return Material.getMaterial((Integer) m);
        } else {
            throw new IllegalArgumentException("Invalid material: " + m);
        }
    }
}