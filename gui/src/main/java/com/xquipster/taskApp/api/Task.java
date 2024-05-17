package com.xquipster.taskApp.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Task {
    private final String title;
    private final ArrayList<TaskPart> parts;
    private final boolean done;
    private final boolean open;

    public Task(String title, TaskPart... parts) {
        this.title = title;
        this.parts = new ArrayList<>();
        this.parts.addAll(Arrays.asList(parts));
        this.done = false;
        this.open = false;
    }
    public Task(String title, boolean open, TaskPart... parts) {
        this.title = title;
        this.parts = new ArrayList<>();
        this.parts.addAll(Arrays.asList(parts));
        this.done = false;
        this.open = open;
    }
    public Task(String title, boolean done){
        this.title = title;
        this.done = done;
        this.parts = new ArrayList<>();
        this.open = false;
    }
    public Task(String title, boolean open, boolean done){
        this.title = title;
        this.done = done;
        this.parts = new ArrayList<>();
        this.open = open;
    }
    public Task(String title, Task task){
        this.title = title;
        this.done = task.done;
        this.parts = new ArrayList<>(task.parts);
        this.open = task.open;
    }
    public Task(String title, boolean open, Task task){
        this.title = title;
        this.done = task.done;
        this.parts = new ArrayList<>(task.parts);
        this.open = open;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return done;
    }
    public boolean isOpen(){
        return open;
    }
    public ArrayList<TaskPart> getParts(){
        return (ArrayList<TaskPart>) parts.clone();
    }
    public int getProgress(){
        int i = 0;
        for (TaskPart part : getParts()){
            if (part.isDone()){
                i++;
            }
        }
        return Math.round(((float) (i / getParts().size())) * 100f);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Task {\n  Title: \"" + title + "\"\n  Open: \"" + (open ? 1 : 0) + "\"\n");
        if (parts.isEmpty()){
            s.append("  Done: \"").append(done ? 1 : 0).append("\"\n}");
            return s.toString();
        }
        for (TaskPart part : parts) s.append("\n").append(part.toString());
        s.append("\n}");
        return s.toString();
    }
    public static Task parse(String s){
        if (s.startsWith("Task") && !s.startsWith("TaskPart")){
            int firstOpenBracket = -1;
            ArrayList<Integer> secondOpenBrackets = new ArrayList<>();
            int lastCloseBracket = -1;
            for (int i = 0; i < s.toCharArray().length; i++){
                if (s.toCharArray()[i] == '{'){
                    if (firstOpenBracket == -1) {
                        firstOpenBracket = i;
                    }else{
                        secondOpenBrackets.add(i);
                    }
                }else if (s.toCharArray()[i] == '}'){
                    lastCloseBracket = i;
                }
            }
            String body = s.substring(firstOpenBracket + 1, lastCloseBracket);
            String title = getLabel(body, "Title");
            String open = getLabel(body, "Open");
            String done = getLabel(body, "Done");
            ArrayList<TaskPart> parts = new ArrayList<>();
            for (int i = 0; i < secondOpenBrackets.size(); i++){
                String str;
                if (secondOpenBrackets.size() > i + 1){
                    str = "TaskPart " + s.substring(secondOpenBrackets.get(i), s.indexOf("}", secondOpenBrackets.get(i)) + 1);
                }else{
                    str = "TaskPart " + s.substring(secondOpenBrackets.get(i));
                }
                TaskPart part = TaskPart.parse(str);
                if (part != null){
                    parts.add(part);
                }
            }
            if (!title.isEmpty() && !open.isEmpty()){
                try {
                    int i1 = Integer.parseInt(open);
                    if (!parts.isEmpty()){
                        return new Task(title, i1 > 0, parts.toArray(new TaskPart[]{}));
                    }else if (!done.isEmpty()){
                        int i = Integer.parseInt(done);
                        return new Task(title, i1 > 0, i > 0);
                    }
                }catch (Exception ignored){ }
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
