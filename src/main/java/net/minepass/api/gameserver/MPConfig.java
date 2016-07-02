package net.minepass.api.gameserver;

import java.util.HashMap;

public class MPConfig {

    public String variant;
    public HashMap<String,String> variant_config;
    public String api_host;
    public String server_uuid;
    public String server_secret;

    public MPConfig() {
        this.variant_config = new HashMap<>();
    }
}
