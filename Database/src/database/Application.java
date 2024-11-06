package database;

import javax.swing.*;
import java.awt.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;

public class Application extends JFrame {

    private JTextField idField, moneyField, interestField;
    private JTextField firstNameField, lastNameField, ageField;
    private JComboBox<String> dayBox, monthBox, yearBox;
    private JComboBox<String> birthDayBox, birthMonthBox, birthYearBox;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public Application() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("work");
        collection = database.getCollection("OOP");

        setTitle("Show Detail of Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("ACCOUNT MONEY");
        JLabel subtitleLabel = new JLabel("Enter Data Account Money");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idField = new JTextField(8);
        moneyField = new JTextField(8);
        topPanel.add(new JLabel("ID:"));
        topPanel.add(idField);
        topPanel.add(new JLabel("MONEY:"));
        topPanel.add(moneyField);
        topPanel.add(new JLabel("BATH:"));

        JPanel interestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        interestField = new JTextField(15);
        interestPanel.add(new JLabel("ANNUAL INTEREST RATE:"));
        interestPanel.add(interestField);

        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = String.valueOf(i);
        }

        String[] months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

        String[] years = new String[100];
        for (int i = 0; i < 100; i++) {
            years[i] = String.valueOf(2024 - i);
        }

        JPanel openDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dayBox = new JComboBox<>(days);
        monthBox = new JComboBox<>(months);
        yearBox = new JComboBox<>(years);
        openDatePanel.add(new JLabel("DAY OPEN ACCOUNT:"));
        openDatePanel.add(dayBox);
        openDatePanel.add(monthBox);
        openDatePanel.add(yearBox);

        JPanel firstNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstNameField = new JTextField(20);
        firstNamePanel.add(new JLabel("FIRST NAME:"));
        firstNamePanel.add(firstNameField);

        JPanel lastNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lastNameField = new JTextField(20);
        lastNamePanel.add(new JLabel("LAST NAME:"));
        lastNamePanel.add(lastNameField);

        JPanel birthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        birthDayBox = new JComboBox<>(days);
        birthMonthBox = new JComboBox<>(months);
        birthYearBox = new JComboBox<>(years);
        birthPanel.add(new JLabel("BIRTH DAY:"));
        birthPanel.add(birthDayBox);
        birthPanel.add(birthMonthBox);
        birthPanel.add(birthYearBox);

        JPanel agePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ageField = new JTextField(5);
        agePanel.add(new JLabel("AGE:"));
        agePanel.add(ageField);
        agePanel.add(new JLabel("YEAR"));

        formPanel.add(topPanel);
        formPanel.add(interestPanel);
        formPanel.add(openDatePanel);
        formPanel.add(firstNamePanel);
        formPanel.add(lastNamePanel);
        formPanel.add(birthPanel);
        formPanel.add(agePanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("SAVE");
        JButton showButton = new JButton("SHOW");
        buttonPanel.add(saveButton);
        buttonPanel.add(showButton);

        saveButton.addActionListener(e -> {
            try {
                String id = idField.getText();
                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "ID field cannot be empty!");
                    return;
                }

                Document accountDoc = new Document()
                        .append("money", Double.valueOf(moneyField.getText()))
                        .append("interestRate", Double.valueOf(interestField.getText()))
                        .append("openDate", new Document()
                                .append("day", dayBox.getSelectedItem())
                                .append("month", monthBox.getSelectedItem())
                                .append("year", yearBox.getSelectedItem()))
                        .append("firstName", firstNameField.getText())
                        .append("lastName", lastNameField.getText())
                        .append("birthDate", new Document()
                                .append("day", birthDayBox.getSelectedItem())
                                .append("month", birthMonthBox.getSelectedItem())
                                .append("year", birthYearBox.getSelectedItem()))
                        .append("age", Integer.valueOf(ageField.getText()));

                Document existingDoc = collection.find(eq("_id", id)).first();

                if (existingDoc != null) {
                    collection.updateOne(
                            eq("_id", id),
                            new Document("$set", accountDoc)
                    );
                    JOptionPane.showMessageDialog(null, "Account updated successfully!");
                } else {
                    accountDoc.append("_id", id);
                    collection.insertOne(accountDoc);
                    JOptionPane.showMessageDialog(null, "Account saved successfully!");
                }

                clearFields();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Please ensure all numeric fields are filled correctly!",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Error saving data: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        showButton.addActionListener(e -> {
            try {
                String id = idField.getText();
                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter an ID to search");
                    return;
                }

                Document doc = collection.find(eq("_id", id)).first();
                if (doc != null) {
                    moneyField.setText(doc.getDouble("money").toString());
                    interestField.setText(doc.getDouble("interestRate").toString());

                    Document openDate = (Document) doc.get("openDate");
                    dayBox.setSelectedItem(openDate.getString("day"));
                    monthBox.setSelectedItem(openDate.getString("month"));
                    yearBox.setSelectedItem(openDate.getString("year"));

                    firstNameField.setText(doc.getString("firstName"));
                    lastNameField.setText(doc.getString("lastName"));

                    Document birthDate = (Document) doc.get("birthDate");
                    birthDayBox.setSelectedItem(birthDate.getString("day"));
                    birthMonthBox.setSelectedItem(birthDate.getString("month"));
                    birthYearBox.setSelectedItem(birthDate.getString("year"));

                    ageField.setText(doc.getInteger("age").toString());
                } else {
                    JOptionPane.showMessageDialog(null, "No account found with ID: " + id);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Error retrieving data: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (mongoClient != null) {
                    mongoClient.close();
                }
            }
        });

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        pack();
        setLocationRelativeTo(null);
    }

    private void clearFields() {
        idField.setText("");
        moneyField.setText("");
        interestField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        ageField.setText("");
        dayBox.setSelectedIndex(0);
        monthBox.setSelectedIndex(0);
        yearBox.setSelectedIndex(0);
        birthDayBox.setSelectedIndex(0);
        birthMonthBox.setSelectedIndex(0);
        birthYearBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        new Application().setVisible(true);
    }
}
