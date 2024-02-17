package com.bungdz.Wizards_App.constants;

import com.bungdz.Wizards_App.models.ThingsBoardInfo;

public class Constants {
    public static final String WS_URL = "wss://thingsboard.cloud:443/api/ws/plugins/telemetry";
    public static final String HiveMQ_URL = "tcp://broker.mqttdashboard.com:1883";
    public static final String CLIENT_HIVEMQ_ID = "androidClientId1";
    public static final String TOPIC_ELEMENT = "app/home/element";
    public static final String TOPIC_ALARM = "app/home/alarm";
    public static final String TOPIC_MESH = "app/home/mesh";
    public static final String TOPIC_ACK = "home/ack";
    public static final String THINGSBOARD_GET_PERIOD_URL ="https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/DEVICE_ID/values/timeseries?keys=KEY&useStrictDataTypes=true";
    public static final String THINGSBOARD_GET_ACTIVE_URL = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/1c2f2e10-bcc4-11ee-a441-01b9c6b9277c/values/attributes/SERVER_SCOPE?keys=active";
    public static final String THINGSBOARD_GET_MESH_URL = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/DEVICE_ID/values/attributes/SHARED_SCOPE";
    public static final String THINGSBOARD_GET_ALARM_URL = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/DEVICE_ID/values/attributes/SHARED_SCOPE";
    public static final String THINGSBOARD_POST_ALARM_URL = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/DEVICE_ID/SHARED_SCOPE";
    public static final String THINGSBOARD_DELETE_AlARM_URL = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/DEVICE_ID/SHARED_SCOPE?keys=KEY";
    public static final String THINGSBOARD_DELETE_MESH_URL = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/DEVICE_ID/SHARED_SCOPE?keys=KEY";
//const THINGSBOARD_GET_UPDATE_MESH_URL = "http://thingsboard.cloud/api/v1/CbKAIXsZ7sD1ty2grTMO/attributes/updates?timeout=20000"
    public static final String THINGSBOARD_GET_UPDATE_MESH_URL = "https://thingsboard.cloud:443/api/v1/ACCESS_TOKEN/attributes/updates?timeout=40000";
    //public static final String THINGSBOARD_GET_UPDATE_AlARM_URL = "https://web-production-e849.up.railway.app/https://thingsboard.cloud:443/api/v1/ACCESS_TOKEN/attributes/updates?timeout=20000";
    public static final String THINGSBOARD_GET_ALL_KEYS_AlARM_URL = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/DEVICE_ID/keys/attributes/SHARED_SCOPE";
    public static final String THINGSBOARD_GET_UPDATE_AlARM_URL = "https://thingsboard.cloud:443/api/v1/ACCESS_TOKEN/attributes/updates?timeout=40000";
    public static final String THINGSBOARD_POST_AlARM_URL = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/DEVICE_ID/SHARED_SCOPE";
    public static final String THINGSBOARD_DELETE_API = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/DEVICE_ID/timeseries/delete?keys=FIELD&deleteAllDataForKeys=true&rewriteLatestIfDeleted=true";
    public static final String THINGSBOARD_POST_API = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/DEVICE_ID/timeseries/ANY?scope=ANY";
    public static final String THINGSBOARD_URL_LOGIN = "https://thingsboard.cloud:443/api/auth/login";
    public static final String THINGSBOARD_GET_LASTDATA_URL = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/DEVICE_ID/values/timeseries?useStrictDataTypes=true";
    public static final String THINGSBOARD_GET_TIMESERIES_URL = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/DEVICE_ID/values/timeseries?keys=FIELD&startTs=TIME_START&endTs=TIME_END&useStrictDataTypes=true";
    public static final ThingsBoardInfo[] THINGS_BOARD_INFOS = new ThingsBoardInfo[] {
            new ThingsBoardInfo("7ZvQBCNeQICxPQkSxlfh", "1c2f2e10-bcc4-11ee-a441-01b9c6b9277c"),
            new ThingsBoardInfo("wRYkjd8aLCnBejYhxPcz", "22781110-bcc4-11ee-862d-c1e0a53112a0"),
            new ThingsBoardInfo("DpX5Fc6ip4azNoyBhnnh", "a75fcf30-bcc4-11ee-a1be-053f95f9b401"),
            new ThingsBoardInfo("l4odG8CgyhSLW0CzsmsS", "ab700040-bcc4-11ee-a124-0d00bef77fcc"),
            new ThingsBoardInfo("eIpdTlv1I4ivvljJIrKd", "b4776700-bcc4-11ee-a441-01b9c6b9277c"),
    };
}
