package cn.Yuang2714.OpenlinkChmlfrpExtension.Statics;

import cn.Yuang2714.OpenlinkChmlfrpExtension.Tools.Network;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class URLs {
    public static final String panel = "https://panel.chmlfrp.net/home";
    public static final String api = "https://cf-v2.uapis.cn/";
    public static final String ipCheck = "http://ip-api.com/json/";
    public static final String nodes = "https://panel.chmlfrp.net/tunnel/status";
    public static final String oauth2 = "https://account-api.qzhua.net/oauth2/";
    public static final String clientID = "019d57504ef77f9ea3dd50eb07325fdb";

    public static double[] exchangeLocation(String loc) throws Exception {
        JsonObject location = JsonParser.parseString(Network.get(
                "http://api.tianditu.gov.cn/geocoder?tk=837963e070323c0fd804f0ac51c142b4&ds={\"keyWord\":\""
                + loc
                + "\"}",
                false
        )).getAsJsonObject()
                .get("location")
                .getAsJsonObject();
        return new double[]{location.get("lon").getAsDouble(), location.get("lat").getAsDouble()};
    }
}
