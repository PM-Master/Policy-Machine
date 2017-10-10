package gov.nist.policyserver.helpers;

import com.google.gson.*;
import gov.nist.policyserver.exceptions.InvalidPropertyException;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.Property;
import gov.nist.policyserver.model.graph.relationships.Association;
import gov.nist.policyserver.model.prohibitions.Prohibition;
import gov.nist.policyserver.model.prohibitions.ProhibitionRes;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubject;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class JsonHelper {

    public static Node getNodeFromJson(String json){
        return new Gson().fromJson(json, Node.class);
    }

    public static HashSet<String> getOpsFromJson(String json) {
        HashSet<String> ops = new HashSet<>();
        String[] opsArr = json.replaceAll("[\\[\\]\"]", "").split(",");
        if(!opsArr[0].isEmpty()){
            ops.addAll(Arrays.asList(opsArr));
        }
        return ops;
    }

    public static List<Long> toList(String json){
        List<Long> ids = new ArrayList<>();
        String[] idArr = json.replaceAll("[\\[\\]]", "").split(",");
        if(!idArr[0].isEmpty()){
            for(String id : idArr) {
                ids.add(Long.valueOf(id));
            }
        }
        return ids;
    }

    public static Association getAssociationFromJson(String json) {
        return new Gson().fromJson(json, Association.class);
    }

    public static String toJson(Object o, boolean pretty){
        String s = new Gson().toJson(o);
        Gson gson;
        if(pretty) {
            gson = new GsonBuilder().setPrettyPrinting().create();
        }else{
            gson = new GsonBuilder().create();
        }
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(s);
        return gson.toJson(je);
    }

    public static List<Property> getPropertiesFromJson(String json) {
        List<Property> props = new ArrayList<>();
        JsonElement je = new JsonParser().parse(json);
        JsonObject jo = je.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = jo.entrySet();

        for(Map.Entry<String, JsonElement> prop : entries){
            if(prop.getKey().equals("name") || prop.getKey().equals("type") || prop.getKey().equals("id")){
                continue;
            }
            try {
                Property property = new Property(prop.getKey(), StringUtils.strip(prop.getValue().toString(), "\""));
                props.add(property);
            }
            catch (InvalidPropertyException e) {
                e.printStackTrace();
            }
        }
        return props;
    }

    public static ProhibitionSubject getProhibitionSubject(String json) {
        return new Gson().fromJson(json, ProhibitionSubject.class);
    }

    public static Prohibition getProhibition(String json) {
        return new Gson().fromJson(json, Prohibition.class);
    }

    public static List<ProhibitionRes> getProhibitionResources(String json) {
        json = json.replaceAll("[\\[\\]]", "").replaceAll("\\},\\{", "}|{");
        String[] jsonArr = json.split("\\|");
        List<ProhibitionRes> drs = new ArrayList<>();
        for(String j : jsonArr){
            drs.add(new Gson().fromJson(j, ProhibitionRes.class));
        }
        return drs;
    }
}
