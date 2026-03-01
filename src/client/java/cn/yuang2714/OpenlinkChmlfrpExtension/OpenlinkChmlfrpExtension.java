package cn.Yuang2714.OpenlinkChmlfrpExtension;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.Preferences;

public class OpenlinkChmlfrpExtension implements ClientModInitializer {
    public static final String MODID = "OpenlinkChmlfrpExtension";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static Preferences PREFERENCES = Preferences.userNodeForPackage(OpenlinkChmlfrpExtension.class);
	@Override
	public void onInitializeClient() {
		LOGGER.info("""
                
                  ____                       _       _         _        ____   _                  _    ______                 ______           _                       _
                 / __ \\                     | |     (_)       | |      / __ \\ | |                | |  | _____|               |  ____|        _| |_                    (_)
                | |  | | _ __    ___  _ __  | |      _  _ __  | | __  | |  \\_|| |___   _ ___ __  | |  | |____  _ __  _ __    | |____  _   _ |_   _|  ___  _ __   ____  _   ____   _ __ \s
                | |  | || '_ \\  / _ \\| '_ \\ | |     | || '_ \\ | |/ /  | |   _ | /__ \\ | '_  '_ \\ | |  |  ____|| '__|| '_ \\   |  ____|\\ \\_/ /  | |   / _ \\| '_ \\ / ___|| | / __ \\ | '_ \\
                | |__| || |_) ||  __/| | | || |____ | || | | ||   <   | |__/ || /  \\ || | | | | || |_ | |     | |   | |_) |  | |____  > _ <   | |__|  __/| | | |\\___ \\| || |__| || | | |
                 \\____/ | .__/  \\___||_| |_||______||_||_| |_||_|\\_\\   \\____/ |_|  |_||_| |_| |_||___||_|     |_|   | .__/   |______|/_/ \\_\\   \\__/ \\___||_| |_||____/|_| \\____/ |_| |_|
                        | |                                                                                         | |
                        |_|                                                                                         |_|
                """);
	}
}