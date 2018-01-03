package gov.nist.policyserver.translator.model.row;

import java.util.ArrayList;
import java.util.List;

public class CompositeRow {
    private List<SimpleRow> compositeRow;

    public CompositeRow(){
        compositeRow = new ArrayList<>();
    }

    public void addToRow(SimpleRow row){
        compositeRow.add(row);
    }

    public List<SimpleRow> getCompositeRow(){
        return compositeRow;
    }

    @Override
    public String toString(){
        String s = "";
        for(SimpleRow r : compositeRow){
            if(s.isEmpty()){
                s += r + "\t";
            }else {
                s += r + "\t";
            }
        }
        return s;
    }
}
