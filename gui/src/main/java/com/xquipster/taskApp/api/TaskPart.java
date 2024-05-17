package com.xquipster.taskApp.api;

public class TaskPart {
    private String title;
    private boolean done;

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return done;
    }

    public TaskPart(String title, boolean done) {
        this.title = title;
        this.done = done;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "  TaskPart {\n    Title: \"" + title + "\"\n    Done: \"" + (done ? 1 : 0) +"\"\n  }";
    }
    public static TaskPart parse(String s){
        if (s.startsWith("TaskPart")){
            int firstOpenBracket = -1;
            int lastCloseBracket = -1;
            for (int i = 0; i < s.toCharArray().length; i++){
                if (s.toCharArray()[i] == '{'){
                    if(firstOpenBracket == -1) firstOpenBracket = i;
                }else if (s.toCharArray()[i] == '}'){
                    lastCloseBracket = i;
                }
            }
            String body = s.substring(firstOpenBracket + 1, lastCloseBracket);
            String title = getLabel(body, "Title");
            String done = getLabel(body, "Done");
            if (!title.isEmpty() && !done.isEmpty()){
                try {
                    int bool = Integer.parseInt(done);
                    return new TaskPart(title, bool > 0);
                }catch (Exception ignored){}
            }
        }
        return null;
    }
    private static String getLabel(String body, String key){
        String[] args = body.split(key + ":");
        StringBuilder label = new StringBuilder();
        boolean bracket = false;
        if (args.length >= 2){
            for (int i = 0; i < args[1].length(); i++){
                if (bracket){
                    if (args[1].charAt(i) == '"'){
                        break;
                    }else{
                        label.append(args[1].charAt(i));
                    }
                }else{
                    if (args[1].charAt(i) == '"'){
                        bracket = true;
                    }
                }
            }
        }
        return label.toString();
    }
}
