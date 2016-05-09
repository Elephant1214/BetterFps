package guichaguri.betterfps;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

/**
 * @author Guilherme Chaguri
 */
public class UpdateChecker implements Runnable {

    private static boolean updateCheck = false;
    private static boolean done = false;
    private static Properties prop = null;

    public static void check() {
        if(!BetterFpsConfig.getConfig().updateChecker) {
            done = true;
            return;
        }
        if(!updateCheck) {
            updateCheck = true;
            Thread thread = new Thread(new UpdateChecker(), "BetterFps Update Checker");
            thread.setDaemon(true);
            thread.start();
        }
    }

    public static void showChat() {
        if(!done) return;
        if(prop == null) return;
        if(BetterFpsHelper.VERSION.equals(prop.getProperty("version"))) {
            prop = null;
            return;
        }
        if(!BetterFps.isClient) return;

        GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        if(chat == null) return;

        TextComponentString title = new TextComponentString("BetterFps " + prop.getProperty("version") + " is available");
        title.setStyle(title.getStyle().setColor(TextFormatting.GREEN).setBold(true));

        TextComponentString desc = new TextComponentString(prop.getProperty("quick-description"));
        desc.setStyle(desc.getStyle().setColor(TextFormatting.GRAY));

        TextComponentString buttons = new TextComponentString(" ");
        buttons.setStyle(buttons.getStyle().setColor(TextFormatting.YELLOW));
        buttons.appendSibling(createButton("Download", prop.getProperty("download-url"), "Click here to download the new version"));
        buttons.appendText("  ");
        buttons.appendSibling(createButton("More Information", prop.getProperty("moreinfo-url"), "Click here for more information about the update"));

        chat.printChatMessage(title);
        chat.printChatMessage(desc);
        chat.printChatMessage(buttons);

        prop = null;
    }

    public static void showConsole() {
        if(!done) return;
        if(prop == null) return;
        if(BetterFpsHelper.VERSION.equals(prop.getProperty("version"))) {
            prop = null;
            return;
        }

        BetterFps.log.info("BetterFps " + prop.getProperty("version") + " is available");
        BetterFps.log.info(prop.getProperty("quick-description"));
        BetterFps.log.info("More information at: " + prop.getProperty("moreinfo-url"));

        prop = null;
    }

    private static TextComponentString createButton(String label, String link, String hover) {
        TextComponentString sib = new TextComponentString("[" + label + "]");
        Style style = sib.getStyle();
        style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        TextComponentString h = new TextComponentString(hover);
        h.setStyle(h.getStyle().setColor(TextFormatting.RED));
        style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, h));
        sib.setStyle(style);
        return sib;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(BetterFpsHelper.UPDATE_URL);
            InputStream in = url.openStream();
            Properties p = new Properties();
            p.load(in);

            prop = p;
            done = true;

            if(!BetterFps.isClient) {
                showConsole();
            } else {
                if(Minecraft.getMinecraft().theWorld != null) {
                    showChat();
                }
            }
        } catch(IOException ex) {
            BetterFps.log.warn("Could not check for updates: " + ex.getLocalizedMessage());
            done = true;
        } catch(Exception ex) {
            ex.printStackTrace();
            done = true;
        }
    }
}