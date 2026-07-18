package lucatruglia.piratecore.models;

import java.util.List;

public class ListMessage {

    public String title;
    public List<Row> rows;

    public static class Row {
        public String key;
        public String value;

        public Row(String key, String value){
            this.key = key;
            this.value = value;
        }
    }

    public ListMessage(String title, List<Row> rows){
        this.title = title;
        this.rows = rows;
    }
}
