package com.xquipster.taskApp;

import com.xquipster.taskApp.api.Task;
import com.xquipster.taskApp.api.TaskPart;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager extends JFrame {
    private final DefaultTableModel taskTableModel;
    private final JTable taskTable;

    private final ArrayList<Task> tasks = new ArrayList<>();
    private final HashMap<Integer, Integer> tasksInTable = new HashMap<>();
    private File directory = null;
    public TaskManager() {
        try {
            File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            directory = jarFile.getParentFile();
        }catch (Exception e){
            System.exit(1);
        }
        setTitle("Task Manager");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        try {
            setIconImage(ImageIO.read(Objects.requireNonNull(getClass().getResource("/taskAppIcon.png"))));
        }catch (Exception ignored){}
        setLocationRelativeTo(null);

        // Table setup
        taskTableModel = new DefaultTableModel(new Object[]{"Task", "Progress"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        taskTable = new JTable(taskTableModel);
        JScrollPane scrollPane = new JScrollPane(taskTable);
        TableColumn column = taskTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(150);

        // Context menu setup
        JPopupMenu popupMenu = new JPopupMenu();
        JPopupMenu popupMenu1 = new JPopupMenu();
        JMenuItem addTaskMenuItem = new JMenuItem("Add");
        addTaskMenuItem.addActionListener(e -> addTask());
        popupMenu.add(addTaskMenuItem);
        JMenuItem renameMenuItem = new JMenuItem("Rename");
        renameMenuItem.addActionListener(e -> renameTask());
        popupMenu.add(renameMenuItem);
        JMenuItem addTaskMenuItem1 = new JMenuItem("Add");
        addTaskMenuItem1.addActionListener(e -> addTask());
        popupMenu1.add(addTaskMenuItem1);
        JMenuItem deleteTaskMenuItem = new JMenuItem("Delete");
        deleteTaskMenuItem.addActionListener(e -> deleteTask());
        popupMenu.add(deleteTaskMenuItem);
        taskTable.getColumnModel().getColumn(1).setCellRenderer((table, value, isSelected, hasFocus, row, column1) -> value instanceof Boolean ? new CheckBoxRenderer((Boolean) value) : value instanceof Integer ? new ProgressRenderer((int) value) : new DefaultTableCellRenderer());
        // Mouse listener for renaming task on double-click
        taskTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TaskManager.this.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        // Display table
        add(scrollPane, BorderLayout.CENTER);

        // Show context menu on right-click
        taskTable.setComponentPopupMenu(popupMenu);
        scrollPane.setComponentPopupMenu(popupMenu1);

        // Load tasks from file on startup
        loadTasks();
    }
    private void mouseClicked(MouseEvent e){
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (e.getClickCount() == 2){
                int row = taskTable.rowAtPoint(e.getPoint());
                int column = taskTable.columnAtPoint(e.getPoint());
                if (row != -1 && column == 0) {
                    toggleSubtasks(row);
                }
            }else{
                int row = taskTable.rowAtPoint(e.getPoint());
                int column = taskTable.columnAtPoint(e.getPoint());
                if (row != -1 && column == 1){
                    Object o = taskTableModel.getValueAt(row, column);
                    if (o instanceof Boolean){
                        toggleCheckbox(row);
                    }
                }
            }
        }
    }

    public void toggleCheckbox(int row) {
        int index = tasksInTable.getOrDefault(row, -1);
        if (index != -1){
            Task task = new Task(tasks.get(index).getTitle(), !tasks.get(index).isDone());
            tasks.set(index, task);
            saveTasks();
        }else{
            Task task1 = null;
            int taskIndex = -1;
            int subtask = -1;
            for (int i = row; i >= 0; i--){
                Object obj = taskTableModel.getValueAt(i, 0);
                if (obj instanceof String){
                    String s = (String) obj;
                    if (!s.startsWith("   ")){
                        taskIndex = tasksInTable.get(i);
                        task1 = tasks.get(taskIndex);
                        subtask = row - i - 1;
                        break;
                    }
                }
            }
            if (task1 != null){
                TaskPart part = task1.getParts().get(subtask);
                ArrayList<TaskPart> parts = task1.getParts();
                if (part != null){
                    TaskPart part1 = new TaskPart(part.getTitle(), !part.isDone());
                    parts.set(subtask,part1);
                    Task task = new Task(task1.getTitle(), task1.isOpen(), parts.toArray(new TaskPart[]{}));
                    tasks.set(taskIndex, task);
                    saveTasks();
                }
            }
        }
        loadTasks();
    }

    private void addTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
            String subtask = JOptionPane.showInputDialog(this, "Enter subtask:");
            if (subtask != null && !subtask.isEmpty()) {
                String selected = (String) taskTableModel.getValueAt(selectedRow, 0);
                int task = -1;
                if (selected.startsWith("   ")){
                    for (int i = selectedRow; i >= 0; i--){
                        Object o = taskTableModel.getValueAt(i, 0);
                        if (o instanceof String){
                            String s = (String) o;
                            if (!s.startsWith("   ")){
                                task = tasksInTable.get(i);
                                break;
                            }
                        }
                    }
                }else{
                    for (int i = 0; i < tasks.size(); i++){
                        Task task1 = tasks.get(i);
                        if (task1.getTitle().equalsIgnoreCase(selected)){
                            task = i;
                            break;
                        }
                    }
                }
                if (task != -1){
                    Task t = tasks.get(task);
                    ArrayList<TaskPart> parts = new ArrayList<>(t.getParts());
                    parts.add(new TaskPart(subtask, false));
                    tasks.set(task, new Task(t.getTitle(), t.isOpen(), parts.toArray(new TaskPart[]{})));
                    saveTasks();
                    loadTasks();
                }
            }
        }else{
            String task = JOptionPane.showInputDialog(this, "Enter task:");
            if (task != null && !task.isEmpty()) {
                tasks.add(new Task(task));
                saveTasks();
                loadTasks();
            }
        }
    }
    private void toggleSubtasks(int row) {
        String s = (String) taskTableModel.getValueAt(row, 0);
        if (s != null && !s.isEmpty()){
            boolean expanded = false;
            for (Task task : tasks) {
                if (task.getTitle().equals(s)) {
                    expanded = task.isOpen();
                    break;
                }
            }
            if (expanded) {
                collapseSubtasks(row);
            } else {
                expandSubtasks(row);
            }
            saveTasks();
            loadTasks();
        }
    }

    private void expandSubtasks(int row) {
        int index = tasksInTable.getOrDefault(row, -1);
        if (index != -1){
            Task task = tasks.get(index);
            tasks.set(index, new Task(task.getTitle(), true, task.getParts().toArray(new TaskPart[]{})));
        }
    }

    private void collapseSubtasks(int row) {
        int index = tasksInTable.getOrDefault(row, -1);
        if (index != -1){
            Task task = tasks.get(index);
            tasks.set(index, new Task(task.getTitle(), false, task.getParts().toArray(new TaskPart[]{})));
        }
    }

    private void deleteTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1 && selectedRow < taskTableModel.getRowCount()) {
            String task = (String) taskTableModel.getValueAt(selectedRow, 0);
            if (task.startsWith("   ")){
                task = task.substring(3);
                Task task1 = null;
                int taskIndex = -1;
                for (int i = selectedRow; i >= 0; i--){
                    Object o = taskTableModel.getValueAt(i, 0);
                    if (o instanceof String){
                        String s = (String) o;
                        if (!s.startsWith("   ")){
                            taskIndex = tasksInTable.get(i);
                            task1 = tasks.get(taskIndex);
                            break;
                        }
                    }
                }
                if (task1 != null){
                    ArrayList<TaskPart> parts = new ArrayList<>();
                    for (TaskPart part : task1.getParts()){
                        if (!Objects.equals(part.getTitle(), task)){
                            parts.add(part);
                        }
                    }
                    tasks.set(taskIndex, new Task(task1.getTitle(), task1.isOpen(), parts.toArray(new TaskPart[]{})));
                }
            }else{
                for (int i = 0; i < tasks.size(); i++){
                    Task task1 = tasks.get(i);
                    if (task1.getTitle().equalsIgnoreCase(task)){
                        tasks.remove(i);
                        break;
                    }
                }
            }
            saveTasks();
            loadTasks();
        }
    }
    private void renameTask(){
        int row = taskTable.getSelectedRow();
        if (row != -1 && row < taskTableModel.getRowCount()){
            String task = (String) taskTableModel.getValueAt(row, 0);
            boolean a = false;
            if (task.startsWith("   ")){
                task = task.substring(3);
                a = true;
            }
            String newTaskName = JOptionPane.showInputDialog(TaskManager.this, "Enter new task name:", task);
            if (newTaskName != null && !newTaskName.isEmpty()) {
                if (a){
                    Task task1 = null;
                    int taskIndex = -1;
                    for (int i = row; i >= 0; i--){
                        Object o = taskTableModel.getValueAt(i, 0);
                        if (o instanceof String){
                            String s = (String) o;
                            if (!s.startsWith("   ")){
                                taskIndex = tasksInTable.get(i);
                                task1 = tasks.get(taskIndex);
                                break;
                            }
                        }
                    }
                    if (task1 != null){
                        ArrayList<TaskPart> parts = new ArrayList<>();
                        for (TaskPart part : task1.getParts()){
                            if (Objects.equals(part.getTitle(), task)){
                                TaskPart part1 = new TaskPart(newTaskName, part.isDone());
                                parts.add(part1);
                            }else{
                                parts.add(part);
                            }
                        }
                        tasks.set(taskIndex, new Task(task1.getTitle(), task1.isOpen(), parts.toArray(new TaskPart[]{})));
                    }
                }else{
                    for (int i = 0; i < tasks.size(); i++){
                        Task task1 = tasks.get(i);
                        if (task1.getTitle().equalsIgnoreCase(task)){
                            tasks.set(i, new Task(newTaskName, task1));
                            break;
                        }
                    }
                }
                saveTasks();
                loadTasks();
            }
        }
    }
    private void loadTasks() {
        File file = new File(directory, "tasks");
        if (!file.exists()){
            try {
                file.createNewFile();
            }catch (Exception e){
                System.exit(1);
            }
            return;
        }
        tasks.clear();
        tasksInTable.clear();
        for (int i = 0; i < taskTableModel.getRowCount(); i++){
            taskTableModel.removeRow(i);
            i--;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(directory.getAbsolutePath() + File.separator + "tasks"))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String s = builder.toString();
            ArrayList<Integer> opens = new ArrayList<>();
            ArrayList<String> parts = new ArrayList<>();
            for (int i = 0; i < s.length(); i++){
                char c = s.charAt(i);
                if (c == '{'){
                    opens.add(i);
                }else if(c == '}'){
                    if (opens.size() > 0){
                        parts.add(s.substring(opens.get(opens.size() - 1), i + 1));
                        opens.remove(opens.size() - 1);
                    }
                }
            }
            if (parts.size() > 0){
                for (int p1 = 0; p1 < parts.size(); p1++){
                    String part = parts.get(p1);
                    for (int p2 = 0; p2 < parts.size(); p2++){
                        String part1 = parts.get(p2);
                        if (p1 == p2){
                            continue;
                        }
                        if (part1.contains(part)){
                            parts.remove(p1);
                            p1--;
                        }else if (part.contains(part1)){
                            parts.remove(p2);
                            p2--;
                        }
                    }
                }
                for (String part : parts) {
                    Task task = Task.parse("Task" + part);
                    if (task == null) {
                        return;
                    }
                    tasks.add(task);
                    if (task.getParts().isEmpty()) {
                        taskTableModel.addRow(new Object[]{task.getTitle(), task.isDone()});
                    } else {
                        int done = 0;
                        for (int sub = 0; sub < task.getParts().size(); sub++) {
                            if (task.getParts().get(sub).isDone()) {
                                done++;
                            }
                        }
                        taskTableModel.addRow(new Object[]{task.getTitle(), Math.round(((float) (done)) / (float) task.getParts().size() * 100)});
                    }
                    tasksInTable.put(taskTableModel.getRowCount() - 1, tasks.size() - 1);
                    if (task.isOpen()) {
                        for (TaskPart part1 : task.getParts()) {
                            taskTableModel.addRow(new Object[]{"   " + part1.getTitle(), part1.isDone()});
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(directory.getAbsolutePath() + File.separator + "tasks"))) {
            for (Task task : tasks) {
                writer.append(task.toString()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ProgressRenderer extends JProgressBar implements TableCellRenderer {
        public ProgressRenderer(int value) {
            super(0, 100);
            setValue(value);
            setStringPainted(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    private class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        public CheckBoxRenderer(boolean selected){
            super();
            setSelected(selected);
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setAlignmentX(CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (Boolean.TRUE.equals(taskTableModel.getValueAt(row, 1))) {
                if (value instanceof Boolean){
                    setSelected((Boolean) value);
                }
            } else {
                setSelected(false);
            }
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaskManager taskManager = new TaskManager();
            taskManager.setVisible(true);
        });
    }
}

