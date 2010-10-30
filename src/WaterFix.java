//import java.util.*;
import java.util.logging.Logger;

public class WaterFix extends Plugin {
	private final String name = "Water Freeze";
	private final String version = "0.1a";
	private Logger log;
	final Listener listener = new Listener();

	@Override
	public void enable() {
		log = Logger.getLogger("Minecraft");
		log.info(name + " " + version + " initialising...");		
	}

	@Override
	public void disable() {
		log = Logger.getLogger("Minecraft");
		log.info(name + " " + version + " deinitialising...");
		etc.getInstance().removeCommand("/pausewater");

	}
	
	public void initialise() {
		etc.getInstance().addCommand("/pausewater", "Pause nearby waterflows");
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
	}
	
	class Listener extends PluginListener {
		public boolean onCommand(Player player, String[] split) {
			if (!player.canUseCommand("/pausewater")) {
				player.sendMessage("You can't use /pausewater");
				return false;
			} else {
				player.sendMessage("You can /pausewater.");
				return true;
			}
		}
	}
}
