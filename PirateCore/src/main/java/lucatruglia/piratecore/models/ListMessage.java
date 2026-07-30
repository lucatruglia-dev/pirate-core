package lucatruglia.piratecore.models;

import java.util.List;

public class ListMessage {

    public String title;
    public List<Row> rows;
    public List<Button> buttons;

    public static class Row {
        public String key;
        public String value;

        public Row(String key, String value){
            this.key = key;
            this.value = value;
        }
    }

    public static class Button {
        public String text;
        public String command;

        public Button(String text, String command){
            this.text = text;
            this.command = command;
        }
    }

    public ListMessage(String title, List<Row> rows){
        this.title = title;
        this.rows = rows;
    }

    public ListMessage(String title, List<Row> rows, List<Button> buttons){
        this.title = title;
        this.rows = rows;
        this.buttons = buttons;
    }
}
